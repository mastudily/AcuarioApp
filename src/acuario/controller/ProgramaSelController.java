/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Programa;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Seguridad;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class ProgramaSelController extends ControllerAM {

    @FXML
    Button btnSeleccionar;
    @FXML
    Button btnNuevo;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn programaNombreTC;
    @FXML
    TableColumn programaIdentidadTC;
    @FXML
    TableColumn programaTipoTC;     
    @FXML
    TextField programaId;
    @FXML
    TextField programaNombre;
    @FXML
    TextField programaDescripcion;
    @FXML
    TextField programaAbreviatura;
    @FXML
    TextField programaPadre; 
    @FXML
    TextField programaBuscar;
    @FXML
    Label nombreImagen;
    @FXML
    ImageView imagenImagen;
    @FXML
    Label mensajeInformativo;

    private Stage dialogStage;
    private Programa programaSel;
    private boolean okClicked = false;

    private ObservableList<Programa> listaProgramas = FXCollections.observableArrayList();
   
    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        inicializaTabla();
        datosTabla("A", "nombre");
        Programa programa = (Programa) tabla.getSelectionModel().getSelectedItem();
        if (programa != null) {
            datosPrograma(programa);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Programa>() {

            @Override
            public void changed(ObservableValue<? extends Programa> observable,
                    Programa oldValue, Programa newValue) {
                mensajeInformativo.setText("");
                datosPrograma(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaProgramas = FXCollections.observableArrayList(Seguridad.findProgramaByNombre(filtro));
        } else {
            listaProgramas = FXCollections.observableArrayList(Seguridad.findProgramaById(0));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        programaNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Programa, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Programa, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getProgramanombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        programaIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("programaid"));
        programaTipoTC.setCellValueFactory(new PropertyValueFactory<>("programatipo"));
    }


   private void datosPrograma(Programa programa) {
        if (programa != null) {
            programaId.setText(programa.getProgramaid().toString());
            programaNombre.setText(programa.getProgramanombre());           
            programaDescripcion.setText(programa.getProgramadescripcion());
            programaAbreviatura.setText(programa.getProgramaabreviatura());
            Programa padre = Seguridad.findPrograma(programa.getProgramapadreid());
            programaPadre.setText("");
            if (padre!=null) {
                programaPadre.setText(padre.getProgramanombre());
            }
        } else {
            programaId.setText("");
            programaNombre.setText("");
            programaDescripcion.setText("");
            programaAbreviatura.setText("");
            programaPadre.setText("");
        }

    }

   public void nuevoPrograma() {
        Programa selectedPrograma = new Programa();
        boolean okClicked = programaEditar(selectedPrograma, "Crear");
        if (okClicked) {
            Integer registroid = Seguridad.createIdPrograma(selectedPrograma);
            selectedPrograma.setProgramaid(registroid);
            listaProgramas.add(selectedPrograma);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedPrograma.getProgramanombre();
            datosTabla(filtro,"nombre");
            datosPrograma(selectedPrograma);
            GenerarAuditoria.grabaRegistro("Programa", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void buscarPrograma() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (programaBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(programaBuscar.getText(), tipo);
    }

    public boolean programaEditar(Programa programa, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/ProgramaEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Programa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            ProgramaEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPrograma(programa, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaProgramas);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Programa> getListaProgramas() {
        return listaProgramas;
    }
    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void seleccionPrograma() {
        programaSel = (Programa) tabla.getSelectionModel().getSelectedItem();
        okClicked = true;
        dialogStage.close();
    }

    public Programa getProgramaSel() {
        return programaSel;
    }

    public void setProgramaSel(Programa programaSel) {
        this.programaSel = programaSel;
    }

    


}
