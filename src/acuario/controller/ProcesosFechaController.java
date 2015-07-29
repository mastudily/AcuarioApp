/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.clases.ControllerAM;
import acuario.clases.Util;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javax.xml.datatype.XMLGregorianCalendar;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class ProcesosFechaController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    DatePicker fechaInicialP;
    @FXML
    DatePicker fechaFinalP;

    private Stage dialogStage;
    private boolean okClicked = false;
    private XMLGregorianCalendar fechaInicial;
    private XMLGregorianCalendar fechaFinal;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        try {
            XMLGregorianCalendar fecha = Util.getXMLCalendar(sdf.format(new Date()));
            fechaInicialP.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            fechaFinalP.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
        } catch (Exception ex) {
            System.err.println(ProcesosFechaController.class.getName() + " " + ex);
        }

    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            try {
                XMLGregorianCalendar fecha = Util.getXMLCalendar(fechaInicialP.getValue().toString());
                setFechaInicial(fecha);
                fecha = Util.getXMLCalendar(fechaFinalP.getValue().toString());
                setFechaFinal(fecha);
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(ProcesosFechaController.class.getName()+" "+ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if (fechaInicialP.getValue() == null ) {
            errorMessage += "Debe ingresar fecha inicial!\n";
        }
        if (fechaFinalP.getValue() == null ) {
            errorMessage += "Debe ingresar fecha final!\n";
        }        
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

    public XMLGregorianCalendar getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(XMLGregorianCalendar fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public XMLGregorianCalendar getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(XMLGregorianCalendar fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

}
