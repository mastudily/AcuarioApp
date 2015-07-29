/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cuentacontable;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Financiero;
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
import javafx.scene.control.CheckBox;
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
public class CuentacontableMntController extends ControllerAM {

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
    TableColumn cuentacontableNombreTC;
    @FXML
    TableColumn cuentacontableCodigoTC;
    @FXML
    TableColumn cuentacontableSignoTC;
    @FXML
    TextField cuentacontableId;
    @FXML
    TextField cuentacontableNombre;
    @FXML
    TextField cuentacontableDescripcion;
    @FXML
    TextField cuentacontableCodigo;
    @FXML
    TextField cuentacontablePadre;
    @FXML
    CheckBox cuentacontableMayor;
    @FXML
    TextField cuentacontableBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Cuentacontable> listaCuentacontable = FXCollections.observableArrayList();
    private Integer empresaid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        datosTabla("A", "nombre");
        Cuentacontable cuentacontable = (Cuentacontable) tabla.getSelectionModel().getSelectedItem();
        if (cuentacontable != null) {
            datosCuentacontable(cuentacontable);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cuentacontable>() {

            @Override
            public void changed(ObservableValue<? extends Cuentacontable> observable,
                    Cuentacontable oldValue, Cuentacontable newValue) {
                mensajeInformativo.setText("");
                datosCuentacontable(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaCuentacontable = FXCollections.observableArrayList(Financiero.findCuentacontableByNombre(filtro, empresaid));
        } else {
            listaCuentacontable = FXCollections.observableArrayList(Financiero.findCuentacontableByCodigo(filtro, empresaid));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        cuentacontableNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Cuentacontable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Cuentacontable, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getCuentacontablenombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        cuentacontableCodigoTC.setCellValueFactory(new PropertyValueFactory<>("cuentacontablecodigo"));
        cuentacontableSignoTC.setCellValueFactory(new PropertyValueFactory<>("cuentacontablesigno"));
    }

    private void datosCuentacontable(Cuentacontable cuentacontable) {
        if (cuentacontable != null) {
            cuentacontableNombre.setText(cuentacontable.getCuentacontablenombre());
            cuentacontableDescripcion.setText(cuentacontable.getCuentacontabledescripcion());
            cuentacontableCodigo.setText(cuentacontable.getCuentacontablecodigo());
            Cuentacontable padre = Financiero.findCuentacontable(cuentacontable.getCuentacontablepadreid());
            cuentacontablePadre.setText("");
            if (padre != null) {
                cuentacontablePadre.setText(padre.getCuentacontablenombre());
            }
            cuentacontableMayor.setSelected(cuentacontable.isCuentacontablemayor());
        } else {
            cuentacontableNombre.setText("");
            cuentacontableDescripcion.setText("");
            cuentacontableCodigo.setText("");
            cuentacontablePadre.setText("");
        }

    }

    public void nuevaCuentacontable() {
        Cuentacontable selectedCuentacontable = new Cuentacontable();
        boolean okClicked = cuentacontableEditar(selectedCuentacontable, "Crear");
        if (okClicked) {
            Integer registroid = Financiero.createIdCuentacontable(selectedCuentacontable);
            selectedCuentacontable.setCuentacontableid(registroid);
            listaCuentacontable.add(selectedCuentacontable);
            String filtro = selectedCuentacontable.getCuentacontablecodigo();
            datosTabla(filtro, "codigo");
            datosCuentacontable(selectedCuentacontable);
            GenerarAuditoria.grabaRegistro("Cuentacontable", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarCuentacontable() {
        Cuentacontable selectedCuentacontable = (Cuentacontable) tabla.getSelectionModel().getSelectedItem();
        if (selectedCuentacontable != null) {
            boolean okClicked = cuentacontableEditar(selectedCuentacontable, "Editar");
            if (okClicked) {
                Integer registroid = selectedCuentacontable.getCuentacontableid();
                Financiero.editCuentacontable(selectedCuentacontable);
                String filtro = selectedCuentacontable.getCuentacontablenombre();
                datosTabla(filtro, "nombre");
                datosCuentacontable(selectedCuentacontable);
                GenerarAuditoria.grabaRegistro("Cuentacontable", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }

        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarCuentacontable() {
        Cuentacontable selectedCuentacontable = (Cuentacontable) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedCuentacontable.getCuentacontableestado())) {
            try {
                if (confirmaEliminacion()) {
                    Financiero.removeCuentacontable(selectedCuentacontable);
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

    public void buscarCuentacontable() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (cuentacontableBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(cuentacontableBuscar.getText(), tipo);
    }

    public boolean cuentacontableEditar(Cuentacontable cuentacontable, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/CuentacontableEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Cuenta contable");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            CuentacontableEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCuentacontable(cuentacontable, modo);
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
        tabla.setItems(listaCuentacontable);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Cuentacontable> getListaCuentacontables() {
        return listaCuentacontable;
    }

}
