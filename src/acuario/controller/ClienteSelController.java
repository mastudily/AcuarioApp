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
import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import javafx.scene.image.Image;
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
public class ClienteSelController extends ControllerAM {

    @FXML
    Button btnSeleccionar;
    @FXML
    Button btnNuevo;
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
    TextField clienteEdad;
    @FXML
    TextField personaBuscar;
    @FXML
    Label nombreImagen;
    @FXML
    ImageView imagenImagen;
    @FXML
    Label mensajeInformativo;

    private Stage dialogStage;
    private Cliente clienteSel;
    private boolean okClicked = false;
    
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
        mensajeInformativo.setText("");
    }

    public ObservableList<Cliente> getListaClientes() {
        return listaClientes;
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
    private void seleccionCliente() {
        clienteSel = (Cliente) tabla.getSelectionModel().getSelectedItem();
        okClicked = true;
        dialogStage.close();
    }

    public Cliente getClienteSel() {
        return clienteSel;
    }

    public void setClienteSel(Cliente clienteSel) {
        this.clienteSel = clienteSel;
    }

    
}
