/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Formapago;
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
import javafx.scene.control.Alert.AlertType;
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
public class FormapagoMntController extends ControllerAM {

    @FXML
    Button btnNuevo;
    @FXML
    Button btnModificar;
    @FXML
    Button btnEliminar;
    @FXML
    Button btnFormapago;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn formapagoNombreTC;
    @FXML
    TableColumn formapagoIdTC;
    @FXML
    TextField formapagoId;
    @FXML
    TextField formapagoNombre;
    @FXML
    TextField formapagoDescripcion;
    @FXML
    TextField formapagoCtaCbe;
    @FXML
    TextField formapagoConCtaCbe;
    @FXML
    TextField formapagoBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Formapago> listaFormapago = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        datosTabla("A", "nombre");
        Formapago formapago = (Formapago) tabla.getSelectionModel().getSelectedItem();
        if (formapago != null) {
            datosFormapago(formapago);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Formapago>() {

            @Override
            public void changed(ObservableValue<? extends Formapago> observable,
                    Formapago oldValue, Formapago newValue) {
                mensajeInformativo.setText("");
                datosFormapago(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaFormapago = FXCollections.observableArrayList(Mantenimiento.findFormapagoByNombre(filtro));
        } else {
            listaFormapago = FXCollections.observableArrayList(Mantenimiento.findFormapagoById(0));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        formapagoIdTC.setCellValueFactory(new PropertyValueFactory<>("formapagoid"));
        formapagoNombreTC.setCellValueFactory(new PropertyValueFactory<>("formapagonombre"));
    }

    private void datosFormapago(Formapago formapago) {
        if (formapago != null) {
            formapagoId.setText(formapago.getFormapagoid().toString());
            formapagoNombre.setText(formapago.getFormapagonombre());
            formapagoDescripcion.setText(formapago.getFormapagodescripcion());
            formapagoCtaCbe.setText(formapago.getFormapagoctacble().getCuentacontabledescripcion());
            formapagoConCtaCbe.setText(formapago.getFormapagoconctacble().getCuentacontabledescripcion());
        } else {
            formapagoId.setText("");
            formapagoNombre.setText("");
            formapagoDescripcion.setText("");
            formapagoCtaCbe.setText("");
            formapagoConCtaCbe.setText("");            
        }

    }

    public void nuevoFormapago() {
        Formapago selectedFormapago = new Formapago();
        boolean okClicked = formapagoEditar(selectedFormapago, "Crear");
        if (okClicked) {
            Integer registroid = Mantenimiento.createIdFormapago(selectedFormapago);
            selectedFormapago.setFormapagoid(registroid);
            listaFormapago.add(selectedFormapago);
            String filtro = selectedFormapago.getFormapagonombre();
            datosTabla(filtro, "nombre");
            datosFormapago(selectedFormapago);
            GenerarAuditoria.grabaRegistro("Formapago", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarFormapago() {
        Formapago selectedFormapago = (Formapago) tabla.getSelectionModel().getSelectedItem();
        if (selectedFormapago != null && "PASIVO".equals(selectedFormapago.getFormapagoestado())) {
            boolean okClicked = formapagoEditar(selectedFormapago, "Editar");
            if (okClicked) {
                Integer registroid = selectedFormapago.getFormapagoid();
                Mantenimiento.editFormapago(selectedFormapago);
                String filtro = selectedFormapago.getFormapagonombre();
                datosTabla(filtro, "nombre");
                datosFormapago(selectedFormapago);
                GenerarAuditoria.grabaRegistro("Formapago", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }

        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla o registro no puede ser modificado...").showInformation();
        }
    }

    public void eliminarFormapago() {
        Formapago selectedFormapago = (Formapago) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedFormapago.getFormapagoestado())) {
            try {
                if (confirmaEliminacion()) {
                    Mantenimiento.removeFormapago(selectedFormapago);
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
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("");
        alert.setContentText("Esta seguro de eliminar el registro?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void buscarFormapago() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (formapagoBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(formapagoBuscar.getText(), tipo);
    }

    public boolean formapagoEditar(Formapago formapago, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/FormapagoEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Forma pago");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            FormapagoEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            controller.setApp(getApp());
            controller.Inicializar();
            controller.setFormapago(formapago, modo);

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
        tabla.setItems(listaFormapago);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Formapago> getListaFormapagos() {
        return listaFormapago;
    }

}
