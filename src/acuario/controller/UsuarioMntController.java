/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Usuario;
import acuario.services.Persona;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Seguridad;
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
public class UsuarioMntController extends ControllerAM {

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
    TextField usuarioIdentidad;
    @FXML
    TextField usuarioBuscar;
    @FXML
    Label nombreImagen;
    @FXML
    ImageView imagenImagen;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
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
        Usuario usuario = (Usuario) tabla.getSelectionModel().getSelectedItem();
        if (usuario != null) {
            persona = usuario.getPersonaid();
            datosUsuario(usuario);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Usuario>() {

            @Override
            public void changed(ObservableValue<? extends Usuario> observable,
                    Usuario oldValue, Usuario newValue) {
                mensajeInformativo.setText("");
                datosUsuario(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaUsuarios = FXCollections.observableArrayList(Seguridad.findUsuarioByNombre(filtro));
        } else {
            listaUsuarios = FXCollections.observableArrayList(Seguridad.findUsuarioByIdentidad(filtro));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        personaNombresTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Usuario, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Usuario, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPersonaid().getPersonaapellidos() + " " + p.getValue().getPersonaid().getPersonanombres());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        personaIdentidadTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Usuario, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Usuario, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPersonaid().getPersonarut());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });

//        personaIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("personarut"));
    }

    private void datosUsuario(Usuario usuario) {
        if (usuario != null) {
            persona = usuario.getPersonaid();
            personaIdentidad.setText(persona.getPersonarut());
            personaNombres.setText(persona.getPersonanombres());
            personaApellidos.setText(persona.getPersonaapellidos());
            personaCorreo.setText(persona.getPersonacorreo());
            usuarioIdentidad.setText(usuario.getUsuarioidentidad());
            if (persona.getPersonafoto() != null) {
                byte[] foto = persona.getPersonafoto();
                ByteArrayInputStream bis = new ByteArrayInputStream(foto);
                imagenImagen.setImage(new Image(bis));
            } else {
                imagenImagen.setImage(null);
            }
        } else {
            personaIdentidad.setText("");
            personaNombres.setText("");
            personaApellidos.setText("");
            personaCorreo.setText("");
            usuarioIdentidad.setText("");
        }

    }

    public void nuevoUsuario() {
        Usuario selectedUsuario = new Usuario();
        boolean okClicked = usuarioEditar(selectedUsuario, "Crear");
        if (okClicked) {
            Integer registroid = Seguridad.createIdUsuario(selectedUsuario);
            selectedUsuario.setUsuarioid(registroid);
            listaUsuarios.add(selectedUsuario);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedUsuario.getPersonaid().getPersonaapellidos() + " " + selectedUsuario.getPersonaid().getPersonanombres();
            datosTabla(filtro, "nombre");
            datosUsuario(selectedUsuario);
            GenerarAuditoria.grabaRegistro("Usuario", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarUsuario() {
        Usuario selectedUsuario = (Usuario) tabla.getSelectionModel().getSelectedItem();
        if (selectedUsuario != null) {
            //String foto = selectedUsuario.getPersonaid().getPersonafoto();
            boolean okClicked = usuarioEditar(selectedUsuario, "Editar");
            if (okClicked) {
                Integer registroid = selectedUsuario.getUsuarioid();
                Seguridad.editUsuario(selectedUsuario);
                String filtro = selectedUsuario.getPersonaid().getPersonaapellidos() + " " + selectedUsuario.getPersonaid().getPersonanombres();
                datosTabla(filtro, "nombre");
                datosUsuario(selectedUsuario);
                GenerarAuditoria.grabaRegistro("Usuario", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarUsuario() {
        Usuario selectedUsuario = (Usuario) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedUsuario.getUsuarioestado())) {
            try {
                if (confirmaEliminacion()) {
                    Seguridad.removeUsuario(selectedUsuario);
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

    public void buscarUsuario() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (usuarioBuscar.getText().matches(regex)) {
            tipo = "identidad";
        }
        datosTabla(usuarioBuscar.getText(), tipo);
    }

    public boolean usuarioEditar(Usuario usuario, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/UsuarioEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Usuario");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            UsuarioEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setUsuario(usuario, modo);
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
        tabla.setItems(listaUsuarios);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

}
