/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Persona;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Mantenimiento;
import java.io.ByteArrayInputStream;
import java.io.File;
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
public class PersonaMntController extends ControllerAM {

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

    private ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();
    private File file;
    private String url;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        url = getApp().getUrl();
        datosTabla("A", "nombre");
        Persona persona = (Persona) tabla.getSelectionModel().getSelectedItem();
        datosPersona(persona);
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Persona>() {

            @Override
            public void changed(ObservableValue<? extends Persona> observable,
                    Persona oldValue, Persona newValue) {
                mensajeInformativo.setText("");
                datosPersona(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaPersonas = FXCollections.observableArrayList(Mantenimiento.findPersonaByNombre(filtro));
        } else {
            listaPersonas = FXCollections.observableArrayList(Mantenimiento.findPersonaByIdentidad(filtro));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        personaNombresTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Persona, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Persona, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPersonaapellidos() + " " + p.getValue().getPersonanombres());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        personaIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("personarut"));
    }

    private void datosPersona(Persona persona) {
        if (persona != null) {
            personaIdentidad.setText(persona.getPersonarut());
            personaNombres.setText(persona.getPersonanombres());
            personaApellidos.setText(persona.getPersonaapellidos());
            personaCorreo.setText(persona.getPersonacorreo());
        } else {
            personaIdentidad.setText("");
            personaNombres.setText("");
            personaApellidos.setText("");
            personaCorreo.setText("");
        }

    }

    public void nuevaPersona() {
        Persona selectedPerson = new Persona();
        boolean okClicked = personaEditar(selectedPerson, "Crear");
        if (okClicked) {
            /*if (selectedPerson.getPersonafoto() != null && selectedPerson.getPersonafoto().length() != 0) {
             String foto = Util.uploadFile(selectedPerson.getPersonafoto(), "Persona", url);
             foto = foto.substring(foto.lastIndexOf("/") + 1);
             selectedPerson.setPersonafoto(foto);
             }*/
            Integer registroid = Mantenimiento.createIdPersona(selectedPerson);
            selectedPerson.setPersonaid(registroid);
            listaPersonas.add(selectedPerson);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedPerson.getPersonaapellidos() + " " + selectedPerson.getPersonanombres();
            datosTabla(filtro, "nombre");
            datosPersona(selectedPerson);
            GenerarAuditoria.grabaRegistro("Persona", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarPersona() {
        Persona selectedPerson = (Persona) tabla.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            //String foto = selectedPerson.getPersonafoto();
            boolean okClicked = personaEditar(selectedPerson, "Editar");
            if (okClicked) {
                Integer registroid = selectedPerson.getPersonaid();
                Mantenimiento.editPersona(selectedPerson);
                String filtro = selectedPerson.getPersonaapellidos() + " " + selectedPerson.getPersonanombres();
                datosTabla(filtro, "nombre");
                datosPersona(selectedPerson);
                GenerarAuditoria.grabaRegistro("Persona", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }

        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarPersona() {
        Persona selectedPerson = (Persona) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedPerson.getPersonaestado())) {
            try {
                if (confirmaEliminacion()) {
                    Mantenimiento.removePersona(selectedPerson);
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

    public void buscarPersona() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (personaBuscar.getText().matches(regex)) {
            tipo = "identidad";
        }
        datosTabla(personaBuscar.getText(), tipo);
    }

    public boolean personaEditar(Persona persona, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/PersonaEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Persona");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            PersonaEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setPersona(persona, modo);

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
        tabla.setItems(listaPersonas);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Persona> getListaPersonas() {
        return listaPersonas;
    }

}
