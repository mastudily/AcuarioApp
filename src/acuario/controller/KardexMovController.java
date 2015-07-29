/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumo;
import acuario.services.Kardex;
import acuario.services.Solicitudinsumo;
import acuario.clases.ControllerAM;
import acuario.clases.KardexMovimiento;
import acuario.clases.Util;
import acuario.webservices.Farmacia;
import java.math.BigDecimal;
import java.time.LocalDate;
import static java.time.LocalDate.now;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import javax.xml.datatype.XMLGregorianCalendar;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class KardexMovController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnImprimir;
    @FXML
    Button btnCancelar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn solicitudId;
    @FXML
    TableColumn solicitudDescripcion;
    @FXML
    TableColumn solicitudSigno;
    @FXML
    TableColumn kardexAnterior;
    @FXML
    TableColumn kardexCantidad;
    @FXML
    TableColumn kardexActual;
    @FXML
    TextField insumoNombre;
    @FXML
    TextField insumoDescripcion;
    @FXML
    DatePicker fechaInicial;
    @FXML
    DatePicker fechaFinal;

    private ObservableList<KardexMovimiento> listaKardex = FXCollections.observableArrayList();
    private Insumo insumo;
    private Stage dialogStage;
    private boolean okClicked = false;
    private XMLGregorianCalendar fechaI;
    private XMLGregorianCalendar fechaF;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        try {
            fechaInicial.setValue(LocalDate.ofYearDay(now().getYear(), 1));
            fechaFinal.setValue(now());
            fechaI = Util.getXMLCalendar(fechaInicial.getValue().toString());
            fechaF = Util.getXMLCalendar(fechaFinal.getValue().toString());
            inicializaTabla();
            datosInsumo(insumo);
            actualizarTabla(fechaI, fechaF);
        } catch (Exception ex) {
            Logger.getLogger(KardexMovController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inicializaTabla() {
        solicitudId.setCellValueFactory(new PropertyValueFactory<>("solicitudinsumoid"));
        solicitudDescripcion.setCellValueFactory(new PropertyValueFactory<>("solicitudinsumodescripcion"));
        solicitudSigno.setCellValueFactory(new PropertyValueFactory<>("solicitudinsumotipo"));
        kardexAnterior.setCellValueFactory(new PropertyValueFactory<>("kardexultimacantidad"));
        kardexCantidad.setCellValueFactory(new PropertyValueFactory<>("kardexcantidad"));
        kardexActual.setCellValueFactory(new PropertyValueFactory<>("kardexactualcantidad"));
    }

    private void actualizarTabla(XMLGregorianCalendar fechaini, XMLGregorianCalendar fechafin) {
        dialogStage.getScene().setCursor(Cursor.WAIT);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                listaKardex = FXCollections.observableArrayList(kardexMovimiento(insumo, fechaini, fechafin));
                return null;
            }

            @Override
            protected void succeeded() {
                dialogStage.getScene().setCursor(Cursor.DEFAULT);
                if (listaKardex.isEmpty()) {
                    tabla.setPlaceholder(new Label("No existen movimientos para este producto..."));
                }
                tabla.setItems(null);
                tabla.layout();
                tabla.setItems(listaKardex);
                tabla.getSelectionModel().selectFirst();
            }
        };
        new Thread(task).start();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void datosInsumo(Insumo insumo) {
        if (insumo != null) {
            insumoNombre.setText(insumo.getInsumonombre());
            insumoDescripcion.setText(insumo.getInsumodescripcion());
        }
    }

    private List<KardexMovimiento> kardexMovimiento(Insumo insumo, XMLGregorianCalendar fechaini, XMLGregorianCalendar fechafin) {
        List<KardexMovimiento> listKardex = new ArrayList();
        List<Kardex> kardex = Farmacia.findByInsumoidPeriodo(insumo.getInsumoid(), fechaini, fechafin, "PROCESADO");
        BigDecimal actualcantidad = BigDecimal.ZERO;
        BigDecimal cantidad = BigDecimal.ZERO;
        for (Iterator<Kardex> iterator = kardex.iterator(); iterator.hasNext();) {
            Kardex next = iterator.next();
            KardexMovimiento movimiento = new KardexMovimiento();
            XMLGregorianCalendar fecha = next.getKardexfecha();
            String fechad = LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()).toString();
            movimiento.setKardexfecha(fechad);
            Solicitudinsumo solicitud = next.getSolicitudinsumo();
            movimiento.setSolicitudinsumoid(solicitud.getSolicitudinsumoid());
            movimiento.setSolicitudinsumodescripcion(solicitud.getSolicitudinsumodescripcion());
            movimiento.setSolicitudinsumotipo(next.getKardexsigno());
            actualcantidad = next.getKardexultimacantidad();
            cantidad = next.getKardexcantidad();
            if ("+".equals(next.getKardexsigno())) {
                actualcantidad = actualcantidad.add(cantidad);
            } else {
                actualcantidad = actualcantidad.subtract(cantidad);
            }
            movimiento.setKardexultimacantidad(next.getKardexultimacantidad());
            movimiento.setKardexcantidad(cantidad);
            movimiento.setKardexactualcantidad(actualcantidad);
            listKardex.add(movimiento);
        }
        return listKardex;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        try {
            fechaI = Util.getXMLCalendar(fechaInicial.getValue().toString());
            fechaF = Util.getXMLCalendar(fechaFinal.getValue().toString());
            actualizarTabla(fechaI, fechaF);
        } catch (Exception ex) {
            Logger.getLogger(KardexMovController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    @SuppressWarnings("static-access")
    public void kardexImprimir() {
        try {
            //getEscena().setCursor(Cursor.WAIT);
            //Task<Void> task = new Task<Void>() {
            //    @Override
            //    protected Void call() throws Exception {
            generaReporte(insumo, fechaI, fechaF);
            //        return null;
            //    }

            //    @Override
            //    protected void succeeded() {
            //getEscena().setCursor(Cursor.DEFAULT);
            //this.setCursor(Cursor.DEFAULT);
            //    }
            //};
            //new Thread(task).start();
        } catch (Exception ex) {
            Logger.getLogger(KardexMovController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generaReporte(Insumo insumo, XMLGregorianCalendar fechaini, XMLGregorianCalendar fechafin) {
        try {
            // Rescata datos de la cabecera y detalle y pasa como parámetro
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("Empresa", getApp().getLoggedUser().getEmpresaid().getEmpresanombre());
            parametros.put("Usuario", getApp().getLoggedUser().getUsuarioidentidad());
            parametros.put("Nombre", insumo.getInsumonombre());
            //XMLGregorianCalendar fecha = new Date();
            String fechaI = LocalDate.of(fechaini.getYear(), fechaini.getMonth(), fechaini.getDay()).toString();
            parametros.put("FechaInicial", fechaI);
            fechaI = LocalDate.of(fechafin.getYear(), fechafin.getMonth(), fechafin.getDay()).toString();
            parametros.put("FechaFinal", fechaI);
            //JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(listKardex, true);
            String archivo = "C:\\Acuario\\KardexMovimientos.jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(archivo, parametros, new JRBeanCollectionDataSource(listaKardex));
            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.viewReport(jasperPrint, false);

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getLocalizedMessage());
            System.err.println("Error:" + ex.getLocalizedMessage());
        }

    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

}
