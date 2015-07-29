/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import asmor.hourfield.NumberTextField;
import acuario.services.Cliente;
import acuario.services.Ingreso;
import acuario.services.Ingresoforpag;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.SelectItem;
import acuario.clases.Util;
import acuario.webservices.Mantenimiento;
import asmor.textfield.TextFieldL;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class IngresoEdcFacController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    TextField clienteRUT;
    @FXML
    TextField clienteNombres;
    @FXML
    TextArea ingresoObservaciones;
    @FXML
    NumberTextField ingresoValor;
    @FXML
    NumberTextField ingresoImpuestos;
    @FXML
    NumberTextField ingresoDescuentos;
    @FXML
    NumberTextField ingresoAdicional;
    @FXML
    NumberTextField ingresoTotal;
    @FXML
    TextFieldL numeroFactura;
    @FXML
    ComboBox tipoFactura;
    @FXML
    TableColumn formapagoId;
    @FXML
    TableColumn formapagoDescripcion;
    @FXML
    TableColumn formapagoReferencia;
    @FXML
    TableColumn formapagoValor;
    @FXML
    NumberTextField formapagoSuman;
    @FXML
    TableView detalle;

    private Stage dialogStage;
    private Ingreso ingreso;
    private Cliente cliente;
    private boolean okClicked = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private boolean editMode;
    private List listaTipofactura;
    private List<Ingresoforpag> ingresoforpagList;
    private ObservableList<Ingresoforpag> ol;
    private Ingresoforpag ingresoforpag;
    private BigDecimal valorTotal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        Locale.setDefault(Locale.CANADA);
        llenaComboTipofactura();
        numeroFactura.setPrefWidth(150);
        numeroFactura.setMaxLength(30);
        valorTotal = BigDecimal.ZERO;
        ingresoforpagList = new ArrayList();
        tipoFactura.valueProperty().addListener(new ChangeListener<SelectItem>() {            
            @Override
            public void changed(ObservableValue<? extends SelectItem> observable, SelectItem oldValue, SelectItem newValue) {
                if ("CONIVA".equals(newValue.getId())) {
                    verificaIVA(true);
                } else {
                    verificaIVA(false);
                }
            }
        });
    }

    private void llenaComboTipofactura() {
        tipoFactura.getItems().clear();
        listaTipofactura = Util.getSelectConstante(Mantenimiento.findConstanteByCodigo("TIPFAC"), true);
        tipoFactura.getItems().addAll(listaTipofactura);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setIngreso(Ingreso ingreso) {
        this.ingreso = ingreso;
        dialogStage.getScene().setCursor(Cursor.WAIT);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }

            @Override
            protected void succeeded() {
                dialogStage.getScene().setCursor(Cursor.DEFAULT);
                //ingresoId.setText(ingreso.getIngresoid().toString());
                cliente = ingreso.getClienteid();
                clienteRUT.setText(cliente.getPersonaid().getPersonarut());
                clienteNombres.setText(cliente.getPersonaid().getPersonaapellidos() + " " + cliente.getPersonaid().getPersonanombres());
                ingresoObservaciones.setText(ingreso.getIngresoobservaciones());
                numeroFactura.setTexto(ingreso.getIngresofactura());
                tipoFactura.getSelectionModel().select(Util.indiceSeleccionado(listaTipofactura, ingreso.getIngresotipofactura()));
                ingresoValor.setNumber(ingreso.getIngresovalor());
                ingresoImpuestos.setNumber(ingreso.getIngresoimpuestos());
                ingresoDescuentos.setNumber(ingreso.getIngresodescuentos());
                ingresoAdicional.setNumber(ingreso.getIngresoadicional());
                ingresoTotal.setNumber(ingreso.getIngresovalor().
                        add(ingreso.getIngresoimpuestos().
                                subtract(ingreso.getIngresodescuentos())).
                        add(ingreso.getIngresoadicional()));
                ingresoDetalle();
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
            ingreso.setIngresoobservaciones(ingresoObservaciones.getText());
            ingreso.setIngresofactura(numeroFactura.getTexto());
            ingreso.setIngresotipofactura(Util.stringSeleccionado(tipoFactura));
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void btnCancela() {
        dialogStage.close();
    }

    private boolean datosValidos() {
        String errorMessage = "";

        if ("".equals(numeroFactura.getTexto())) {
            errorMessage += "Debe ingresar numero de Factura!\n";
        }
        if ("".equals(Util.stringSeleccionado(tipoFactura))) {
            errorMessage += "Debe ingresar tipo de factura!\n";
        }
        if (formapagoSuman.getNumber().compareTo(ingresoTotal.getNumber()) != 0) {
            errorMessage += "Desglose de forma de pago no cuadra con total!\n";
        }
        if (ol.size() == 0) {
            errorMessage += "Debe ingresar al menos una forma de pago!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create().title(dialogStage.getTitle()).masthead(null)
                    .message(errorMessage).showInformation();
            return false;
        }
    }

    public void ingresoDetalle() {
        if (ingresoforpagList.isEmpty()) {
            detalle.setPlaceholder(new Label("No existen registros..."));
        }
        ol = FXCollections.observableArrayList(ingresoforpagList);

        formapagoId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingresoforpag, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingresoforpag, String> param) {
                if (param.getValue() != null) {
                    String id = String.valueOf(param.getValue().getFormapago().getFormapagoid());
                    return new SimpleStringProperty(id);
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        formapagoDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Ingresoforpag, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Ingresoforpag, String> param) {
                if (param.getValue() != null) {
                    String nombre = String.valueOf(param.getValue().getFormapago().getFormapagodescripcion());
                    return new SimpleStringProperty(nombre);
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        formapagoReferencia.setCellValueFactory(new PropertyValueFactory<>("ingresoforpagreferencia1"));
        formapagoValor.setCellValueFactory(new PropertyValueFactory<>("ingresoforpagvalor"));

        MenuItem mnuDel = new MenuItem("Eliminar");
        mnuDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Ingresoforpag item = (Ingresoforpag) detalle.getSelectionModel().getSelectedItem();
                if (item != null) {
                    ol.remove(item);
                    detalle.getSelectionModel().selectLast();
                    valorTotal = valorTotal.subtract(item.getIngresoforpagvalor());
                    formapagoSuman.setNumber(valorTotal);
                } else {
                    Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                            .message("Registro no puede ser eliminado...").showInformation();
                }
            }
        });
        MenuItem mnuMod = new MenuItem("Modificar");
        mnuMod.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Ingresoforpag item = (Ingresoforpag) detalle.getSelectionModel().getSelectedItem();
                if (item != null) {
                    int id = ol.indexOf(item);
                    modificarFormapago(item, id);
                    detalle.getSelectionModel().selectFirst();
                } else {
                    // Nothing selected
                    Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                            .message("Seleccione un registro de la tabla.").showInformation();
                }
            }
        });
        MenuItem mnuNew = new MenuItem("Nuevo");
        mnuNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                nuevoIngresoformapago();
                detalle.getSelectionModel().selectLast();
            }
        });
        detalle.setContextMenu(new ContextMenu(mnuNew, mnuMod, mnuDel));
        detalle.setItems(ol);
    }

    public void nuevoIngresoformapago() {
        Ingresoforpag formapago = new Ingresoforpag();
        boolean okClicked = ingresoforpagEditar(formapago, "Crear");
        if (okClicked) {
            ol.add(formapago);
            valorTotal = valorTotal.add(formapago.getIngresoforpagvalor());
            formapagoSuman.setNumber(valorTotal);
        }
    }

    public void modificarFormapago(Ingresoforpag formapago, Integer id) {
        valorTotal = valorTotal.subtract(formapago.getIngresoforpagvalor());
        boolean okClicked = ingresoforpagEditar(formapago, "Editar");
        if (okClicked) {
            ol.set(id, formapago);
            valorTotal = valorTotal.add(formapago.getIngresoforpagvalor());
            formapagoSuman.setNumber(valorTotal);
        }
    }

    public boolean ingresoforpagEditar(Ingresoforpag ingresoforpag, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/IngresoforpagEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Forma de pago");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            IngresoforpagEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            controller.setApp(getApp());
            controller.Inicializar();
            controller.setIngresoforpag(ingresoforpag, modo);
            controller.setIngresoforpagList(ol);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                ingresoforpag = controller.getIngresoforpag();
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    public List<Ingresoforpag> getIngresoforpagList() {
        return ingresoforpagList;
    }

    public void setIngresoforpagList(List<Ingresoforpag> ingresoforpagList) {
        this.ingresoforpagList = ingresoforpagList;
    }

    private void verificaIVA(boolean iva) {        
        BigDecimal valor = BigDecimal.ZERO;
        //Actualiza impuestos en ingreso
        if (iva) {                    
            BigDecimal tasaiva = Mantenimiento.findConstanteByCodigoTipo("VALORES", "IVA").getConstantevalor();
            valor = ingreso.getIngresovalor().multiply(tasaiva);
            ingreso.setIngresoimpuestos(valor);
        } else {                        
            ingreso.setIngresoimpuestos(BigDecimal.ZERO);
        }
        valor = ingreso.getIngresovalor().
                        add(ingreso.getIngresoimpuestos().
                                subtract(ingreso.getIngresodescuentos())).
                        add(ingreso.getIngresoadicional());
        ingresoTotal.setNumber(valor);
        ingresoImpuestos.setNumber(ingreso.getIngresoimpuestos());
    }

    public ObservableList<Ingresoforpag> getOl() {
        return ol;
    }

    public void setOl(ObservableList<Ingresoforpag> ol) {
        this.ol = ol;
    }

}
