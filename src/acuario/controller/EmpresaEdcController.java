/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Empresa;
import acuario.services.Persona;
import asmor.textfield.TextFieldL;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
public class EmpresaEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPersona;
    @FXML
    TextFieldL empresaRUT;
    @FXML
    TextFieldL empresaNombre;
    @FXML
    TextFieldL empresaDescripcion;
    @FXML
    TextFieldL empresaTelefono;
    @FXML
    TextArea empresaDireccion;
    @FXML
    TextField empresaPersonaid;
    @FXML
    TextField empresaPersonanombres;

    private Stage dialogStage;
    private Empresa empresa;
    private Persona persona;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        empresaRUT.setMaxLength(20);
        empresaRUT.setPrefWidth(200);
        empresaNombre.setMaxLength(30);
        empresaNombre.setPrefWidth(300);
        empresaDescripcion.setMaxLength(100);
        empresaDescripcion.setPrefWidth(400);
        empresaTelefono.setMaxLength(20);
        empresaTelefono.setPrefWidth(200);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEmpresa(Empresa empresa, String modo) {
        this.empresa = empresa;
        if ("Editar".equals(modo)) {
            empresaRUT.setTexto(empresa.getEmpresarut());
            empresaNombre.setTexto(empresa.getEmpresanombre());
            empresaDescripcion.setTexto(empresa.getEmpresadescripcion());
            empresaDireccion.setText(empresa.getEmpresadireccion());
            empresaTelefono.setTexto(empresa.getEmpresatelefono());
            persona = empresa.getPersonaid();
            datosPersona(persona);
        } else {
            empresaRUT.setTexto("");
            empresaNombre.setTexto("");
            empresaDescripcion.setTexto("");
            empresaDireccion.setText("");
            empresaTelefono.setTexto("");
            empresaPersonaid.setText("");
            empresaPersonanombres.setText("");
        }
    }

    private void datosPersona(Persona persona) {
        empresaPersonaid.setText(persona.getPersonaid().toString());
        empresaPersonanombres.setText(persona.getPersonaapellidos() + " " + persona.getPersonanombres());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            try {
                empresa.setEmpresarut(empresaRUT.getTexto());
                empresa.setEmpresanombre(empresaNombre.getTexto());
                empresa.setEmpresadescripcion(empresaDescripcion.getTexto());
                empresa.setEmpresatelefono(empresaTelefono.getTexto());
                empresa.setEmpresadireccion(empresaDireccion.getText());
                empresa.setEmpresacoordlat(BigDecimal.ZERO);
                empresa.setEmpresacoordlon(BigDecimal.ZERO);
                empresa.setEmpresaestado("ACTIVO");
                empresa.setPersonaid(persona);
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(EmpresaEdcController.class.getName() + " " + ex);
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
            //controller.setEmpresa(empresa, modo);

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

        if (empresaPersonaid.getText() == null || empresaPersonaid.getText().length() == 0) {
            errorMessage += "Debe ingresar identificacion de persona!\n";
        }
        if (empresaRUT.getTexto() == null || empresaRUT.getTexto().length()==0) {
            errorMessage += "Debe ingresar RUT de la empresa!\n";
        }
        if (empresaNombre.getTexto() == null || empresaNombre.getTexto().length()==0) {
            errorMessage += "Debe ingresar nombre de la empresa!\n";
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
