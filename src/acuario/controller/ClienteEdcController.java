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
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.datatype.XMLGregorianCalendar;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class ClienteEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPersona;
    @FXML
    TextField clientePersonaid;
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
    TextArea personaDireccion;

    private Stage dialogStage;
    private Cliente cliente;
    private Persona persona;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private File file;
    private List listaCiudad;
    private List listaComuna;

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
        llenaComboCiudad();
        llenaComboComuna();

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

    public void setCliente(Cliente cliente, String modo) {
        this.cliente = cliente;        
        if ("Editar".equals(modo)) {
            persona = cliente.getPersonaid();
            XMLGregorianCalendar fecha = cliente.getClientefechanacimiento();
            //clienteFecha.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            datosPersona(persona);
        } else {
            //clienteFecha.setValue(null);
        }
    }

    public void datosPersona(Persona persona) {
            clientePersonaid.setText(persona.getPersonaid().toString());
            personaRUT.setText(persona.getPersonarut());
            personaNombres.setText(persona.getPersonaapellidos() + " " + persona.getPersonanombres());
            personaCorreo.setText(persona.getPersonacorreo());
            personaDireccion.setText(persona.getPersonadireccion());
            personaMovil.setText(persona.getPersonamovil());
            personaFijo.setText(persona.getPersonatelefono());
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
                //XMLGregorianCalendar fecha = Util.getXMLCalendar(clienteFecha.getValue().toString());
                //cliente.setClientefechanacimiento(fecha);
                cliente.setClienteestado("ACTIVO");
                cliente.setPersonaid(persona);
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(ClienteEdcController.class.getName()+" "+ ex);
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
            //controller.setCliente(cliente, modo);

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

        if (clientePersonaid.getText() == null || clientePersonaid.getText().length()==0) {
            errorMessage += "Debe ingresar identificacion de persona!\n";
        }
        /*if (clienteFecha.getValue() == null ) {
            errorMessage += "Debe ingresar fecha de nacimiento!\n";
        }*/
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

}
