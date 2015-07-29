/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cuentacontable;
import acuario.services.Formapago;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Financiero;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class FormapagoEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnSeleccion;
    @FXML
    TextField formapagoId;
    @FXML
    TextFieldL formapagoNombre;
    @FXML
    TextFieldL formapagoDescripcion;
    @FXML
    ComboBox formapagoCtaCbe;
    @FXML
    ComboBox formapagoCtaCtaCbe;

    private Stage dialogStage;
    private Formapago formapago;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private List listaCuenta;
    private Integer empresaid;
    private boolean editMode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        formapagoNombre.setMaxLength(30);
        formapagoNombre.setPrefWidth(250);
        formapagoDescripcion.setMaxLength(100);
        formapagoDescripcion.setPrefWidth(300);
        llenaComboCuenta();
    }


    private void llenaComboCuenta() {
        formapagoCtaCbe.getItems().clear();
        formapagoCtaCtaCbe.getItems().clear();
        listaCuenta = Util.getSelectCuentacontable(Financiero.findByCuentacontablemayor(false, empresaid), true);
        formapagoCtaCbe.getItems().addAll(listaCuenta);
        formapagoCtaCtaCbe.getItems().addAll(listaCuenta);
    }
    
    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setFormapago(Formapago formapago, String modo) {
        this.formapago = formapago;
        if ("Editar".equals(modo)) {
            this.formapago = formapago;
            formapagoNombre.setTexto(formapago.getFormapagonombre());
            formapagoDescripcion.setTexto(formapago.getFormapagodescripcion());
            formapagoCtaCbe.getSelectionModel().select(Util.indiceNumericoSeleccionado(listaCuenta, formapago.getFormapagoctacble().getCuentacontableid()));
            formapagoCtaCtaCbe.getSelectionModel().select(Util.indiceNumericoSeleccionado(listaCuenta, formapago.getFormapagoconctacble().getCuentacontableid()));
            editMode = true;
        } else {
            formapagoNombre.setTexto("");
            formapagoDescripcion.setTexto("");
            editMode = false;
        }
    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            formapago.setFormapagonombre(formapagoNombre.getTexto());
            formapago.setFormapagodescripcion(formapagoDescripcion.getTexto());
            Cuentacontable cuenta = Financiero.findCuentacontable(Util.codigoSeleccionado(formapagoCtaCbe));
            formapago.setFormapagoctacble(cuenta);
            cuenta = Financiero.findCuentacontable(Util.codigoSeleccionado(formapagoCtaCtaCbe));
            formapago.setFormapagoconctacble(cuenta);
            if (!editMode) {
                formapago.setFormapagoestado("PASIVO");
            }
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

        if ("".equals(formapagoNombre.getTexto())) {
            errorMessage += "Debe ingresar nombre de la forma de pago!\n";
        }
        if ("".equals(formapagoDescripcion.getTexto())) {
            errorMessage += "Debe ingresar descripcion de la forma de pago!\n";
        }        
        if (Util.codigoSeleccionado(formapagoCtaCbe)==0) {
            errorMessage += "Debe ingresar cuenta contable!\n";
        }
        if (Util.codigoSeleccionado(formapagoCtaCtaCbe)==0) {
            errorMessage += "Debe ingresar contra cuenta contable!\n";
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
