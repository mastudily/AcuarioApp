/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cliente;
import acuario.services.Persona;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Mantenimiento;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class ClienteMntController extends ControllerAM {

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
    TableColumn personaNombresTC;
    @FXML
    TableColumn personaIdentidadTC;
    @FXML
    TextField personaIdentidad;
    @FXML
    TextField personaNombres;
    @FXML
    TextField personaApellidos;
    @FXML
    TextField personaCorreo;
    @FXML
    TextField personaBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private File file;
    private String url;
    private Persona persona;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        url = getApp().getUrl();
        datosTabla("A", "nombre");
        Cliente cliente = (Cliente) tabla.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            persona = cliente.getPersonaid();
            datosCliente(cliente);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cliente>() {

            @Override
            public void changed(ObservableValue<? extends Cliente> observable,
                    Cliente oldValue, Cliente newValue) {
                mensajeInformativo.setText("");
                datosCliente(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaClientes = FXCollections.observableArrayList(Mantenimiento.findClienteByNombre(filtro));
        } else {
            listaClientes = FXCollections.observableArrayList(Mantenimiento.findClienteByIdentidad(filtro));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        personaNombresTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Cliente, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Cliente, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPersonaid().getPersonaapellidos() + " " + p.getValue().getPersonaid().getPersonanombres());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        personaIdentidadTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Cliente, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Cliente, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPersonaid().getPersonarut());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });

//        personaIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("personarut"));
    }

    private void datosCliente(Cliente cliente) {
        if (cliente != null) {
            persona = cliente.getPersonaid();
            personaIdentidad.setText(persona.getPersonarut());
            personaNombres.setText(persona.getPersonanombres());
            personaApellidos.setText(persona.getPersonaapellidos());
            personaCorreo.setText(persona.getPersonacorreo());
            //Date fechoy = new Date();
            //Date fecnac = cliente.getClientefechanacimiento().toGregorianCalendar().getTime();
            //Long edad = (fechoy.getTime() - fecnac.getTime())/(60 * 60 * 24 * 1000* 30 * 360);           
            //Integer edad = fechoy.getYear() - fecnac.getYear();
            //clienteEdad.setText(edad.toString());
        } else {
            personaIdentidad.setText("");
            personaNombres.setText("");
            personaApellidos.setText("");
            personaCorreo.setText("");
        }

    }

    public void nuevoCliente() {
        mensajeInformativo.setText("");
        Cliente selectedCliente = new Cliente();
        boolean okClicked = clienteEditar(selectedCliente, "Crear");
        if (okClicked) {
            Integer registroid = Mantenimiento.createIdCliente(selectedCliente);
            selectedCliente.setClienteid(registroid);
            listaClientes.add(selectedCliente);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedCliente.getPersonaid().getPersonaapellidos() + " " + selectedCliente.getPersonaid().getPersonanombres();
            datosTabla(filtro, "nombre");
            datosCliente(selectedCliente);
            GenerarAuditoria.grabaRegistro("Cliente", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarCliente() {
        mensajeInformativo.setText("");
        Cliente selectedCliente = (Cliente) tabla.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            //String foto = selectedCliente.getPersonaid().getPersonafoto();
            boolean okClicked = clienteEditar(selectedCliente, "Editar");
            if (okClicked) {
                Integer registroid = selectedCliente.getClienteid();
                Mantenimiento.editCliente(selectedCliente);
                String filtro = selectedCliente.getPersonaid().getPersonaapellidos() + " " + selectedCliente.getPersonaid().getPersonanombres();
                datosTabla(filtro, "nombre");
                datosCliente(selectedCliente);
                GenerarAuditoria.grabaRegistro("Cliente", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarCliente() {
        mensajeInformativo.setText("");
        Cliente selectedCliente = (Cliente) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedCliente.getClienteestado())) {
            try {
                if (confirmaEliminacion()) {
                    Mantenimiento.removeCliente(selectedCliente);
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

    public void buscarCliente() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (personaBuscar.getText().matches(regex)) {
            tipo = "identidad";
        }
        datosTabla(personaBuscar.getText(), tipo);
    }

    public boolean clienteEditar(Cliente cliente, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/ClienteEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Cliente");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            ClienteEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setCliente(cliente, modo);

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
        tabla.setItems(listaClientes);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
    }

    public ObservableList<Cliente> getListaClientes() {
        return listaClientes;
    }

}
