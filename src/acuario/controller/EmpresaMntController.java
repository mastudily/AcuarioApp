/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Empresa;
import acuario.services.Persona;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Mantenimiento;
import java.io.IOException;
import java.util.Optional;
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
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class EmpresaMntController extends ControllerAM {

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
    TableColumn empresaNombreTC;
    @FXML
    TableColumn empresaIdentidadTC;
    @FXML
    TextField personaIdentidad;
    @FXML
    TextField personaNombres;
    @FXML
    TextField empresaRUT;
    @FXML
    TextField empresaNombre;
    @FXML
    TextField empresaDescripcion;
    @FXML
    TextField empresaTelefono;
    @FXML
    TextField empresaBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Empresa> listaEmpresas = FXCollections.observableArrayList();
    private Persona persona;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        datosTabla("A", "nombre");
        Empresa empresa = (Empresa) tabla.getSelectionModel().getSelectedItem();
        if (empresa != null) {
            persona = empresa.getPersonaid();
            datosEmpresa(empresa);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Empresa>() {

            @Override
            public void changed(ObservableValue<? extends Empresa> observable,
                    Empresa oldValue, Empresa newValue) {
                mensajeInformativo.setText("");
                datosEmpresa(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaEmpresas = FXCollections.observableArrayList(Mantenimiento.findEmpresaByNombre(filtro));
        } else {
            listaEmpresas = FXCollections.observableArrayList(Mantenimiento.findEmpresaByIdentidad(filtro));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        empresaNombreTC.setCellValueFactory(new PropertyValueFactory<>("empresanombre"));
        empresaIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("empresarut"));
    }

    private void datosEmpresa(Empresa empresa) {
        if (empresa != null) {
            empresaNombre.setText(empresa.getEmpresanombre());
            empresaRUT.setText(empresa.getEmpresarut());
            empresaDescripcion.setText(empresa.getEmpresadescripcion());
            empresaTelefono.setText(empresa.getEmpresatelefono());
            persona = empresa.getPersonaid();
            personaIdentidad.setText(persona.getPersonarut());
            personaNombres.setText(persona.getPersonaapellidos() + " " + persona.getPersonanombres());
        } else {
            empresaNombre.setText("");
            empresaRUT.setText("");
            empresaDescripcion.setText("");
            empresaTelefono.setText("");
            personaIdentidad.setText("");
            personaNombres.setText("");
        }

    }

    public void nuevoEmpresa() {
        Empresa selectedEmpresa = new Empresa();
        boolean okClicked = empresaEditar(selectedEmpresa, "Crear");
        if (okClicked) {
            Integer registroid = Mantenimiento.createIdEmpresa(selectedEmpresa);
            selectedEmpresa.setEmpresaid(registroid);
            listaEmpresas.add(selectedEmpresa);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedEmpresa.getEmpresanombre();
            datosTabla(filtro, "nombre");
            datosEmpresa(selectedEmpresa);
            GenerarAuditoria.grabaRegistro("Empresa", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarEmpresa() {
        Empresa selectedEmpresa = (Empresa) tabla.getSelectionModel().getSelectedItem();
        if (selectedEmpresa != null) {
            //String foto = selectedEmpresa.getPersonaid().getPersonafoto();
            boolean okClicked = empresaEditar(selectedEmpresa, "Editar");
            if (okClicked) {
                Integer registroid = selectedEmpresa.getEmpresaid();
                Mantenimiento.editEmpresa(selectedEmpresa);
                String filtro = selectedEmpresa.getEmpresanombre();
                datosTabla(filtro, "nombre");
                datosEmpresa(selectedEmpresa);
                GenerarAuditoria.grabaRegistro("Empresa", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarEmpresa() {
        Empresa selectedEmpresa = (Empresa) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedEmpresa.getEmpresaestado())) {
            try {
                if (confirmaEliminacion()) {
                    Mantenimiento.removeEmpresa(selectedEmpresa);
                    tabla.getItems().remove(selectedIndex);
                    mensajeInformativo.setText("Datos eliminados satisfactoriamente...");
                }
            } catch (Exception e) {
                Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                        .message("Registro relacionado no se puede eliminar o registro no puede ser eliminado...").showError();
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
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

    public void buscarEmpresa() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (empresaBuscar.getText().matches(regex)) {
            tipo = "identidad";
        }
        datosTabla(empresaBuscar.getText(), tipo);
    }

    public boolean empresaEditar(Empresa empresa, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/EmpresaEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Empresa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            EmpresaEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setEmpresa(empresa, modo);

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
        tabla.setItems(listaEmpresas);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Empresa> getListaEmpresas() {
        return listaEmpresas;
    }

}
