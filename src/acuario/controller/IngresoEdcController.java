/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cliente;
import acuario.services.Ingreso;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.DetalleVentaT;
import acuario.clases.Util;
import acuario.services.Insumoempresa;
import acuario.services.InsumoempresaPK;
import acuario.services.Kardex;
import acuario.webservices.Farmacia;
import asmor.hourfield.NumberTextField;
import asmor.textfield.TextFieldL;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class IngresoEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnIngreso;
    @FXML
    TextField ingresoClienteid;
    @FXML
    TextField clienteRUT;
    @FXML
    TextField clienteNombres;
    @FXML
    TextArea ingresoObservaciones;
    @FXML
    TableView tabla;
    @FXML
    TableColumn itemCodigo;
    @FXML
    TableColumn itemDescripcion;
    @FXML
    TableColumn itemCantidad;
    @FXML
    TableColumn itemPrecio;
    @FXML
    TableColumn itemTotal;
    @FXML
    NumberTextField valorTotal;

    private Stage dialogStage;
    private Ingreso ingreso;
    private Cliente cliente;
    private boolean okClicked = false;
    private List<DetalleVentaT> ventaT;
    private ObservableList<DetalleVentaT> ol;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private boolean editMode;
    private Integer empresaid;
    private BigDecimal sumaTotal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        tabla.setId("my-table");
        sumaTotal = BigDecimal.ZERO;
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setIngreso(Ingreso ingreso, String modo) {
        this.ingreso = ingreso;
        dialogStage.getScene().setCursor(Cursor.WAIT);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                llenaDetalle(modo);
                return null;
            }

            @Override
            protected void succeeded() {
                dialogStage.getScene().setCursor(Cursor.DEFAULT);
                if ("Editar".equals(modo)) {
                    editMode = true;
                    cliente = ingreso.getClienteid();
                    ingresoObservaciones.setText(ingreso.getIngresoobservaciones());
                    datosIngreso(cliente);
                } else {
                    editMode = false;
                    ingresoObservaciones.setText(ingreso.getIngresoobservaciones());                    
                }
                ventaDetalle();
            }
        };
        new Thread(task).start();

    }

    public void datosIngreso(Cliente cliente) {
        ingresoClienteid.setText(cliente.getClienteid().toString());
        clienteRUT.setText(cliente.getPersonaid().getPersonarut());
        clienteNombres.setText(cliente.getPersonaid().getPersonaapellidos() + " " + cliente.getPersonaid().getPersonanombres());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            XMLGregorianCalendar fecha = null;
            XMLGregorianCalendar hora = null;
            try {
                ingreso.setIngresoobservaciones(ingresoObservaciones.getText());
                ingreso.setClienteid(cliente);
                try {
                    fecha = Util.fechaSistema();
                    hora = Util.horaSistema();
                } catch (Exception ex) {
                    System.out.println("Error:" + ex);
                }
                //fecha = Util.getXMLCalendar(ingresoFecha.getValue().toString());
                ingreso.setIngresofecha(fecha);
                ingreso.setIngresohora(hora);
                if (!editMode) {
                    ingreso.setIngresoestado("PASIVO");
                    Integer scn = 0;
                    ingreso.setIngresosecuencia(scn.shortValue());
                    ingreso.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
                    ingreso.setIngresovalor(sumaTotal);
                    ingreso.setIngresoadicional(BigDecimal.ZERO);
                    ingreso.setIngresoimpuestos(BigDecimal.ZERO);
                    ingreso.setIngresodescuentos(BigDecimal.ZERO);
                    ingreso.setIngresofactura("");
                }
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(IngresoEdcController.class.getName() + " " + ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @FXML
    public boolean seleccionCliente() {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/ClienteSel.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleccionar Cliente");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            ClienteSelController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            //controller.setIngreso(ingreso, modo);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                cliente = controller.getClienteSel();
                datosIngreso(cliente);
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

        if (ingresoClienteid.getText() == null || ingresoClienteid.getText().length() == 0) {
            errorMessage += "Debe ingresar cliente!\n";
        }
        if (ol.size() == 0) {
            errorMessage += "Debe ingresar al menos un producto!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

    public void llenaDetalle(String modo) {
        ventaT = new ArrayList();
        if ("Editar".equals(modo)) {
            List<Kardex> kardex = Farmacia.findBySolicitudinsumoid(ingreso.getSolicitudinsumoid().getSolicitudinsumoid());
            for (Iterator<Kardex> iterator = kardex.iterator(); iterator.hasNext();) {
                Kardex kardexd = (Kardex) iterator.next();
                InsumoempresaPK insumoempPK = new InsumoempresaPK();
                insumoempPK.setInsumoid(kardexd.getKardexPK().getInsumoid());
                insumoempPK.setEmpresaid(empresaid);
                Insumoempresa insumo = Farmacia.findInsumoempresa(insumoempPK);
                Integer codigo = insumo.getInsumo().getInsumoid();
                String nombre = insumo.getInsumo().getInsumonombre();
                BigDecimal cantidad = kardexd.getKardexcantidad();
                BigDecimal precio = kardexd.getKardexprecio();
                BigDecimal total = precio.multiply(cantidad);
                ventaT.add(new DetalleVentaT(codigo, nombre, cantidad.doubleValue(), precio.doubleValue(), total.doubleValue()));
            }
        }
    }

    public void ventaDetalle() {
        if (ventaT.isEmpty()) {
            tabla.setPlaceholder(new Label("No existen registros..."));
        }
        ol = FXCollections.observableArrayList(ventaT);

        itemCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        itemDescripcion.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        itemCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        itemPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        itemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        MenuItem mnuDel = new MenuItem("Eliminar");
        mnuDel.setOnAction((ActionEvent t) -> {
            DetalleVentaT item = (DetalleVentaT) tabla.getSelectionModel().getSelectedItem();
            if (item != null) {
                Double valor = item.getPrecio().doubleValue() * item.getCantidad().doubleValue();
                sumaTotal = sumaTotal.subtract(new BigDecimal(valor));
                valorTotal.setNumber(sumaTotal);
                ol.remove(item);
                ingreso.setIngresovalor(sumaTotal);
                tabla.getSelectionModel().selectLast();
            }
        });
        MenuItem mnuNew = new MenuItem("Nuevo");
        mnuNew.setOnAction((ActionEvent t) -> {
            nuevoDetalle();
            tabla.getSelectionModel().selectLast();
        });
        tabla.setContextMenu(new ContextMenu(mnuDel, mnuNew));
        tabla.setItems(ol);
    }

    public void nuevoDetalle() {
        DetalleVentaT det = new DetalleVentaT(0, "", 0, 0, 0);
        boolean okClicked = detalleEditar(det, "Crear");
        if (okClicked) {
            Double valor = det.getPrecio().doubleValue() * det.getCantidad().doubleValue();
            sumaTotal = sumaTotal.add(new BigDecimal(valor));
            valorTotal.setNumber(sumaTotal);
            ingreso.setIngresovalor(sumaTotal);
            ol.add(new DetalleVentaT(det.getCodigo().getValue(), det.getNombre().getValue(), det.getCantidad().doubleValue(), det.getPrecio().doubleValue(), det.getTotal().doubleValue()));
        }
    }

    public void modificarDetalle(DetalleVentaT det, int id) {
        boolean okClicked = detalleEditar(det, "Editar");
        if (okClicked) {
            ol.set(id, new DetalleVentaT(det.getCodigo().getValue(), det.getNombre().getValue(), det.getCantidad().doubleValue(), det.getPrecio().doubleValue(), det.getTotal().doubleValue()));
            ventaT.set(id, det);
        }
    }

    public boolean detalleEditar(DetalleVentaT detalleventaT, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/IngresoDet.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Registro de Productos");
            dialog.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);

            // Set the person into the controller
            IngresoDetController controller = loader.getController();
            controller.setDialogStage(dialog);

            controller.setApp(getApp());
            controller.Inicializar();
            controller.setDetalleventaTList(ol);
            controller.setDetalle(detalleventaT, null, modo);
            // Show the dialog and wait until the user closes it
            dialog.showAndWait();
            if (controller.isOkClicked()) {
                detalleventaT = controller.getDetalleventaT();
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    
    public ObservableList<DetalleVentaT> getOl() {
        return ol;
    }

    public void setOl(ObservableList<DetalleVentaT> ol) {
        this.ol = ol;
    }

    public List<DetalleVentaT> getVentaT() {
        return ventaT;
    }

    public void setVentaT(List<DetalleVentaT> ventaT) {
        this.ventaT = ventaT;
    }

}
