/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.DetalleVentaT;
import acuario.services.Insumo;
import acuario.services.Insumoempresa;
import acuario.services.InsumoempresaPK;
import acuario.webservices.Farmacia;
import asmor.textfield.TextFieldL;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
public class IngresoDetController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnProducto;
    @FXML
    TextField itemCodigo;
    @FXML
    TextField itemDescripcion;
    @FXML
    TextField itemActual;
    @FXML
    TextField itemPrecio;
    @FXML
    TextFieldL itemCantidad;

    private Stage dialogStage;
    private DetalleVentaT detalleventaT;
    private List<DetalleVentaT> detalleventaTList;
    private Insumo insumo;
    private boolean okClicked = false;
    private Integer empresaid;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        itemCantidad.setMaxLength(11);
        itemCantidad.setPrefWidth(150);
        itemCantidad.setRestrict("[0-9]*.[0-9]*");
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setDetalle(DetalleVentaT ventaT, Insumo insumo, String modo) {
        this.detalleventaT = ventaT;
        if ("Editar".equals(modo)) {
            itemCantidad.setTexto(ventaT.getCantidad().getValue().toString());
            datosProducto(insumo);
        } else {
            itemCodigo.setText("");
            itemDescripcion.setText("");
            itemActual.setText("");
            itemPrecio.setText("");
            itemCantidad.setTexto("");
        }
    }

    public void datosProducto(Insumo insumo) {
        itemCodigo.setText(insumo.getInsumoid().toString());
        itemDescripcion.setText(insumo.getInsumodescripcion());
        Insumoempresa insumoempresa = Farmacia.findByInsumoempresacodigo(insumo.getInsumocodigo(), empresaid);
        itemActual.setText(insumoempresa.getInsumoempresastockactual().toPlainString());
        itemPrecio.setText(insumo.getInsumovalor().toPlainString());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            try {
                Double cantidad = Double.parseDouble(itemCantidad.getTexto());
                detalleventaT.setCantidad(cantidad);
                detalleventaT.setCodigo(insumo.getInsumoid());
                detalleventaT.setNombre(insumo.getInsumodescripcion());
                detalleventaT.setPrecio(insumo.getInsumovalor().doubleValue());
                BigDecimal total = insumo.getInsumovalor().multiply(new BigDecimal(cantidad));
                detalleventaT.setTotal(total.doubleValue());
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(IngresoDetController.class.getName() + " " + ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public boolean seleccionProducto() {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/InsumoSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Seleccionar Producto");
            dialog.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            
            InsumoSelController controller = loader.getController();
            controller.setDialogStage(dialog);
            controller.setApp(getApp());
            controller.Inicializar();
            // Show the dialog and wait until the user closes it
            dialog.showAndWait();
            if (controller.isOkClicked()) {
                insumo = controller.getInsumoSel();
                datosProducto(insumo);
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

        if (itemCodigo.getText() == null || itemCodigo.getText().length() == 0) {
            errorMessage += "Debe ingresar identificacion del producto!\n";
        } else if (buscarProducto(new Integer(itemCodigo.getText()))) {
            errorMessage += "Producto ya existe!\n";
        }
        if (itemCantidad.getTexto() == null || itemCantidad.getTexto().length() == 0) {
            errorMessage += "Debe ingresar identificacion del producto!\n";
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

    private boolean buscarProducto(Integer id) {
        for (Iterator<DetalleVentaT> iterator = detalleventaTList.iterator(); iterator.hasNext();) {
            DetalleVentaT next = iterator.next();
            if (Objects.equals(next.getCodigo().getValue(), id)) {
                return true;
            }
        }
        return false;
    }
    private boolean validaStock(Integer id, Double cantidad) {
        boolean stockv = true;
        // Valida solo si es EGRESO
        InsumoempresaPK insumoempresapk = new InsumoempresaPK();
        insumoempresapk.setInsumoid(id);
        insumoempresapk.setEmpresaid(empresaid);
        Insumoempresa insumoempresa = Farmacia.findInsumoempresa(insumoempresapk);
        if (cantidad > insumoempresa.getInsumoempresastockactual().doubleValue()) {
            stockv = false;
        }
        return stockv;
    }
    
    public DetalleVentaT getDetalleventaT() {
        return detalleventaT;
    }

    public void setDetalleventaT(DetalleVentaT detalleventaT) {
        this.detalleventaT = detalleventaT;
    }

    public List<DetalleVentaT> getDetalleventaTList() {
        return detalleventaTList;
    }

    public void setDetalleventaTList(List<DetalleVentaT> detalleventaTList) {
        this.detalleventaTList = detalleventaTList;
    }

}
