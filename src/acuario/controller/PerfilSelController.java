/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Perfil;
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
public class PerfilSelController extends ControllerAM {

    @FXML
    Button btnSeleccionar;
    @FXML
    Button btnNuevo;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn perfilNombreTC;
    @FXML
    TableColumn perfilIdentidadTC;
    @FXML
    TextField perfilId;
    @FXML
    TextField perfilNombre;
    @FXML
    TextField perfilDescripcion;
    @FXML
    TextField perfilBuscar;
    @FXML
    Label mensajeInformativo;

    private Stage dialogStage;
    private Perfil perfilSel;
    private boolean okClicked = false;

    private ObservableList<Perfil> listaPerfils = FXCollections.observableArrayList();
   
    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        inicializaTabla();
        datosTabla("A", "nombre");
        Perfil perfil = (Perfil) tabla.getSelectionModel().getSelectedItem();
        if (perfil != null) {
            datosPerfil(perfil);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Perfil>() {

            @Override
            public void changed(ObservableValue<? extends Perfil> observable,
                    Perfil oldValue, Perfil newValue) {
                mensajeInformativo.setText("");
                datosPerfil(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaPerfils = FXCollections.observableArrayList(Seguridad.findPerfilByNombre(filtro));
        } else {
            listaPerfils = FXCollections.observableArrayList(Seguridad.findPerfilById(0));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        perfilNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Perfil, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Perfil, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPerfilnombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        perfilIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("perfilid"));
    }


   private void datosPerfil(Perfil perfil) {
        if (perfil != null) {
            perfilId.setText(perfil.getPerfilid().toString());
            perfilNombre.setText(perfil.getPerfilnombre());           
            perfilDescripcion.setText(perfil.getPerfildescripcion());
        } else {
            perfilId.setText("");
            perfilNombre.setText("");
            perfilDescripcion.setText("");
        }

    }

   public void nuevoPerfil() {
        Perfil selectedPerfil = new Perfil();
        boolean okClicked = perfilEditar(selectedPerfil, "Crear");
        if (okClicked) {
            Integer registroid = Seguridad.createIdPerfil(selectedPerfil);
            selectedPerfil.setPerfilid(registroid);
            listaPerfils.add(selectedPerfil);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedPerfil.getPerfilnombre();
            datosTabla(filtro,"nombre");
            datosPerfil(selectedPerfil);
            GenerarAuditoria.grabaRegistro("Perfil", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void buscarPerfil() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (perfilBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(perfilBuscar.getText(), tipo);
    }

    public boolean perfilEditar(Perfil perfil, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/PerfilEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Perfil");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            PerfilEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerfil(perfil, modo);

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
        tabla.setItems(listaPerfils);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Perfil> getListaPerfils() {
        return listaPerfils;
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
    private void seleccionPerfil() {
        perfilSel = (Perfil) tabla.getSelectionModel().getSelectedItem();
        okClicked = true;
        dialogStage.close();
    }

    public Perfil getPerfilSel() {
        return perfilSel;
    }

    public void setPerfilSel(Perfil perfilSel) {
        this.perfilSel = perfilSel;
    }

    


}
