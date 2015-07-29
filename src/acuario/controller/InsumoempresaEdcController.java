/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumoempresa;
import acuario.services.Insumo;
import acuario.services.InsumoempresaPK;
import asmor.textfield.TextFieldL;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
public class InsumoempresaEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnInsumo;
    @FXML
    TextField insumoId;    
    @FXML
    TextField insumoNombre;
    @FXML
    TextField insumoDescripcion;
    @FXML
    TextField insumoCodigo;
    @FXML
    TextFieldL insumoInicial;
    @FXML
    TextFieldL insumoMinimo;
    @FXML
    TextFieldL insumoMaximo;
    @FXML
    DatePicker fechaCompra;
    @FXML
    DatePicker fechaVigencia;

    

    private Stage dialogStage;
    private Insumoempresa insumoempresa;
    private Insumo insumo;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private XMLGregorianCalendar fecha;
    private File file;
    private Integer empresaid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        insumoInicial.setPrefWidth(150);
        insumoInicial.setMaxLength(11);
        insumoInicial.setRestrict("[0-9]*.[0-9]*");
        insumoMinimo.setPrefWidth(150);
        insumoMinimo.setMaxLength(11);
        insumoMinimo.setRestrict("[0-9]*.[0-9]*");
        insumoMaximo.setPrefWidth(150);
        insumoMaximo.setMaxLength(11);
        insumoMaximo.setRestrict("[0-9]*.[0-9]*");
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInsumoempresa(Insumoempresa insumoempresa, String modo) {
        this.insumoempresa = insumoempresa;        
        if ("Editar".equals(modo)) {
            btnInsumo.setDisable(true);
            insumoId.setEditable(false);            
            insumo = insumoempresa.getInsumo();
            fecha = insumoempresa.getInsumoempresacompra();
            fechaCompra.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));  
            fecha = insumoempresa.getInsumoempresavigencia();
            fechaVigencia.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            datosInsumo(insumo);
        } else {
            btnInsumo.setDisable(false);
            insumoId.setEditable(true);
            fechaCompra.setValue(null);
            fechaVigencia.setValue(null);
        }
    }

    public void datosInsumo(Insumo insumo) {
            insumoId.setText(insumo.getInsumoid().toString());
            insumoCodigo.setText(insumo.getInsumocodigo());
            insumoNombre.setText(insumo.getInsumonombre());
            insumoDescripcion.setText(insumo.getInsumodescripcion());
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            try {
                InsumoempresaPK insumoempresaPK = new InsumoempresaPK();
                insumoempresaPK.setInsumoid(insumo.getInsumoid());
                insumoempresaPK.setEmpresaid(empresaid);
                insumoempresa.setInsumoempresaPK(insumoempresaPK);
                insumoempresa.setInsumoempresaestado("ACTIVO");
                insumoempresa.setInsumo(insumo);
                insumoempresa.setEmpresa(getApp().getLoggedUser().getEmpresaid());
                insumoempresa.setInsumoempresastockinicial(new BigDecimal(Double.parseDouble(insumoInicial.getTexto())));
                insumoempresa.setInsumoempresastockminimo(new BigDecimal(Double.parseDouble(insumoMinimo.getTexto())));
                insumoempresa.setInsumoempresastockmaximo(new BigDecimal(Double.parseDouble(insumoMaximo.getTexto())));
                fecha = Util.getXMLCalendar(fechaCompra.getValue().toString());
                insumoempresa.setInsumoempresacompra(fecha);
                fecha = Util.getXMLCalendar(fechaVigencia.getValue().toString());
                insumoempresa.setInsumoempresavigencia(fecha);                
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(InsumoempresaEdcController.class.getName()+" "+ ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public boolean seleccionInsumo() {
    try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/InsumoSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Insumo");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            InsumoSelController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            //controller.setInsumoempresa(insumoempresa, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                insumo = controller.getInsumoSel();
                datosInsumo(insumo);
                insumoId.setText(insumo.getInsumoid().toString());
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

        if (insumoInicial.getTexto() == null || insumoInicial.getTexto().length()==0) {
            errorMessage += "Debe ingresar stock inicial!\n";
        }
        if (insumoMinimo.getTexto() == null || insumoMinimo.getTexto().length()==0) {
            errorMessage += "Debe ingresar stock minimo!\n";
        }
        if (insumoMaximo.getTexto() == null || insumoMaximo.getTexto().length()==0) {
            errorMessage += "Debe ingresar stock maximo!\n";
        }
        if (fechaCompra.getValue() == null ) {
            errorMessage += "Debe ingresar fecha de compra del insumo!\n";
        }
        if (fechaVigencia.getValue() == null ) {
            errorMessage += "Debe ingresar fecha de vigencia del insumo!\n";
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
