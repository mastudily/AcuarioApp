/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumo;
import acuario.services.Insumoempresa;
import acuario.services.InsumoempresaPK;
import acuario.services.Solicitudinsumo;
import acuario.services.Kardex;
import acuario.services.KardexPK;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.KardexT;
import acuario.clases.GenerarAuditoria;
import acuario.clases.SolInsReporte;
import acuario.clases.Util;
import acuario.webservices.Farmacia;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import javax.xml.datatype.XMLGregorianCalendar;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class SolicitudinsumoMntController extends ControllerAM {

    @FXML
    Button btnNuevo;
    @FXML
    Button btnModificar;
    @FXML
    Button btnEliminar;
    @FXML
    Button btnProcesar;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn solicitudinsumoReferenciaTC;
    @FXML
    TableColumn solicitudinsumoFechaTC;
    @FXML
    TextField solicitudinsumoDescripcion;
    @FXML
    TextField solicitudinsumoReferencia;
    @FXML
    TextArea solicitudinsumoObservaciones;
    @FXML
    DatePicker solicitudinsumoFecha;
    @FXML
    DatePicker solicitudinsumoBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Solicitudinsumo> listaSolicitudinsumo = FXCollections.observableArrayList();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
    private Integer empresaid;
    private List<KardexT> kardexT;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        try {
            Locale.setDefault(Locale.CANADA);
            XMLGregorianCalendar fecha = Util.getXMLCalendar(sdf.format(new Date()));
            solicitudinsumoBuscar.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
            inicializaTabla();
            datosTabla(fecha);
            Solicitudinsumo solicitudinsumo = (Solicitudinsumo) tabla.getSelectionModel().getSelectedItem();
            if (solicitudinsumo != null) {
                datosSolicitudinsumo(solicitudinsumo);
            }
            // Listen for selection changes
            tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Solicitudinsumo>() {

                @Override
                public void changed(ObservableValue<? extends Solicitudinsumo> observable,
                        Solicitudinsumo oldValue, Solicitudinsumo newValue) {
                    mensajeInformativo.setText("");
                    datosSolicitudinsumo(newValue);
                }
            });
        } catch (Exception ex) {
            System.err.println(SolicitudinsumoMntController.class.getName() + " " + ex);
        }
    }

    public void datosTabla(XMLGregorianCalendar filtro) {
        listaSolicitudinsumo = FXCollections.observableArrayList(Farmacia.findSolicitudinsumoByFecha(filtro, empresaid));
        actualizarTabla();
    }

    public void inicializaTabla() {
        solicitudinsumoReferenciaTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Solicitudinsumo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Solicitudinsumo, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getSolicitudinsumoreferencia());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        solicitudinsumoFechaTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Solicitudinsumo, Date>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Solicitudinsumo, Date> p) {
                if (p.getValue() != null) {
                    Calendar calendar = p.getValue().getSolicitudinsumofecha().toGregorianCalendar();
                    return new SimpleStringProperty(sdf2.format(calendar.getTime()));
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        //solicitudinsumoFechaTC.setCellValueFactory(new PropertyValueFactory<>("solicitudinsumofecha"));
    }

    private void datosSolicitudinsumo(Solicitudinsumo solicitudinsumo) {
        if (solicitudinsumo != null) {
            try {
                solicitudinsumoDescripcion.setText(solicitudinsumo.getSolicitudinsumodescripcion());
                solicitudinsumoReferencia.setText(solicitudinsumo.getSolicitudinsumoreferencia());
                XMLGregorianCalendar fecha = solicitudinsumo.getSolicitudinsumofecha();
                solicitudinsumoFecha.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
                solicitudinsumoObservaciones.setText(solicitudinsumo.getSolicitudinsumoobservaciones());
                if ("PROCESADO".equals(solicitudinsumo.getSolicitudinsumoestado())) {
                    btnProcesar.setText("Imprimir");
                    btnProcesar.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            solicitudinsumoImprimir();
                        }
                    });
                } else {
                    btnProcesar.setText("Procesar");
                    btnProcesar.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            solicitudinsumoProcesar();
                        }
                    });
                }
            } catch (Exception ex) {
                System.err.println(SolicitudinsumoMntController.class.getName() + " " + ex);
            }
        } else {
            solicitudinsumoDescripcion.setText("");
            solicitudinsumoReferencia.setText("");
            solicitudinsumoFecha.setValue(null);
            solicitudinsumoObservaciones.setText("");
        }

    }

    public void nuevoSolicitudinsumo() {
        Solicitudinsumo selectedSolicitudinsumo = new Solicitudinsumo();
        boolean okClicked = solicitudinsumoEditar(selectedSolicitudinsumo, "Crear");
        if (okClicked) {
            Integer registroid = Farmacia.createIdSolicitudinsumo(selectedSolicitudinsumo);
            if (registroid != -1) {
                selectedSolicitudinsumo.setSolicitudinsumoid(registroid);
                grabaDatos(selectedSolicitudinsumo, registroid, "Crear");
                listaSolicitudinsumo.add(selectedSolicitudinsumo);
            } else {
                mensajeInformativo.setText("No fue posible la creacion del registro...");
            }
        }
    }

    public void modificarSolicitudinsumo() {
        Solicitudinsumo selectedSolicitudinsumo = (Solicitudinsumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedSolicitudinsumo.getSolicitudinsumoestado())) {
            boolean okClicked = solicitudinsumoEditar(selectedSolicitudinsumo, "Editar");
            if (okClicked) {
                Integer registroid = selectedSolicitudinsumo.getSolicitudinsumoid();
                Farmacia.editSolicitudinsumo(selectedSolicitudinsumo);
                grabaDatos(selectedSolicitudinsumo, registroid, "Editar");
            }

        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Debe seleccionar un registro de la tabla o registro no puede ser modificado...").showInformation();
        }
    }

    public void eliminarSolicitudinsumo() {
        Solicitudinsumo selectedSolicitudinsumo = (Solicitudinsumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedSolicitudinsumo.getSolicitudinsumoestado())) {
            try {
                if (confirmaEliminacion()) {
                    borraDetalle(selectedSolicitudinsumo);
                    Farmacia.removeSolicitudinsumo(selectedSolicitudinsumo);
                    tabla.getItems().remove(selectedIndex);
                    mensajeInformativo.setText("Datos eliminados satisfactoriamente...");
                }
            } catch (Exception e) {
                Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                        .message("Registro relacionado no se puede eliminar.").showError();
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Debe seleccionar un registro de la tabla o registro no puede ser eliminado...").showInformation();
        }
    }

    private boolean confirmaEliminacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("");
        alert.setContentText("Esta seguro de eliminar el registro?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void buscarSolicitudinsumo() {
        try {
            XMLGregorianCalendar fecha = Util.getXMLCalendar(solicitudinsumoBuscar.getValue().toString());
            datosTabla(fecha);
        } catch (Exception ex) {
            System.err.println(SolicitudinsumoMntController.class.getName() + " " + ex);
        }
    }

    public boolean solicitudinsumoEditar(Solicitudinsumo solicitudinsumo, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup

            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/SolicitudinsumoEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Solicitud insumo");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            SolicitudinsumoEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            List<Kardex> kardex = Farmacia.findBySolicitudinsumoid(solicitudinsumo.getSolicitudinsumoid());
            controller.setDetallesolicitudinsumo(kardex);
            controller.setSolicitudinsumo(solicitudinsumo, modo);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                kardexT = controller.getOl();
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaSolicitudinsumo);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        datosSolicitudinsumo((Solicitudinsumo) tabla.getSelectionModel().getSelectedItem());
        mensajeInformativo.setText("");
    }

    public ObservableList<Solicitudinsumo> getListaSolicitudinsumos() {
        return listaSolicitudinsumo;
    }

    private void grabaDatos(Solicitudinsumo solicitudinsumo, Integer registroid, String modo) {
        getEscena().setCursor(Cursor.WAIT);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                grabaDetalle(solicitudinsumo);
                GenerarAuditoria.grabaRegistro("Solicitudinsumo", registroid, modo, getApp().getLoggedUser().getUsuarioidentidad());
                return null;
            }

            @Override
            protected void succeeded() {
                getEscena().setCursor(Cursor.DEFAULT);
                XMLGregorianCalendar filtro = solicitudinsumo.getSolicitudinsumofecha();
                datosTabla(filtro);
                if ("Crear".equals(modo)) {
                    mensajeInformativo.setText("Datos creados satisfactoriamente...");
                } else {
                    mensajeInformativo.setText("Datos modificados satisfactoriamente...");
                }
            }
        };
        new Thread(task).start();
    }

    private void grabaDetalle(Solicitudinsumo solicitudinsumo) {
        // Borra el detalle
        borraDetalle(solicitudinsumo);
        for (Iterator<KardexT> iterator = kardexT.iterator(); iterator.hasNext();) {
            KardexT detalle = (KardexT) iterator.next();
            Integer codigo = detalle.getCodigoinsumo().getValue();
            BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidad().getValue());
            BigDecimal precio = BigDecimal.valueOf(detalle.getPrecio().getValue());
            Kardex kardex = null;
            if (codigo != 0) {
                kardex = new Kardex();
                kardex.setSolicitudinsumo(solicitudinsumo);
                InsumoempresaPK insumoempresapk = new InsumoempresaPK();
                insumoempresapk.setInsumoid(codigo);
                insumoempresapk.setEmpresaid(empresaid);
                Insumoempresa insumoempresa = Farmacia.findInsumoempresa(insumoempresapk);
                kardex.setInsumoempresa(insumoempresa);
                KardexPK kardexpk = new KardexPK();
                kardexpk.setInsumoid(codigo);
                kardexpk.setSolicitudinsumoid(solicitudinsumo.getSolicitudinsumoid());
                kardexpk.setEmpresaid(empresaid);
                kardex.setKardexPK(kardexpk);
                kardex.setKardexcantidad(cantidad);
                kardex.setKardexultimacantidad(insumoempresa.getInsumoempresastockactual());
                kardex.setKardexfecha(solicitudinsumo.getSolicitudinsumofecha());
                kardex.setKardextipo(solicitudinsumo.getSolicitudinsumotipo());
                kardex.setKardexsigno("-");
                Insumo insumo = Farmacia.findInsumo(codigo);
                if ("INGRESO".equals(solicitudinsumo.getSolicitudinsumotipo())) {
                    kardex.setKardexsigno("+");
                    kardex.setKardexprecio(precio);
                    // Actualiza precio del insumo
                    insumo.setInsumovalor(precio);
                    GenerarAuditoria.grabaRegistro("Insumo", insumo.getInsumoid(), "Precio", getApp().getLoggedUser().getUsuarioidentidad());
                    Farmacia.editInsumo(insumo);
                } else {
                    kardex.setKardexprecio(insumo.getInsumovalor());
                }
                kardex.setKardexestado("PASIVO");
                Farmacia.createKardex(kardex);
            }
        }
    }

    private void borraDetalle(Solicitudinsumo solicitudinsumo) {
        List<Kardex> kardex = Farmacia.findBySolicitudinsumoid(solicitudinsumo.getSolicitudinsumoid());
        for (Iterator<Kardex> iterator = kardex.iterator(); iterator.hasNext();) {
            Kardex next = iterator.next();
            Farmacia.removeKardex(next);
        }
    }

    public void solicitudinsumoProcesar() {
        Solicitudinsumo selectedSolicitudinsumo = (Solicitudinsumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedSolicitudinsumo.getSolicitudinsumoestado())) {
            getEscena().setCursor(Cursor.WAIT);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Integer registroid = selectedSolicitudinsumo.getSolicitudinsumoid();
                    selectedSolicitudinsumo.setSolicitudinsumoestado("PRCESADO");
                    Farmacia.editSolicitudinsumo(selectedSolicitudinsumo);
                    procesaDetalle(selectedSolicitudinsumo);
                    GenerarAuditoria.grabaRegistro("Solicitudinsumo", registroid, "Procesar", getApp().getLoggedUser().getUsuarioidentidad());
                    return null;
                }

                @Override
                protected void succeeded() {
                    getEscena().setCursor(Cursor.DEFAULT);
                    generaReporte(selectedSolicitudinsumo);
                    XMLGregorianCalendar filtro = selectedSolicitudinsumo.getSolicitudinsumofecha();
                    datosTabla(filtro);
                    mensajeInformativo.setText("Datos procesados satisfactoriamente...");
                    //this.setCursor(Cursor.DEFAULT);
                }
            };
            new Thread(task).start();
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Debe seleccionar un registro de la tabla o registro no puede ser procesado...").showInformation();
        }
    }

    private void procesaDetalle(Solicitudinsumo solicitudinsumo) {
        List<Kardex> kardex = Farmacia.findBySolicitudinsumoid(solicitudinsumo.getSolicitudinsumoid());
        for (Iterator<Kardex> iterator = kardex.iterator(); iterator.hasNext();) {
            Kardex next = iterator.next();
            Insumoempresa insumoempresa = next.getInsumoempresa();
            BigDecimal stock = insumoempresa.getInsumoempresastockactual();
            next.setKardexultimacantidad(stock);
            // Actualiza stock actual
            if ("+".equals(next.getKardexsigno())) {
                insumoempresa.setInsumoempresastockactual(stock.add(next.getKardexcantidad()));
            } else {
                insumoempresa.setInsumoempresastockactual(stock.subtract(next.getKardexcantidad()));
            }
            Farmacia.editInsumoempresa(insumoempresa);
            next.setKardexestado("PROCESADO");
            Farmacia.editKardex(next);
        }
    }

    @SuppressWarnings("static-access")
    public void solicitudinsumoImprimir() {
        Solicitudinsumo selectedSolicitudinsumo = (Solicitudinsumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PROCESADO".equals(selectedSolicitudinsumo.getSolicitudinsumoestado())) {
            getEscena().setCursor(Cursor.WAIT);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    generaReporte(selectedSolicitudinsumo);
                    return null;
                }

                @Override
                protected void succeeded() {
                    getEscena().setCursor(Cursor.DEFAULT);
                    //this.setCursor(Cursor.DEFAULT);
                }
            };
            new Thread(task).start();
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Debe seleccionar un registro de la tabla o registro no puede ser impreso...").showInformation();
        }
    }

    private List<SolInsReporte> detalleReporte(Solicitudinsumo solicitud) {
        List<SolInsReporte> listKardex = new ArrayList();
        List<Kardex> kardex = Farmacia.findBySolicitudinsumoid(solicitud.getSolicitudinsumoid());
        for (Iterator<Kardex> iterator = kardex.iterator(); iterator.hasNext();) {
            Kardex next = iterator.next();
            SolInsReporte solinsreporte = new SolInsReporte();
            Insumo insumo = Farmacia.findByInsumoid(next.getKardexPK().getInsumoid());
            solinsreporte.setInsumocodigo(insumo.getInsumocodigo());
            solinsreporte.setInsumonombre(insumo.getInsumonombre());
            solinsreporte.setInsumounmed(insumo.getInsumounmed());
            solinsreporte.setInsumopresentacion(insumo.getInsumopresentacion());
            solinsreporte.setKardexcantidad(next.getKardexcantidad());
            solinsreporte.setKardexusado(next.getKardexusado());
            listKardex.add(solinsreporte);
        }
        return listKardex;
    }

    private void generaReporte(Solicitudinsumo solicitud) {
        try {
            // Rescata datos de la cabecera y detalle y pasa como parámetro
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("Empresa", getApp().getLoggedUser().getEmpresaid().getEmpresanombre());
            parametros.put("Usuario", getApp().getLoggedUser().getUsuarioidentidad());
            parametros.put("Descripcion", solicitud.getSolicitudinsumodescripcion());
            parametros.put("Referencia", solicitud.getSolicitudinsumoreferencia());
            XMLGregorianCalendar fecha = solicitud.getSolicitudinsumofecha();
            String fechad = LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()).toString();
            parametros.put("Fecha", fechad);
            parametros.put("Id", solicitud.getSolicitudinsumoid());
            parametros.put("Empleado", "");
            parametros.put("Tipo", solicitud.getSolicitudinsumotipo());
            //JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(listKardex, true);
            String archivo = "C:\\Acuario\\SolicitudInsumo.jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(archivo, parametros, new JRBeanCollectionDataSource(detalleReporte(solicitud)));
            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.viewReport(jasperPrint, false);

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getLocalizedMessage());
            System.err.println("Error:" + ex.getLocalizedMessage());
        }

    }
}
