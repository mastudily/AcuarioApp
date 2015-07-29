/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Constante;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class ConstanteEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    ComboBox constanteCodigo;
    @FXML
    TextFieldL constanteTipo;
    @FXML
    TextFieldL constanteDescripcion;
    @FXML
    TextFieldL constanteAbreviatura;
    @FXML
    TextFieldL constanteValor;

    private Stage dialogStage;
    private Constante constante;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private List listaCodigo;
    private boolean modoEdicion;

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
        constanteTipo.setMaxLength(10);
        constanteTipo.setPrefWidth(100);
        constanteDescripcion.setMaxLength(50);
        constanteDescripcion.setPrefWidth(400);
        constanteAbreviatura.setMaxLength(20);
        constanteAbreviatura.setPrefWidth(200);
        constanteValor.setMaxLength(11);
        constanteValor.setPrefWidth(100);
        constanteValor.setRestrict("[0-9]*.[0-9]*");
        llenaComboTipo();
    }

    private void llenaComboTipo() {
        constanteCodigo.getItems().clear();
        listaCodigo = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("CONCEPTO"), true);
        constanteCodigo.getItems().addAll(listaCodigo);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setConstante(Constante constante, String modo) {
        this.constante = constante;
        if ("Editar".equals(modo)) {
            modoEdicion = true;
            constanteCodigo.setValue(constante.getConstantecodigo());
            constanteTipo.setTexto(constante.getConstantetipo());
            constanteDescripcion.setTexto(constante.getConstantedescripcion());
            constanteAbreviatura.setTexto(constante.getConstanteabreviatura());
            constanteValor.setTexto(constante.getConstantevalor().toPlainString());
        } else {
            modoEdicion = false;
            constanteCodigo.getSelectionModel().select(Util.indiceSeleccionado(listaCodigo, constante.getConstantetipo()));
            constanteTipo.setTexto("");
            constanteDescripcion.setTexto("");
            constanteAbreviatura.setTexto("");
            constanteValor.setTexto("");
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            if (!modoEdicion) {
                constante.setConstantecodigo(Util.stringSeleccionado(constanteCodigo));
            }
            constante.setConstantedescripcion(constanteDescripcion.getTexto());
            constante.setConstanteabreviatura(constanteAbreviatura.getTexto());
            constante.setConstantetipo(constanteTipo.getTexto());
            if (constanteValor.getTexto().length()>0) {
                constante.setConstantevalor(new BigDecimal(Double.parseDouble(constanteValor.getTexto())));
            } else {
                constante.setConstantevalor(BigDecimal.ZERO);
            }
            constante.setConstanteestado("ACTIVO");
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
        if (constanteTipo.getTexto() == null || constanteTipo.getTexto().length() == 0) {
            errorMessage += "Debe ingresar tipo de constante!\n";
        }
        if (constanteDescripcion.getTexto() == null || constanteDescripcion.getTexto().length() == 0) {
            errorMessage += "Debe ingresar descripcion del constante!\n";
        }
        if (constanteAbreviatura.getTexto() == null || constanteAbreviatura.getTexto().length() == 0) {
            errorMessage += "Debe ingresar abreviatura!\n";
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
