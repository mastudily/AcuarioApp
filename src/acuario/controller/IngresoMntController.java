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
import acuario.clases.GenerarAuditoria;
import acuario.clases.Util;
import acuario.services.Cuentacontable;
import acuario.services.Diario;
import acuario.services.Diariodetalle;
import acuario.services.DiariodetallePK;
import acuario.services.Ingresoforpag;
import acuario.services.IngresoforpagPK;
import acuario.services.Insumo;
import acuario.services.Insumoempresa;
import acuario.services.InsumoempresaPK;
import acuario.services.Kardex;
import acuario.services.KardexPK;
import acuario.services.Persona;
import acuario.services.Solicitudinsumo;
import acuario.webservices.Farmacia;
import acuario.webservices.Financiero;
import acuario.webservices.Mantenimiento;
import acuario.webservices.Transaccion;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.xml.datatype.XMLGregorianCalendar;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class IngresoMntController extends ControllerAM {

    @FXML
    Button btnNuevo;
    @FXML
    Button btnModificar;
    @FXML
    Button btnEliminar;
    @FXML
    Button btnDocumento;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn ingresoIdTC;
    @FXML
    TableColumn clienteIdentidadTC;
    @FXML
    TableColumn clienteNombresTC;
    @FXML
    TableColumn ingresoFechaTC;
    @FXML
    TableColumn ingresoHoraTC;
    @FXML
    TableColumn ingresoValorTC;

    @FXML
    TextField idBuscar;
    @FXML
    TextField rutBuscar;
    @FXML
    TextField nombreBuscar;
    @FXML
    DatePicker fechaBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Ingreso> listaIngresos = FXCollections.observableArrayList();
    private Cliente cliente;
    private Integer empresaid;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm:ss");
    private List<DetalleVentaT> ventaT;
    private List<Ingresoforpag> ingresoforpagList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        try {
            mensajeInformativo.setText("");
            Locale.setDefault(Locale.CANADA);
            empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
            XMLGregorianCalendar fecha = Util.getXMLCalendar(sdf.format(new Date()));
            fechaBuscar.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            inicializaTabla();
            datosTabla(fecha);
            // Listen for selection changes
            tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ingreso>() {

                @Override
                public void changed(ObservableValue<? extends Ingreso> observable,
                        Ingreso oldValue, Ingreso newValue) {
                    mensajeInformativo.setText("");
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(IngresoMntController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void datosTabla(XMLGregorianCalendar filtro) {
        listaIngresos = FXCollections.observableArrayList(Transaccion.findIngresoByFecha(filtro));
        actualizarTabla();
    }

    public void inicializaTabla() {
        ingresoIdTC.setCellValueFactory(new PropertyValueFactory<>("ingresoid"));
        clienteNombresTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingreso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingreso, String> p) {
                if (p.getValue() != null) {
                    String nombres = p.getValue().getClienteid().getPersonaid().getPersonaapellidos() + " " + p.getValue().getClienteid().getPersonaid().getPersonanombres();
                    return new SimpleStringProperty(nombres);
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        clienteIdentidadTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingreso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingreso, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getClienteid().getPersonaid().getPersonarut());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        ingresoFechaTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingreso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingreso, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(sdf2.format(p.getValue().getIngresofecha().toGregorianCalendar().getTime()));
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        ingresoHoraTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingreso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingreso, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(sdf3.format(p.getValue().getIngresohora().toGregorianCalendar().getTime()));
                    //return new SimpleStringProperty("<>");
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        ingresoValorTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingreso, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingreso, String> p) {
                if (p.getValue() != null) {
                    BigDecimal valor = p.getValue().getIngresovalor()
                            .add(p.getValue().getIngresoimpuestos());
                    return new SimpleStringProperty(valor.toPlainString());
                    //return new SimpleStringProperty("<>");
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
    }

    public void nuevoIngreso() {
        mensajeInformativo.setText("");
        Ingreso selectedIngreso = new Ingreso();
        boolean okClicked = ingresoEditar(selectedIngreso, "Crear");
        if (okClicked) {
            selectedIngreso.setSolicitudinsumoid(grabaSolicitud(selectedIngreso, "Crear"));
            selectedIngreso.setIngresoestado("PROCESADO");
            Integer registroid = Transaccion.createIdIngreso(selectedIngreso);
            selectedIngreso.setIngresoid(registroid);
            grabaDetalleForPag(selectedIngreso);
            listaIngresos.add(selectedIngreso);
            XMLGregorianCalendar filtro = selectedIngreso.getIngresofecha();
            datosTabla(filtro);
            GenerarAuditoria.grabaRegistro("Ingreso", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarIngreso() {
        mensajeInformativo.setText("");
        Ingreso selectedIngreso = (Ingreso) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedIngreso.getIngresoestado())) {
            boolean okClicked = ingresoEditar(selectedIngreso, "Editar");
            if (okClicked) {
                Integer registroid = selectedIngreso.getIngresoid();
                selectedIngreso.setSolicitudinsumoid(grabaSolicitud(selectedIngreso, "Editar"));
                grabaDetalleForPag(selectedIngreso);
                Transaccion.editIngreso(selectedIngreso);
                XMLGregorianCalendar filtro = selectedIngreso.getIngresofecha();
                datosTabla(filtro);
                GenerarAuditoria.grabaRegistro("Ingreso", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla, o registro no puede ser modificado...").showInformation();
        }
    }

    public void eliminarIngreso() {
        mensajeInformativo.setText("");
        Ingreso selectedIngreso = (Ingreso) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedIngreso.getIngresoestado())) {
            try {
                if (confirmaEliminacion()) {
                    Integer registroid = selectedIngreso.getIngresoid();
                    Transaccion.removeIngreso(selectedIngreso);
                    tabla.getItems().remove(selectedIndex);
                    GenerarAuditoria.grabaRegistro("Ingreso", registroid, "Eliminar", getApp().getLoggedUser().getUsuarioidentidad());
                    mensajeInformativo.setText("Datos eliminados satisfactoriamente...");
                }
            } catch (Exception e) {
                Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                        .message("Registro relacionado no se puede eliminar.").showError();
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla, o registro no puede ser eliminado...").showInformation();
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
    
    public void buscarIngreso() {
        try {
            XMLGregorianCalendar fecha = Util.getXMLCalendar(fechaBuscar.getValue().toString());
            datosTabla(fecha);
        } catch (Exception ex) {
            System.err.println(IngresoMntController.class.getName() + " " + ex);
        }
    }

    private boolean ingresoEditar(Ingreso ingreso, String modo) {
        if (ingresoFactura(ingreso, modo)) {
            return ingresoFormaPago(ingreso, modo);
        }
        return false;
    }
    
    private boolean ingresoFactura(Ingreso ingreso, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/IngresoEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Ingreso");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            IngresoEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setIngreso(ingreso, modo);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                ventaT = controller.getOl();
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            System.err.println(IngresoMntController.class.getName() + " " + e);
            return false;
        }
    }

    private boolean ingresoFormaPago(Ingreso ingreso, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/IngresoEdcFac.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Forma de Pago");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            IngresoEdcFacController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setIngreso(ingreso);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                ingresoforpagList = controller.getOl();
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            System.err.println(IngresoMntController.class.getName() + " " + e);
            return false;
        }
    }
    
    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaIngresos);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
    }

    private Solicitudinsumo grabaSolicitud(Ingreso ingreso, String modo) {        
        Solicitudinsumo solicitudinsumo = grabaCabecera(ingreso);
        BigDecimal valor = grabaDetalle(solicitudinsumo);
        BigDecimal tasa = Mantenimiento.findConstanteByCodigoTipo("VALORES", "IVA").getConstantevalor();
        BigDecimal impuesto = valor.multiply(tasa);
        ingreso.setIngresovalor(valor);
        ingreso.setIngresoimpuestos(impuesto);
        Integer solicitudid = solicitudinsumo.getSolicitudinsumoid();
        GenerarAuditoria.grabaRegistro("Solicitudinsumo", solicitudid, "Venta", getApp().getLoggedUser().getUsuarioidentidad());
        return solicitudinsumo;
    }

    private Solicitudinsumo grabaCabecera(Ingreso ingreso) {        
        Solicitudinsumo solicitudinsumo = null;
        try {
            solicitudinsumo = Farmacia.findSolicitudinsumoid(ingreso.getSolicitudinsumoid().getSolicitudinsumoid());
        } catch (Exception e) {
            solicitudinsumo = new Solicitudinsumo();
        }        
        solicitudinsumo.setEmpresaid(ingreso.getEmpresaid());
        solicitudinsumo.setSolicitudinsumodescripcion("Ingreso por venta");
        solicitudinsumo.setSolicitudinsumofecha(ingreso.getIngresofecha());
        Persona persona = ingreso.getClienteid().getPersonaid();
        solicitudinsumo.setSolicitudinsumoobservaciones(persona.getPersonarut()+"\n"+persona.getPersonaapellidos()+" "+persona.getPersonanombres()+"\n"+ingreso.getIngresoobservaciones());
        solicitudinsumo.setSolicitudinsumotipo("EGRESO");
        solicitudinsumo.setSolicitudinsumoreferencia(ingreso.getClienteid().getClienteid().toString());
        solicitudinsumo.setSolicitudinsumoestado("PROCESADO");
        Integer solicitudid = Farmacia.createIdSolicitudinsumo(solicitudinsumo);
        solicitudinsumo.setSolicitudinsumoid(solicitudid);
        return solicitudinsumo;
    }
    
    private BigDecimal grabaDetalle(Solicitudinsumo solicitudinsumo) {
        BigDecimal valor = BigDecimal.ZERO;
        // Borra el detalle
        borraDetalle(solicitudinsumo.getSolicitudinsumoid());
        for (Iterator<DetalleVentaT> iterator = ventaT.iterator(); iterator.hasNext();) {
            DetalleVentaT detalle = (DetalleVentaT) iterator.next();
            Integer codigo = detalle.getCodigo().getValue();
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
                kardex.setKardexusado(cantidad);
                kardex.setKardexultimacantidad(insumoempresa.getInsumoempresastockactual());
                kardex.setKardexfecha(solicitudinsumo.getSolicitudinsumofecha());
                kardex.setKardextipo(solicitudinsumo.getSolicitudinsumotipo());
                kardex.setKardexsigno("-");
                Insumo insumo = Farmacia.findInsumo(codigo);
                kardex.setKardexprecio(insumo.getInsumovalor());
                kardex.setKardexestado("PROCESADO");
                Farmacia.createKardex(kardex);
                valor = valor.add(precio.multiply(cantidad));
            }
        }
        return valor;
    }

    private void borraDetalle(Integer solicitudid) {
        List<Kardex> kardex = Farmacia.findBySolicitudinsumoid(solicitudid);
        for (Iterator<Kardex> iterator = kardex.iterator(); iterator.hasNext();) {
            Kardex next = iterator.next();
            Farmacia.removeKardex(next);
        }
    }

    private void grabaDetalleForPag(Ingreso ingreso) {
        Diario diario = null;
        for (Iterator<Ingresoforpag> iterator = ingresoforpagList.iterator(); iterator.hasNext();) {
            diario = diario == null ? valorContable(ingreso) : diario;
            Ingresoforpag next = iterator.next();
            IngresoforpagPK nuevoPK = new IngresoforpagPK();
            nuevoPK.setIngresoid(ingreso.getIngresoid());
            nuevoPK.setFormapagoid(next.getFormapago().getFormapagoid());
            if (Transaccion.findIngresoforpag(nuevoPK) != null) {
                Transaccion.editIngresoforpag(next);
            } else  {
                next.setIngreso(ingreso);
                next.setFormapago(next.getFormapago());
                next.setIngresoforpagPK(nuevoPK);
                next.setIngresoforpagestado("PROCESADO");
                Transaccion.createIngresoforpag(next);
                Cuentacontable cuenta = next.getFormapago().getFormapagoctacble();
                valorContableDetalle(diario, cuenta, next.getIngresoforpagvalor(), true);
                cuenta = next.getFormapago().getFormapagoconctacble();
                valorContableDetalle(diario, cuenta, next.getIngresoforpagvalor(), false);
            }
        }
    }
    
    private Diario valorContable(Ingreso ingreso) {
        Diario diario = new Diario();
        diario.setDiariofecha(Util.fechaSistema());
        Persona persona = ingreso.getClienteid().getPersonaid();
        String descripcion = " RUT:" + persona.getPersonarut() + "\n" + persona.getPersonaapellidos() + " " + persona.getPersonanombres();
        descripcion = descripcion.concat("\n" + ingreso.getIngresoobservaciones());
        diario.setDiariodescripcion(descripcion);
        diario.setDiarioreferencia("Ingreso #:" + ingreso.getIngresoid().toString());
        diario.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
        diario.setDiarioestado("PROCESADO");
        Integer registroid = Financiero.createIdDiario(diario);
        diario.setDiarioid(registroid);
        return diario;
    }

    private void valorContableDetalle(Diario diario, Cuentacontable cuenta, BigDecimal valor, boolean ctacbe) {
        DiariodetallePK detallePK = new DiariodetallePK();
        detallePK.setDiarioid(diario.getDiarioid());
        detallePK.setCuentacontableid(cuenta.getCuentacontableid());
        BigDecimal debe = BigDecimal.ZERO;
        BigDecimal haber = BigDecimal.ZERO;
        if (ctacbe) {
            debe = valor;
        } else {
            haber = valor;
        }
        // Verifica si ya existe transaccion        
        Diariodetalle detalle = Financiero.findDiariodetalle(detallePK);
        if (detalle == null) {
            detalle = new Diariodetalle();
            detalle.setDiariodetallePK(detallePK);
            detalle.setDiario(diario);
            detalle.setDiariodetalledebe(debe);
            detalle.setDiariodetallehaber(haber);
            detalle.setDiariodetalleestado("PROCESADO");
            Financiero.createDiariodetalle(detalle);
        } else {
            debe = debe.add(detalle.getDiariodetalledebe());
            detalle.setDiariodetalledebe(debe);
            haber = haber.add(detalle.getDiariodetallehaber());
            detalle.setDiariodetallehaber(haber);
            Financiero.editDiariodetalle(detalle);
        }
    }
    
    public ObservableList<Ingreso> getListaIngresos() {
        return listaIngresos;
    }

}
