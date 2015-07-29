/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Constante;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Mantenimiento;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class ConstanteMntController extends ControllerAM {

    @FXML
    Button btnNuevo;
    @FXML
    Button btnModificar;
    @FXML
    Button btnEliminar;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn constanteCodigoTC;
    @FXML
    TableColumn constanteAbreviaturaTC;
    @FXML
    TableColumn constanteTipoTC;
    @FXML
    TextField constanteCodigo;
    @FXML
    TextField constanteTipo;
    @FXML
    TextField constanteDescripcion;
    @FXML
    TextField constanteAbreviatura;
    @FXML
    TextField constanteValor;
    @FXML
    TextField constanteBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Constante> listaConstante = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        datosTabla("A");
        Constante constante = (Constante) tabla.getSelectionModel().getSelectedItem();
        if (constante != null) {
            datosConstante(constante);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Constante>() {

            @Override
            public void changed(ObservableValue<? extends Constante> observable,
                    Constante oldValue, Constante newValue) {
                mensajeInformativo.setText("");
                datosConstante(newValue);
            }
        });
    }

    public void datosTabla(String filtro) {
        listaConstante = FXCollections.observableArrayList(Mantenimiento.findConstanteByCodigoMnt(filtro));
        actualizarTabla();
    }

    public void inicializaTabla() {
        constanteCodigoTC.setCellValueFactory(new PropertyValueFactory<>("constantecodigo"));
        constanteAbreviaturaTC.setCellValueFactory(new PropertyValueFactory<>("constanteabreviatura"));
        constanteTipoTC.setCellValueFactory(new PropertyValueFactory<>("constantetipo"));
    }

    private void datosConstante(Constante constante) {
        if (constante != null) {
            constanteCodigo.setText(constante.getConstantecodigo());
            constanteTipo.setText(constante.getConstantetipo());
            constanteDescripcion.setText(constante.getConstantedescripcion());
            constanteAbreviatura.setText(constante.getConstanteabreviatura());
            if (constante.getConstantevalor() != null) {
                constanteValor.setText(constante.getConstantevalor().toPlainString());
            }
        } else {
            constanteCodigo.setText("");
            constanteTipo.setText("");
            constanteDescripcion.setText("");
            constanteAbreviatura.setText("");
            constanteValor.setText("");
        }

    }

    public void nuevoConstante() {
        Constante selectedConstante = new Constante();
        boolean okClicked = constanteEditar(selectedConstante, "Crear");
        if (okClicked) {
            Integer registroid = Mantenimiento.createIdConstante(selectedConstante);
            selectedConstante.setConstanteid(registroid);
            listaConstante.add(selectedConstante);
            String filtro = selectedConstante.getConstantecodigo();
            datosTabla(filtro);
            datosConstante(selectedConstante);
            GenerarAuditoria.grabaRegistro("Constante", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarConstante() {
        Constante selectedConstante = (Constante) tabla.getSelectionModel().getSelectedItem();
        if (selectedConstante != null) {
            boolean okClicked = constanteEditar(selectedConstante, "Editar");
            if (okClicked) {
                Integer registroid = selectedConstante.getConstanteid();
                Mantenimiento.editConstante(selectedConstante);
                String filtro = selectedConstante.getConstantecodigo();
                datosTabla(filtro);
                datosConstante(selectedConstante);
                GenerarAuditoria.grabaRegistro("Constante", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }

        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarConstante() {
        Constante selectedConstante = (Constante) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedConstante.getConstanteestado())) {
            try {
                if (confirmaEliminacion()) {
                    Mantenimiento.removeConstante(selectedConstante);
                    tabla.getItems().remove(selectedIndex);
                    mensajeInformativo.setText("Datos eliminados satisfactoriamente...");
                }
            } catch (Exception e) {
                Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                        .message("Registro relacionado no se puede eliminar.").showError();
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla o registro no puede ser eliminado...").showInformation();
        }
    }

    private boolean confirmaEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("");
        alert.setContentText("Esta seguro de eliminar el registro?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void buscarConstante() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (constanteBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(constanteBuscar.getText());
    }

    public boolean constanteEditar(Constante constante, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/ConstanteEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Constante");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            ConstanteEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setConstante(constante, modo);
            controller.setApp(getApp());
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
        tabla.setItems(listaConstante);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Constante> getListaConstantes() {
        return listaConstante;
    }

}
