/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumo;
import acuario.services.Insumoempresa;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.poi.util.IOUtils;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class InsumoEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnImagen;
    @FXML
    TextFieldL insumoNombre;
    @FXML
    TextFieldL insumoDescripcion;
    @FXML
    TextFieldL insumoCodigo;
    @FXML
    TextFieldL insumoValor;
    @FXML
    ComboBox insumoUnMed;
    @FXML
    ComboBox insumoTipo;
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
    @FXML
    Label nombreImagen;
    @FXML
    ImageView imagenImagen;    

    private Stage dialogStage;
    private Insumo insumo;
    private Insumoempresa insumoempresa;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private XMLGregorianCalendar fecha;
    private File file;
    private List listaUnMed;
    private List listaTipo;
    private Integer empresaid;
    private boolean editMode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        insumoCodigo.setMaxLength(20);
        insumoCodigo.setPrefWidth(150);
        insumoNombre.setMaxLength(30);
        insumoNombre.setPrefWidth(300);
        insumoDescripcion.setMaxLength(100);
        insumoDescripcion.setPrefWidth(600);
        insumoDescripcion.setMaxWidth(600);
        insumoValor.setMaxLength(13);
        insumoValor.setPrefWidth(150);
        insumoValor.setRestrict("[0-9]*.[0-9]*");
        llenaComboUnMed();
        llenaComboTipo();
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

    private void llenaComboUnMed() {
        insumoUnMed.getItems().clear();
        listaUnMed = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("UNIMED"), true);
        insumoUnMed.getItems().addAll(listaUnMed);
    }

    private void llenaComboTipo() {
        insumoTipo.getItems().clear();
        listaTipo = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("TIPINS"), true);
        insumoTipo.getItems().addAll(listaTipo);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInsumo(Insumo insumo, String modo) {
        this.insumo = insumo;
        if ("Editar".equals(modo)) {
            insumoNombre.setTexto(insumo.getInsumonombre());
            insumoDescripcion.setTexto(insumo.getInsumodescripcion());
            insumoCodigo.setTexto(insumo.getInsumocodigo());
            insumoValor.setTexto(insumo.getInsumovalor().toPlainString());
            if (insumo.getInsumofoto() != null) {
                byte[] foto = insumo.getInsumofoto();
                ByteArrayInputStream bis = new ByteArrayInputStream(foto);
                imagenImagen.setImage(new Image(bis));
            } else {
                imagenImagen.setImage(null);
            }
            insumoUnMed.getSelectionModel().select(Util.indiceSeleccionado(listaUnMed, insumo.getInsumounmed()));
            insumoTipo.getSelectionModel().select(Util.indiceSeleccionado(listaTipo, insumo.getInsumotipo()));
            editMode = true;
        } else {
            insumoNombre.setTexto("");
            insumoDescripcion.setTexto("");
            insumoCodigo.setTexto("");
            insumoValor.setTexto("");
            editMode = false;
            //nombreImagen.setText("");
        }
    }

    public void datosInsumoempresa(Insumoempresa insumoempresa, String modo) {
        this.insumoempresa = insumoempresa;
        if ("Editar".equals(modo)) {
            insumoInicial.setTexto(insumoempresa.getInsumoempresastockinicial().toString());
            insumoMaximo.setTexto(insumoempresa.getInsumoempresastockmaximo().toString());
            insumoMinimo.setTexto(insumoempresa.getInsumoempresastockminimo().toString());
            fecha = insumoempresa.getInsumoempresacompra();
            fechaCompra.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            fecha = insumoempresa.getInsumoempresavigencia();
            fechaVigencia.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
        } else {
            insumoInicial.setTexto("");
            insumoMaximo.setTexto("");
            insumoMinimo.setTexto("");
            fechaCompra.setValue(null);
            fechaVigencia.setValue(null);
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            insumo.setInsumocodigo(insumoCodigo.getTexto());
            insumo.setInsumonombre(insumoNombre.getTexto());
            insumo.setInsumodescripcion(insumoDescripcion.getTexto());
            insumo.setInsumovalor(new BigDecimal(Double.parseDouble(insumoValor.getTexto())));
            if (file != null) {
                FileInputStream is = null;
                byte[] res = null;
                try {
                    is = new FileInputStream(file.getAbsolutePath());
                    res = IOUtils.toByteArray(is);
                    insumo.setInsumofoto(res);
                } catch (FileNotFoundException ex) {
                    System.err.println("FileNotFoundException:"+InsumoEdcController.class.getName()+ " "+ex);
                } catch (IOException ex) {
                    System.err.println("IOException:"+InsumoEdcController.class.getName()+ " "+ex);
                }                
            } 
            insumo.setInsumounmed(Util.stringSeleccionado(insumoUnMed));
            insumo.setInsumotipo(Util.stringSeleccionado(insumoTipo));
            insumo.setInsumopresentacion(Util.stringSeleccionado(insumoUnMed));
            insumo.setInsumoestado("PASIVO");
            grabaInsumoempresa(insumo);
            okClicked = true;
            dialogStage.close();
        }
    }

    private void grabaInsumoempresa(Insumo insumo) {
        try {
            insumoempresa.setInsumoempresaestado("ACTIVO");
            insumoempresa.setInsumoempresastockactual(new BigDecimal(Double.parseDouble(insumoInicial.getTexto())));
            insumoempresa.setInsumoempresastockminimo(new BigDecimal(Double.parseDouble(insumoMinimo.getTexto())));
            insumoempresa.setInsumoempresastockmaximo(new BigDecimal(Double.parseDouble(insumoMaximo.getTexto())));
            fecha = Util.getXMLCalendar(fechaCompra.getValue().toString());
            insumoempresa.setInsumoempresacompra(fecha);
            fecha = Util.getXMLCalendar(fechaVigencia.getValue().toString());
            insumoempresa.setInsumoempresavigencia(fecha);
            if (!editMode) {
                insumoempresa.setInsumoempresastockinicial(new BigDecimal(Double.parseDouble(insumoInicial.getTexto())));
            }            
            okClicked = true;
            dialogStage.close();
        } catch (Exception ex) {
            System.err.println(InsumoempresaEdcController.class.getName() + " " + ex);
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show open file dialog
        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            nombreImagen.setText(file.getName());
            Image image = new Image(file.toURI().toString());
            imagenImagen.setImage(image);
        }
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if (insumoNombre.getTexto() == null || insumoNombre.getTexto().length() == 0) {
            errorMessage += "Debe ingresar nombre del insumo!\n";
        }
        if (insumoDescripcion.getTexto() == null || insumoDescripcion.getTexto().length() == 0) {
            errorMessage += "Debe ingresar descripcion del insumo!\n";
        }
        /*if (insumoCodigo.getTexto() == null || insumoCodigo.getTexto().length() == 0) {
            errorMessage += "Debe ingresar codigo del insumo!\n";
        }*/
        if (insumoUnMed.getValue().toString().length() == 0) {
            errorMessage += "Debe ingresar unidad de medida!\n";
        }
        if (insumoTipo.getValue().toString().length() == 0) {
            errorMessage += "Debe ingresar tipo de insumo!\n";
        }
        if (insumoValor.getTexto() == null || insumoValor.getTexto().length() == 0) {
            errorMessage += "Debe ingresar valor del insumo!\n";
        }
        if (insumoInicial.getTexto() == null || insumoInicial.getTexto().length() == 0) {
            errorMessage += "Debe ingresar stock inicial!\n";
        }
        if (insumoMinimo.getTexto() == null || insumoMinimo.getTexto().length() == 0) {
            errorMessage += "Debe ingresar stock minimo!\n";
        }
        if (insumoMaximo.getTexto() == null || insumoMaximo.getTexto().length() == 0) {
            errorMessage += "Debe ingresar stock maximo!\n";
        }
        if (fechaCompra.getValue() == null) {
            errorMessage += "Debe ingresar fecha de compra del insumo!\n";
        }
        if (fechaVigencia.getValue() == null) {
            errorMessage += "Debe ingresar fecha de vigencia del insumo!\n";
        }

        //if (nombreImagen.getText() == null || nombreImagen.getText().length() == 0) {
        //    errorMessage += "Debe ingresar foto!\n";
        //}
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

}
