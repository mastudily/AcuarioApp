/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Programa;
import asmor.textfield.TextFieldL;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class ProgramaEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPadre;
    @FXML
    Button btnImagen;
    @FXML
    TextFieldL programaNombre;
    @FXML
    TextFieldL programaDescripcion;
    @FXML
    TextFieldL programaAbreviatura;
    @FXML
    TextFieldL programaOrden;
    @FXML
    TextFieldL programaPadreId;
    @FXML
    ComboBox programaTipo;
    @FXML
    ComboBox programaNivel;

    private Stage dialogStage;
    private Programa programa;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private List listaNivel;
    private List listaTipo;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        programaNombre.setMaxLength(30);
        programaNombre.setPrefWidth(300);
        programaDescripcion.setMaxLength(50);
        programaDescripcion.setPrefWidth(400);
        programaAbreviatura.setMaxLength(20);
        programaAbreviatura.setPrefWidth(200);
        programaPadreId.setMaxLength(11);
        programaPadreId.setPrefWidth(100);
        programaOrden.setMaxLength(3);
        programaOrden.setPrefWidth(50);
        llenaComboNivel();
        llenaComboTipo();
    }

    private void llenaComboNivel() {
        programaNivel.getItems().clear();
        listaNivel = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("PRGNIV"), true);
        programaNivel.getItems().addAll(listaNivel);
    }

    private void llenaComboTipo() {
        programaTipo.getItems().clear();
        listaTipo = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("PRGTIP"), true);
        programaTipo.getItems().addAll(listaTipo);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPrograma(Programa programa, String modo) {
        this.programa = programa;
        if ("Editar".equals(modo)) {
            programaNombre.setTexto(programa.getProgramanombre());
            programaDescripcion.setTexto(programa.getProgramadescripcion());
            programaAbreviatura.setTexto(programa.getProgramaabreviatura());
            programaPadreId.setTexto(String.valueOf(programa.getProgramapadreid()));
            programaOrden.setTexto(String.valueOf(programa.getProgramaorden()));
            programaNivel.getSelectionModel().select(Util.indiceSeleccionado(listaNivel, String.valueOf(programa.getProgramanivel())));
            programaTipo.getSelectionModel().select(Util.indiceSeleccionado(listaTipo, programa.getProgramatipo()));
        } else {
            programaNombre.setTexto("");
            programaDescripcion.setTexto("");
            programaAbreviatura.setTexto("");
            programaOrden.setTexto("");
            programaPadreId.setTexto("");
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            programa.setProgramanombre(programaNombre.getTexto());
            programa.setProgramadescripcion(programaDescripcion.getTexto());
            programa.setProgramaabreviatura(programaAbreviatura.getTexto());
            programa.setProgramaorden(Short.parseShort(programaOrden.getTexto()));
            programa.setProgramapadreid(Integer.parseInt(programaPadreId.getTexto()));
            programa.setProgramanivel(Short.parseShort(Util.stringSeleccionado(programaNivel)));
            programa.setProgramatipo(Util.stringSeleccionado(programaTipo));
            programa.setProgramaestado("ACTIVO");
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public boolean seleccionPrograma() {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/ProgramaSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Programa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            ProgramaSelController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            //controller.setCliente(cliente, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                Programa programaSel = controller.getProgramaSel();
                programaPadreId.setTexto(programaSel.getProgramaid().toString());
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

        if (programaNombre.getTexto() == null || programaNombre.getTexto().length() == 0) {
            errorMessage += "Debe ingresar nombre del programa!\n";
        }
        if (programaDescripcion.getTexto() == null || programaDescripcion.getTexto().length() == 0) {
            errorMessage += "Debe ingresar descripcion del programa!\n";
        }
        if (programaAbreviatura.getTexto() == null || programaAbreviatura.getTexto().length() == 0) {
            errorMessage += "Debe ingresar abreviatura!\n";
        }
        if (programaOrden.getTexto() == null || programaOrden.getTexto().length() == 0) {
            errorMessage += "Debe ingresar orden en el menu!\n";
        }
        if (programaPadreId.getTexto() == null || programaPadreId.getTexto().length() == 0) {
            errorMessage += "Debe ingresar jerarquia en el menu!\n";
        }
        if ("".equals(Util.stringSeleccionado(programaNivel))) {
            errorMessage += "Debe ingresar nivel en el menu!\n";
        }
        if ("".equals(Util.stringSeleccionado(programaTipo))) {
            errorMessage += "Debe ingresar tipo de programa!\n";
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
