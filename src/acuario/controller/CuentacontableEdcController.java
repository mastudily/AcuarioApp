/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cuentacontable;
import asmor.textfield.TextFieldL;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class CuentacontableEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPadre;
    @FXML
    Button btnImagen;
    @FXML
    TextFieldL cuentacontableId;
    @FXML
    TextFieldL cuentacontableNombre;
    @FXML
    TextFieldL cuentacontableDescripcion;
    @FXML
    TextFieldL cuentacontableCodigo;
    @FXML
    ChoiceBox cuentacontableSigno;
    @FXML
    TextFieldL cuentacontablePadreId;
    @FXML
    CheckBox cuentacontableMayor;

    private Stage dialogStage;
    private Cuentacontable cuentacontable;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        cuentacontableId.setMaxLength(11);
        cuentacontableId.setPrefWidth(100);
        cuentacontableNombre.setMaxLength(30);
        cuentacontableNombre.setPrefWidth(300);
        cuentacontableDescripcion.setMaxLength(50);
        cuentacontableDescripcion.setPrefWidth(400);
        cuentacontableCodigo.setMaxLength(20);
        cuentacontableCodigo.setPrefWidth(200);
        cuentacontablePadreId.setMaxLength(11);
        cuentacontablePadreId.setPrefWidth(100);
        cuentacontableSigno.setItems(FXCollections.observableArrayList("+", "-"));
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCuentacontable(Cuentacontable cuentacontable, String modo) {
        this.cuentacontable = cuentacontable;
        if ("Editar".equals(modo)) {
            cuentacontableId.setTexto(cuentacontable.getCuentacontableid().toString());
            cuentacontableNombre.setTexto(cuentacontable.getCuentacontablenombre());
            cuentacontableDescripcion.setTexto(cuentacontable.getCuentacontabledescripcion());
            cuentacontableCodigo.setTexto(cuentacontable.getCuentacontablecodigo());
            cuentacontablePadreId.setTexto(cuentacontable.getCuentacontablepadreid().toString());
            cuentacontableSigno.setValue(cuentacontable.getCuentacontablesigno());
            cuentacontableMayor.setSelected(cuentacontable.isCuentacontablemayor());
        } else {
            cuentacontableId.setTexto("");
            cuentacontableNombre.setTexto("");
            cuentacontableDescripcion.setTexto("");
            cuentacontableCodigo.setTexto("");
            cuentacontableSigno.setValue("+");
            cuentacontablePadreId.setTexto("");
            cuentacontableMayor.setSelected(false);
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            cuentacontable.setCuentacontablenombre(cuentacontableNombre.getTexto());
            cuentacontable.setCuentacontabledescripcion(cuentacontableDescripcion.getTexto());
            cuentacontable.setCuentacontablecodigo(cuentacontableCodigo.getTexto());
            cuentacontable.setCuentacontablesigno(cuentacontableSigno.getValue().toString());
            cuentacontable.setCuentacontablepadreid(Integer.parseInt(cuentacontablePadreId.getTexto()));
            cuentacontable.setCuentacontablepadre(false);
            cuentacontable.setCuentacontablemayor(cuentacontableMayor.isSelected());
            cuentacontable.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
            cuentacontable.setCuentacontableestado("ACTIVO");
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public boolean seleccionCuentacontable() {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/CuentacontableSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Cuenta contable");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            CuentacontableSelController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            //controller.setCliente(cliente, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                Cuentacontable cuentacontableSel = controller.getCuentacontableSel();
                cuentacontablePadreId.setTexto(cuentacontableSel.getCuentacontableid().toString());
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

        if (cuentacontableNombre.getTexto() == null || cuentacontableNombre.getTexto().length() == 0) {
            errorMessage += "Debe ingresar nombre de la cuenta contable!\n";
        }
        if (cuentacontableDescripcion.getTexto() == null || cuentacontableDescripcion.getTexto().length() == 0) {
            errorMessage += "Debe ingresar descripcion de la cuenta contable!\n";
        }
        if (cuentacontableCodigo.getTexto() == null || cuentacontableCodigo.getTexto().length() == 0) {
            errorMessage += "Debe ingresar codigo de cuenta!\n";
        }
        if (cuentacontablePadreId.getTexto() == null || cuentacontablePadreId.getTexto().length() == 0) {
            errorMessage += "Debe ingresar jerarquia de la cuenta!\n";
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
