/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Persona;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.util.IOUtils;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class PersonaEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnImagen;
    @FXML
    TextFieldL personaRUT;
    @FXML
    TextFieldL personaApellidos;
    @FXML
    TextFieldL personaNombres;
    @FXML
    TextFieldL personaCorreo;
    @FXML
    TextFieldL personaMovil;
    @FXML
    TextFieldL personaFijo;
    @FXML
    ComboBox personaCiudad;
    @FXML
    ComboBox personaComuna;
    @FXML
    TextArea personaDireccion;

    private Stage dialogStage;
    private Persona persona;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private File file;
    private List listaCiudad;
    private List listaComuna;
    private String url;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        personaRUT.setMaxLength(20);
        personaRUT.setPrefWidth(200);
        personaApellidos.setMaxLength(30);
        personaApellidos.setPrefWidth(300);
        personaNombres.setMaxLength(30);
        personaNombres.setPrefWidth(300);
        personaCorreo.setMaxLength(50);
        personaCorreo.setPrefWidth(400);
        personaFijo.setMaxLength(20);
        personaFijo.setPrefWidth(200);
        personaMovil.setMaxLength(20);
        personaMovil.setPrefWidth(200);
        llenaComboCiudad();
        llenaComboComuna();
        url = getApp().getUrl();
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

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPersona(Persona persona, String modo) {
        this.persona = persona;
        if ("Editar".equals(modo)) {
            personaRUT.setTexto(persona.getPersonarut());
            personaApellidos.setTexto(persona.getPersonaapellidos());
            personaNombres.setTexto(persona.getPersonanombres());
            personaCorreo.setTexto(persona.getPersonacorreo());
            personaDireccion.setText(persona.getPersonadireccion());
            personaMovil.setTexto(persona.getPersonamovil());
            personaFijo.setTexto(persona.getPersonatelefono());            
            personaCiudad.getSelectionModel().select(Util.indiceSeleccionado(listaCiudad, persona.getPersonaciudad()));
            personaComuna.getSelectionModel().select(Util.indiceSeleccionado(listaComuna, persona.getPersonacomuna()));
        } else {
            personaRUT.setTexto("");
            personaNombres.setTexto("");
            personaApellidos.setTexto("");
            personaCorreo.setTexto("");
            personaMovil.setTexto("");
            personaFijo.setTexto("");
            personaDireccion.setText("");
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            persona.setPersonarut(personaRUT.getTexto());
            persona.setPersonaapellidos(personaApellidos.getTexto());
            persona.setPersonanombres(personaNombres.getTexto());
            persona.setPersonacorreo(personaCorreo.getTexto());
            persona.setPersonadireccion(personaDireccion.getText());
            persona.setPersonafoto(null);
            if (file != null) {
                FileInputStream is = null;
                byte[] res = null;
                try {
                    is = new FileInputStream(file.getAbsolutePath());
                    res = IOUtils.toByteArray(is);
                    persona.setPersonafoto(res);
                } catch (FileNotFoundException ex) {
                    System.err.println("FileNotFoundException:"+PersonaEdcController.class.getName()+ " "+ex);
                } catch (IOException ex) {
                    System.err.println("IOException:"+PersonaEdcController.class.getName()+ " "+ex);
                }                
            } 
            persona.setPersonaciudad(Util.stringSeleccionado(personaCiudad));
            persona.setPersonacomuna(Util.stringSeleccionado(personaComuna));
            persona.setPersonamovil(personaMovil.getTexto());
            persona.setPersonatelefono(personaFijo.getTexto());
            persona.setPersonaestado("ACTIVO");
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if (personaRUT.getTexto() == null || personaRUT.getTexto().length() == 0) {
            errorMessage += "Debe ingresar identidad!\n";
        }
        if (personaApellidos.getTexto() == null || personaApellidos.getTexto().length() == 0) {
            errorMessage += "Debe ingresar apellidos!\n";
        }
        if (personaNombres.getTexto() == null || personaNombres.getTexto().length() == 0) {
            errorMessage += "Debe ingresar nombres!\n";
        }
        if ("".equals(Util.stringSeleccionado(personaCiudad))) {
            errorMessage += "Debe ingresar ciudad!\n";
        }
        if ("".equals(Util.stringSeleccionado(personaComuna))) {
            errorMessage += "Debe ingresar comuna!\n";
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
