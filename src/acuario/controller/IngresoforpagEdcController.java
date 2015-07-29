/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import asmor.hourfield.NumberTextField;
import acuario.services.Formapago;
import acuario.services.Ingresoforpag;
import asmor.textfield.TextFieldL;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class IngresoforpagEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnSeleccion;
    @FXML
    TextField formapagoId;
    @FXML
    TextField formapagoDescripcion;    
    @FXML
    TextFieldL formapagoReferencia1;
    @FXML
    TextFieldL formapagoReferencia2;
    @FXML
    TextFieldL formapagoReferencia3;
    @FXML
    NumberTextField formapagoValor;    
    @FXML
    ComboBox formapagoBanEmi;

    private Stage dialogStage;
    private Ingresoforpag ingresoforpag;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private List listaBanEmi;
    private Integer empresaid;
    private boolean editMode;
    private Formapago formapago;
    private List<Ingresoforpag> ingresoforpagList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        formapagoReferencia1.setMaxLength(30);
        formapagoReferencia1.setPrefWidth(250);
        formapagoReferencia2.setMaxLength(30);
        formapagoReferencia2.setPrefWidth(250);
        formapagoReferencia3.setMaxLength(30);
        formapagoReferencia3.setPrefWidth(250);
        formapagoValor.setMaxLength(11);
        formapagoValor.setPrefWidth(100);
        //llenaComboBanEmi();
    }


    private void llenaComboBanEmi() {
        formapagoBanEmi.getItems().clear();
        listaBanEmi = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("BANEMI"), true);
        formapagoBanEmi.getItems().addAll(listaBanEmi);
    }
    
    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setIngresoforpag(Ingresoforpag ingresoforpag, String modo) {
        this.ingresoforpag = ingresoforpag;
        if ("Editar".equals(modo)) {
            formapago = ingresoforpag.getFormapago();
            formapagoId.setText(formapago.getFormapagoid().toString());
            formapagoDescripcion.setText(formapago.getFormapagodescripcion());
            //formapagoBanEmi.getSelectionModel().select(Util.indiceNumericoSeleccionado(listaBanEmi, ingresoforpag.));
            formapagoReferencia1.setTexto(ingresoforpag.getIngresoforpagreferencia1());
            formapagoReferencia2.setTexto(ingresoforpag.getIngresoforpagreferencia2());
            formapagoReferencia3.setTexto(ingresoforpag.getIngresoforpagreferencia3());
            formapagoValor.setNumber(ingresoforpag.getIngresoforpagvalor());
            editMode = true;
            btnSeleccion.setVisible(false);
        } else {
            formapagoId.setText("");
            formapagoDescripcion.setText("");
            formapagoReferencia1.setTexto("");
            formapagoReferencia2.setTexto("");
            formapagoReferencia3.setTexto("");            
            editMode = false;
        }
    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            ingresoforpag.setIngresoforpagreferencia1(formapagoReferencia1.getTexto());
            ingresoforpag.setIngresoforpagreferencia2(formapagoReferencia2.getTexto());
            ingresoforpag.setIngresoforpagreferencia3(formapagoReferencia3.getTexto());
            ingresoforpag.setIngresoforpagvalor(formapagoValor.getNumber());
            ingresoforpag.setFormapago(formapago);
            if (!editMode) {
                ingresoforpag.setIngresoforpagestado("PASIVO");
            }
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }
    
    @FXML
    public boolean seleccionFormapago() {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/FormapagoSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Forma de Pago");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            FormapagoSelController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            //controller.setCliente(cliente, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                formapago = controller.getFormapagoSel();
                formapagoId.setText(formapago.getFormapagoid().toString());
                formapagoDescripcion.setText(formapago.getFormapagodescripcion());
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
        if ("".equals(formapagoId.getText())) {
            errorMessage += "Debe ingresar forma de pago!\n";
        } else if (buscarIngresopago(Integer.parseInt(formapagoId.getText())) && !editMode) {
            errorMessage += "Forma de pago para este ingreso ya existe!\n";
        }    
        if ("".equals(formapagoReferencia1.getTexto())) {
            errorMessage += "Debe ingresar al menos una referencia!\n";
        }
        if (formapagoValor.getNumber().compareTo(BigDecimal.ZERO)==0) {
            errorMessage += "Debe ingresar valor del pago!\n";
        }            
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

    private boolean buscarIngresopago(Integer id) {
        for (Iterator<Ingresoforpag> iterator = ingresoforpagList.iterator(); iterator.hasNext();) {
            Ingresoforpag next = iterator.next();
            if (Objects.equals(next.getFormapago().getFormapagoid(), id)) {
                return true;
            }
        }
        return false;
    }
    
    public Ingresoforpag getIngresoforpag() {
        return ingresoforpag;
    }

    public List<Ingresoforpag> getIngresoforpagList() {
        return ingresoforpagList;
    }

    public void setIngresoforpagList(List<Ingresoforpag> ingresoforpagList) {
        this.ingresoforpagList = ingresoforpagList;
    }



}
