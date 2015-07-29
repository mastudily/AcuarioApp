/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cuentacontable;
import acuario.services.Diario;
import acuario.services.Diariodetalle;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.DiarioDetalleT;
import acuario.clases.EditingCell;
import acuario.clases.SelectItem;
import acuario.clases.Util;
import acuario.webservices.Financiero;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
public class DiarioEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPadre;
    @FXML
    Button btnImagen;
    @FXML
    TextArea diarioDescripcion;
    @FXML
    TextFieldL diarioReferencia;
    @FXML
    DatePicker diarioFecha;
    @FXML
    TableColumn detalleCuenta;
    @FXML
    TableColumn detalleNombre;
    @FXML
    TableColumn detalleDebe;
    @FXML
    TableColumn detalleHaber;
    @FXML
    TableColumn seleccion;
    @FXML
    TableView detalle;
    @FXML
    TextField detalleSaldo;
    @FXML
    TextField totalDebe;
    @FXML
    TextField totalHaber;

    private Stage dialogStage;
    private Diario diario;
    private boolean okClicked = false;
    private List<DiarioDetalleT> detallediarioT;
    private List<Diariodetalle> detallediario;
    private ObservableList<DiarioDetalleT> ol;
    private ObservableList<Cuentacontable> listaCuentas;
    private BigDecimal valorSaldo;
    private BigDecimal valorDebe;
    private BigDecimal valorHaber;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private Integer empresaid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        diarioReferencia.setMaxLength(100);
        diarioReferencia.setPrefWidth(450);
        valorSaldo = BigDecimal.ZERO;
        valorDebe = BigDecimal.ZERO;
        valorHaber = BigDecimal.ZERO;
        List listaAux = Financiero.findByCuentacontablemayor(false, empresaid);
        listaCuentas = FXCollections.observableArrayList(Util.getSelectCuentacontable(listaAux, true));
        detalle.setId("my-table");
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setDiario(Diario diario, String modo) {
        this.diario = diario;
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
                    diarioDescripcion.setText(diario.getDiariodescripcion());
                    diarioReferencia.setTexto(diario.getDiarioreferencia());
                } else {
                    diarioDescripcion.setText("");
                    diarioReferencia.setTexto("");
                }
                asientoDetalle();
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
                diario.setDiariodescripcion(diarioDescripcion.getText());
                diario.setDiarioreferencia(diarioReferencia.getTexto());
                XMLGregorianCalendar fecha = Util.getXMLCalendar(sdf.format(new Date()));
                diario.setDiariofecha(fecha);
                diario.setEmpresaid(getApp().getLoggedUser().getEmpresaid());
                diario.setDiarioestado("PASIVO");
                okClicked = true;
                dialogStage.close();
            } catch (Exception ex) {
                System.err.println(DiarioEdcController.class.getName() + " " + ex);
            }
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if (diarioDescripcion.getText() == null || diarioDescripcion.getText().length() == 0) {
            errorMessage += "Debe ingresar descripcion del diario!\n";
        }
        if (diarioReferencia.getTexto() == null || diarioReferencia.getTexto().length() == 0) {
            errorMessage += "Debe ingresar referencia!\n";
        }
        if (valorSaldo.compareTo(BigDecimal.ZERO) != 0) {
            errorMessage += "Saldo es inconsistente!\n";
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
        valorSaldo = BigDecimal.ZERO;
        detallediarioT = new ArrayList();
        if ("Editar".equals(modo)) {
            for (Iterator<Diariodetalle> iterator = detallediario.iterator(); iterator.hasNext();) {
                Diariodetalle detalled = (Diariodetalle) iterator.next();
                Cuentacontable cuentacontable = Financiero.findCuentacontable(detalled.getDiariodetallePK().getCuentacontableid());
                String codigo = cuentacontable.getCuentacontablecodigo();
                String nombre = cuentacontable.getCuentacontablenombre();
                valorSaldo = valorSaldo.add(detalled.getDiariodetalledebe());
                valorDebe = valorDebe.add(detalled.getDiariodetalledebe());
                valorSaldo = valorSaldo.subtract(detalled.getDiariodetallehaber());
                valorHaber = valorHaber.add(detalled.getDiariodetallehaber());
                detallediarioT.add(new DiarioDetalleT(codigo, nombre, detalled.getDiariodetalledebe().doubleValue(), detalled.getDiariodetallehaber().doubleValue()));
            }
        }
        detalleSaldo.setText(valorSaldo.toPlainString());
        totalDebe.setText(valorDebe.toPlainString());
        totalHaber.setText(valorHaber.toPlainString());
    }

    public void asientoDetalle() {
        if (detallediarioT.isEmpty()) {
            detalle.setPlaceholder(new Label("No existen registros..."));
        }
        ol = FXCollections.observableArrayList(detallediarioT);

        Callback<TableColumn, TableCell> cellFactory
                = (TableColumn p) -> new EditingCell();

        detalleCuenta.setCellValueFactory(new PropertyValueFactory<>("codigocuenta"));

        detalleNombre.setCellValueFactory(new PropertyValueFactory<DiarioDetalleT, String>("nombrecuenta"));
        detalleNombre.setCellFactory(ComboBoxTableCell.forTableColumn(listaCuentas));
        detalleNombre.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<DiarioDetalleT, SelectItem>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<DiarioDetalleT, SelectItem> t) {
                        ((DiarioDetalleT) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCodigocuenta(String.valueOf(t.getNewValue().getId()));
                        ((DiarioDetalleT) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNombrecuenta(t.getNewValue().getDescription());
                    }
                });

        detalleDebe.setCellValueFactory(new PropertyValueFactory<DiarioDetalleT, Double>("valordebe"));
        detalleDebe.setCellFactory(cellFactory);
        detalleDebe.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<DiarioDetalleT, Double>>() {

                    @Override
                    public void handle(TableColumn.CellEditEvent<DiarioDetalleT, Double> t) {
                        int pos = t.getTablePosition().getRow();
                        valorSaldo = valorSaldo.add(new BigDecimal(t.getNewValue()));
                        valorSaldo = valorSaldo.subtract(new BigDecimal(t.getOldValue()));
                        valorDebe = valorDebe.add(new BigDecimal(t.getNewValue()));
                        valorDebe = valorDebe.subtract(new BigDecimal(t.getOldValue()));
                        ((DiarioDetalleT) t.getTableView().getItems().get(
                                pos)).setValordebe(t.getNewValue());
                        detalleSaldo.setText(valorSaldo.toPlainString());
                        totalDebe.setText(valorDebe.toPlainString());
                    }
                });

        detalleHaber.setCellValueFactory(new PropertyValueFactory<DiarioDetalleT, Double>("valorhaber"));
        detalleHaber.setCellFactory(cellFactory);
        detalleHaber.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<DiarioDetalleT, Double>>() {

                    @Override
                    public void handle(TableColumn.CellEditEvent<DiarioDetalleT, Double> t) {
                        int pos = t.getTablePosition().getRow();
                        valorSaldo = valorSaldo.subtract(new BigDecimal(t.getNewValue()));
                        valorSaldo = valorSaldo.add(new BigDecimal(t.getOldValue()));
                        valorHaber = valorHaber.add(new BigDecimal(t.getNewValue()));
                        valorHaber = valorHaber.subtract(new BigDecimal(t.getOldValue()));
                        ((DiarioDetalleT) t.getTableView().getItems().get(
                                pos)).setValorhaber(t.getNewValue());
                        detalleSaldo.setText(valorSaldo.toPlainString());
                        totalHaber.setText(valorHaber.toPlainString());
                    }
                });

        MenuItem mnuDel = new MenuItem("Eliminar");
        mnuDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                DiarioDetalleT item = (DiarioDetalleT) detalle.getSelectionModel().getSelectedItem();
                if (item != null) {
                    valorSaldo = valorSaldo.add(new BigDecimal(item.getValorhaber().doubleValue()));
                    valorSaldo = valorSaldo.subtract(new BigDecimal(item.getValordebe().doubleValue()));
                    valorDebe = valorDebe.subtract(new BigDecimal(item.getValordebe().doubleValue()));
                    valorHaber = valorHaber.subtract(new BigDecimal(item.getValordebe().doubleValue()));
                    ol.remove(item);
                    detalleSaldo.setText(valorSaldo.toPlainString());
                    totalDebe.setText(valorDebe.toPlainString());
                    totalHaber.setText(valorHaber.toPlainString());
                    detalle.getSelectionModel().selectFirst();
                }
            }
        });
        MenuItem mnuNew = new MenuItem("Nuevo");
        mnuNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ol.add(new DiarioDetalleT(" ", " ", 0, 0));
                detalle.getSelectionModel().selectLast();
            }
        });
        detalle.setContextMenu(new ContextMenu(mnuDel, mnuNew));
        detalle.setItems(ol);
    }

    public ObservableList<DiarioDetalleT> getOl() {
        return ol;
    }

    public void setOl(ObservableList<DiarioDetalleT> ol) {
        this.ol = ol;
    }

    public List<Diariodetalle> getDetallediario() {
        return detallediario;
    }

    public void setDetallediario(List<Diariodetalle> detallediario) {
        this.detallediario = detallediario;
    }

}


/*
 detalleHaber.setCellFactory(cellFactory);
 detalleHaber.setOnEditCommit(
 new EventHandler<TableColumn.CellEditEvent<DiarioDetalleT, Double>>() {
 @Override
 public void handle(TableColumn.CellEditEvent<DiarioDetalleT, Double> t) {
 int pos = t.getTablePosition().getRow();
 double subtotal = t.getTableView().getItems().get(
 pos).getValorhaber().getValue() * t.getNewValue();
 double subtotalant = t.getTableView().getItems().get(
 pos).getValorhaber().getValue() * t.getOldValue();
 valorSaldo = valorSaldo.add(new BigDecimal(subtotalant));
 valorSaldo = valorSaldo.subtract(new BigDecimal(subtotal));
 }
 });
 //detalle.setOnMouseClicked((MouseEvent event) -> {
 //    detalle.edit(detalle.getSelectionModel().getSelectedIndex(), detalleHaber);
 //});

 */
