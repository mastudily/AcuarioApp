/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumoempresa;
import acuario.services.InsumoempresaPK;
import acuario.services.Kardex;
import acuario.services.Solicitudinsumo;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.KardexT;
import acuario.clases.EditingCell;
import acuario.clases.SelectItem;
import acuario.clases.Util;
import acuario.webservices.Farmacia;
import acuario.webservices.Mantenimiento;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.xml.datatype.XMLGregorianCalendar;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class SolicitudinsumoEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPadre;
    @FXML
    Button btnImagen;
    @FXML
    TextFieldL solicitudinsumoDescripcion;
    @FXML
    TextFieldL solicitudinsumoReferencia;
    @FXML
    ComboBox solicitudinsumoTipo;
    @FXML
    TextArea solicitudinsumoObservaciones;
    @FXML
    DatePicker solicitudinsumoFecha;
    @FXML
    TableColumn detalleInsumo;
    @FXML
    TableColumn detalleNombre;
    @FXML
    TableColumn detalleCantidad;
    @FXML
    TableColumn detallePrecio;
    @FXML
    TableColumn seleccion;
    @FXML
    TableView detalle;

    private Stage dialogStage;
    private Solicitudinsumo solicitudinsumo;
    private boolean okClicked = false;
    private List<KardexT> kardexT;
    private List<Kardex> kardex;
    private ObservableList<KardexT> ol;
    private ObservableList<Insumoempresa> listaInsumo;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private Integer empresaid;
    private List listaTipo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        solicitudinsumoDescripcion.setMaxLength(100);
        solicitudinsumoDescripcion.setPrefWidth(450);
        solicitudinsumoReferencia.setMaxLength(30);
        solicitudinsumoReferencia.setPrefWidth(300);
        List listaAux = Farmacia.findInsumoempresaByNombre("A", empresaid);
        listaInsumo = FXCollections.observableArrayList(Util.getSelectInsumo(listaAux, true));
        detalle.setId("my-table");
        llenaComboTipo();
        // Listen for selection changes
        solicitudinsumoTipo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                // Oculta columna precio cuando el tipo de solicitud es EGRESO
                if ("EGRESO".equals(newValue.toString())) {
                    detallePrecio.setVisible(false);
                } else {
                    detallePrecio.setVisible(true);
                }
            }

        });

    }

    private void llenaComboTipo() {
        solicitudinsumoTipo.getItems().clear();
        listaTipo = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("SOLINSTIP"), true);
        solicitudinsumoTipo.getItems().addAll(listaTipo);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setSolicitudinsumo(Solicitudinsumo solicitudinsumo, String modo) {
        this.solicitudinsumo = solicitudinsumo;
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
                    solicitudinsumoDescripcion.setTexto(solicitudinsumo.getSolicitudinsumodescripcion());
                    solicitudinsumoReferencia.setTexto(solicitudinsumo.getSolicitudinsumoreferencia());
                    solicitudinsumoObservaciones.setText(solicitudinsumo.getSolicitudinsumoobservaciones());
                    solicitudinsumoTipo.getSelectionModel().select(Util.indiceSeleccionado(listaTipo, solicitudinsumo.getSolicitudinsumotipo()));
                } else {
                    solicitudinsumoDescripcion.setTexto("");
                    solicitudinsumoReferencia.setTexto("");
                    solicitudinsumoObservaciones.setText("");
                }
                kardexDetalle();
            }
        };
        new Thread(task).start();

    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void btnAcepta() {
        if (datosValidos()) {
            try {
                solicitudinsumo.setSolicitudinsumodescripcion(solicitudinsumoDescripcion.getTexto());
                solicitudinsumo.setSolicitudinsumoreferencia(solicitudinsumoReferencia.getTexto());
                solicitudinsumo.setSolicitudinsumoobservaciones(solicitudinsumoObservaciones.getText());
                XMLGregorianCalendar fecha = Util.getXMLCalendar(sdf.format(new Date()));
                solicitudinsumo.setSolicitudinsumofecha(fecha);
                solicitudinsumo.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
                solicitudinsumo.setSolicitudinsumotipo(Util.stringSeleccionado(solicitudinsumoTipo));               
                solicitudinsumo.setSolicitudinsumoestado("PASIVO");
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(SolicitudinsumoEdcController.class.getName() + " " + ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if (solicitudinsumoDescripcion.getTexto() == null || solicitudinsumoDescripcion.getTexto().length() == 0) {
            errorMessage += "Debe ingresar descripcion de la solicitud!\n";
        }
        if (solicitudinsumoReferencia.getTexto() == null || solicitudinsumoReferencia.getTexto().length() == 0) {
            errorMessage += "Debe ingresar referencia!\n";
        }
        if ("".equals(Util.stringSeleccionado(solicitudinsumoTipo))) {
            errorMessage += "Debe ingresar tipo de solicitud!\n";
        }        
        if (ol.size() == 0) {
            errorMessage += "Debe ingresar al menos un insumo!\n";
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
        kardexT = new ArrayList();
        if ("Editar".equals(modo)) {
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
                kardexT.add(new KardexT(codigo, nombre, cantidad.doubleValue(), precio.doubleValue(), "ACT"));
            }
        }
    }

    public void kardexDetalle() {
        if (kardexT.isEmpty()) {
            detalle.setPlaceholder(new Label("No existen registros..."));
        }
        ol = FXCollections.observableArrayList(kardexT);

        Callback<TableColumn, TableCell> cellFactory
                = (TableColumn p) -> new EditingCell();

        detalleInsumo.setCellValueFactory(new PropertyValueFactory<>("codigoinsumo"));

        detalleNombre.setCellValueFactory(new PropertyValueFactory<KardexT, String>("nombreinsumo"));
        detalleNombre.setCellFactory(ComboBoxTableCell.forTableColumn(listaInsumo));
        detalleNombre.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<KardexT, SelectItem>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<KardexT, SelectItem> t) {
                        ((KardexT) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCodigoinsumo(new Integer(t.getNewValue().getId().toString()));
                        ((KardexT) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNombreinsumo(t.getNewValue().getDescription());
                    }
                });

        detalleCantidad.setCellValueFactory(new PropertyValueFactory<KardexT, Double>("cantidad"));
        detalleCantidad.setCellFactory(cellFactory);
        detalleCantidad.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<KardexT, Double>>() {

                    @Override
                    public void handle(TableColumn.CellEditEvent<KardexT, Double> t) {
                        int pos = t.getTablePosition().getRow();
                        ((KardexT) t.getTableView().getItems().get(
                                pos)).setCantidad(t.getNewValue());
                        //Valida que exista suficiente stock
                        Integer id = ((KardexT) t.getTableView().getItems().get(pos)).getCodigoinsumo().getValue();
                        Double cantidad = ((KardexT) t.getTableView().getItems().get(pos)).getCantidad().doubleValue();
                        if (!validaStock(id, cantidad)) {
                            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                            .message("No existe suficiente stock del insumo...").showInformation();
                            ((KardexT) t.getTableView().getItems().get(
                                    pos)).setCantidad(0);
                        }
                    }
                });
        detallePrecio.setCellValueFactory(new PropertyValueFactory<KardexT, Double>("precio"));
        detallePrecio.setCellFactory(cellFactory);
        detallePrecio.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<KardexT, Double>>() {

                    @Override
                    public void handle(TableColumn.CellEditEvent<KardexT, Double> t) {
                        int pos = t.getTablePosition().getRow();
                        ((KardexT) t.getTableView().getItems().get(
                                pos)).setPrecio(t.getNewValue());
                    }
                });

        MenuItem mnuDel = new MenuItem("Eliminar");
        mnuDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                KardexT item = (KardexT) detalle.getSelectionModel().getSelectedItem();
                if (item != null) {
                    ol.remove(item);
                    detalle.getSelectionModel().selectLast();
                }
            }
        });
        MenuItem mnuNew = new MenuItem("Nuevo");
        mnuNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ol.add(new KardexT(0, " ", 0, 0, "NUE"));
                detalle.getSelectionModel().selectLast();
            }
        });
        detalle.setContextMenu(new ContextMenu(mnuDel, mnuNew));
        detalle.setItems(ol);
    }

    private boolean validaStock(Integer id, Double cantidad) {
        boolean stockv = true;
        // Valida solo si es EGRESO
        if ("EGRESO".equals(Util.stringSeleccionado(solicitudinsumoTipo))) {
            InsumoempresaPK insumoempresapk = new InsumoempresaPK();
            insumoempresapk.setInsumoid(id);
            insumoempresapk.setEmpresaid(empresaid);
            Insumoempresa insumoempresa = Farmacia.findInsumoempresa(insumoempresapk);
            if (cantidad > insumoempresa.getInsumoempresastockactual().doubleValue()) {
                stockv = false;
            }
        }
        return stockv;
    }

    public ObservableList<KardexT> getOl() {
        return ol;
    }

    public void setOl(ObservableList<KardexT> ol) {
        this.ol = ol;
    }

    public List<Kardex> getDetallesolicitudinsumo() {
        return kardex;
    }

    public void setDetallesolicitudinsumo(List<Kardex> kardex) {
        this.kardex = kardex;
    }

}
