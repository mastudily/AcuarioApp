/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Perfil;
import acuario.services.Usuario;
import acuario.services.Persona;
import asmor.textfield.TextFieldL;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import acuario.webservices.Seguridad;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class UsuarioEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPersona;
    @FXML
    TextField usuarioPersonaid;
    @FXML
    TextField personaRUT;
    @FXML
    TextField personaNombres;
    @FXML
    TextField personaCorreo;
    @FXML
    TextField personaMovil;
    @FXML
    TextField personaFijo;
    @FXML
    ComboBox personaCiudad;
    @FXML
    ComboBox personaComuna;
    @FXML
    TextFieldL usuarioIdentidad;
    @FXML
    PasswordField usuarioClave;    
    @FXML
    ComboBox usuarioPerfil;
    @FXML
    TextArea personaDireccion;

    private Stage dialogStage;
    private Usuario usuario;
    private Persona persona;   
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private File file;
    private List listaCiudad;
    private List listaComuna;
    private List listaPerfil;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        personaRUT.setPrefWidth(200);
        personaNombres.setPrefWidth(300);
        personaCorreo.setPrefWidth(400);
        personaFijo.setPrefWidth(200);
        personaMovil.setPrefWidth(200);
        usuarioIdentidad.setMaxLength(10);
        usuarioIdentidad.setPrefWidth(150);
        llenaComboCiudad();
        llenaComboComuna();
        llenaComboPerfil();
    }

    private void llenaComboCiudad() {
        personaCiudad.getItems().clear();
        listaCiudad = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("CIUDAD"), true);
        personaCiudad.getItems().addAll(listaCiudad);
    }

    private void llenaComboComuna() {
        personaComuna.getItems().clear();
        listaComuna = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("COMUNA"), true);
        personaComuna.getItems().addAll(listaComuna);
    }

    private void llenaComboPerfil() {
        usuarioPerfil.getItems().clear();
        listaPerfil = Util.getSelectPerfil(Seguridad.findPerfilByNombre(""), true);
        usuarioPerfil.getItems().addAll(listaPerfil);

    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setUsuario(Usuario usuario, String modo) {
        this.usuario = usuario;
        if ("Editar".equals(modo)) {
            persona = usuario.getPersonaid();
            usuarioPerfil.getSelectionModel().select(Util.indiceSeleccionado(listaPerfil, usuario.getPerfilid().getPerfilid().toString()));
            usuarioIdentidad.setTexto(usuario.getUsuarioidentidad());
            usuarioClave.setText(usuario.getUsuarioclave());
            datosPersona(persona);
        }
    }

    public void datosPersona(Persona persona) {
        usuarioPersonaid.setText(persona.getPersonaid().toString());
        personaRUT.setText(persona.getPersonarut());
        personaNombres.setText(persona.getPersonaapellidos() + " " + persona.getPersonanombres());
        personaCorreo.setText(persona.getPersonacorreo());
        personaDireccion.setText(persona.getPersonadireccion());
        personaCiudad.getSelectionModel().select(Util.indiceSeleccionado(listaCiudad, persona.getPersonaciudad()));
        personaComuna.getSelectionModel().select(Util.indiceSeleccionado(listaComuna, persona.getPersonacomuna()));        
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            try {
                usuario.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
                Perfil perfil = Seguridad.findPerfil(Util.codigoSeleccionado(usuarioPerfil));
                usuario.setPerfilid(perfil);
                usuario.setUsuarioidentidad(usuarioIdentidad.getTexto());
                usuario.setUsuarioclave(usuarioClave.getText());
                usuario.setUsuarioestado("PASIVO");
                usuario.setPersonaid(persona);
                usuario.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(UsuarioEdcController.class.getName() + " " + ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public boolean seleccionPersona() {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/PersonaSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Persona");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            PersonaSelController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            //controller.setUsuario(usuario, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                persona = controller.getPersonaSel();
                datosPersona(persona);
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if (usuarioPersonaid.getText() == null || usuarioPersonaid.getText().length() == 0) {
            errorMessage += "Debe ingresar identificacion de la persona!\n";
        }
        if ("".equals(Util.stringSeleccionado(usuarioPerfil))) {
            errorMessage += "Debe ingresar perfil del usuario!\n";
        }
        if (usuarioIdentidad.getTexto() == null || usuarioIdentidad.getTexto().length() == 0) {
            errorMessage += "Debe ingresar identificacion del usuario!\n";
        }        
        if (usuarioClave.getText() == null || usuarioClave.getText().length() == 0) {
            errorMessage += "Debe ingresar clave del usuario!\n";
        }                
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

}
