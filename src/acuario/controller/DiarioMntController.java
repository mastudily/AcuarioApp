/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cuentacontable;
import acuario.services.Diario;
import acuario.services.Diariodetalle;
import acuario.services.DiariodetallePK;
import acuario.services.Ingreso;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.DiarioDetalleT;
import acuario.clases.GenerarAuditoria;
import acuario.clases.InformeContable;
import acuario.clases.Util;
import acuario.webservices.Financiero;
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
public class DiarioMntController extends ControllerAM {

    @FXML
    Button btnNuevo;
    @FXML
    Button btnModificar;
    @FXML
    Button btnEliminar;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn diarioReferenciaTC;
    @FXML
    TableColumn diarioFechaTC;
    @FXML
    TextArea diarioDescripcion;
    @FXML
    TextField diarioReferencia;
    @FXML
    DatePicker diarioFecha;
    @FXML
    DatePicker diarioBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Diario> listaDiario = FXCollections.observableArrayList();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
    private Integer empresaid;
    private List<DiarioDetalleT> diariodetalleT;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        try {
            Locale.setDefault(Locale.CANADA);
            XMLGregorianCalendar fecha = Util.getXMLCalendar(sdf.format(new Date()));
            diarioBuscar.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
            inicializaTabla();
            datosTabla(fecha);
            Diario diario = (Diario) tabla.getSelectionModel().getSelectedItem();
            if (diario != null) {
                datosDiario(diario);
            }
            // Listen for selection changes
            tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Diario>() {

                @Override
                public void changed(ObservableValue<? extends Diario> observable,
                        Diario oldValue, Diario newValue) {
                    mensajeInformativo.setText("");
                    datosDiario(newValue);
                }
            });
        } catch (Exception ex) {
            System.err.println(DiarioMntController.class.getName() + " " + ex);
        }
    }

    public void datosTabla(XMLGregorianCalendar filtro) {
        listaDiario = FXCollections.observableArrayList(Financiero.findDiarioByFecha(filtro, empresaid));
        actualizarTabla();
    }

    public void inicializaTabla() {
        diarioReferenciaTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Diario, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Diario, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getDiarioreferencia());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        diarioFechaTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Diario, Date>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Diario, Date> p) {
                if (p.getValue() != null) {
                    Calendar calendar = p.getValue().getDiariofecha().toGregorianCalendar();
                    return new SimpleStringProperty(sdf2.format(calendar.getTime()));
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        //diarioFechaTC.setCellValueFactory(new PropertyValueFactory<>("diariofecha"));
    }

    private void datosDiario(Diario diario) {
        if (diario != null) {
            try {
                diarioDescripcion.setText(diario.getDiariodescripcion());
                diarioReferencia.setText(diario.getDiarioreferencia());
                XMLGregorianCalendar fecha = diario.getDiariofecha();
                diarioFecha.setValue(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDay()));
            } catch (Exception ex) {
                System.err.println(DiarioMntController.class.getName() + " " + ex);
            }
        } else {
            diarioDescripcion.setText("");
            diarioReferencia.setText("");
            diarioFecha.setValue(null);
        }

    }

    public void nuevoDiario() {
        Diario selectedDiario = new Diario();
        boolean okClicked = diarioEditar(selectedDiario, "Crear");
        if (okClicked) {
            Integer registroid = Financiero.createIdDiario(selectedDiario);
            if (registroid != -1) {
                selectedDiario.setDiarioid(registroid);
                grabaDatos(selectedDiario, registroid, "Crear");
                listaDiario.add(selectedDiario);
            } else {
                mensajeInformativo.setText("No fue posible la creacion del registro...");
            }
        }
    }

    public void modificarDiario() {
        Diario selectedDiario = (Diario) tabla.getSelectionModel().getSelectedItem();
        if (selectedDiario != null) {
            boolean okClicked = diarioEditar(selectedDiario, "Editar");
            if (okClicked) {
                Integer registroid = selectedDiario.getDiarioid();
                Financiero.editDiario(selectedDiario);
                grabaDatos(selectedDiario, registroid, "Editar");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarDiario() {
        Diario selectedDiario = (Diario) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedDiario.getDiarioestado())) {
            try {
                if (confirmaEliminacion()) {
                    borraDetalle(selectedDiario);
                    Financiero.removeDiario(selectedDiario);
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
                    .message("Seleccione un registro de la tabla o registro no puede ser eliminado...").showInformation();
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

    public void buscarDiario() {
        try {
            XMLGregorianCalendar fecha = Util.getXMLCalendar(diarioBuscar.getValue().toString());
            datosTabla(fecha);
        } catch (Exception ex) {
            System.err.println(DiarioMntController.class.getName() + " " + ex);
        }
    }

    public boolean diarioEditar(Diario diario, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup             
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/DiarioEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Diario");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            DiarioEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            List<Diariodetalle> diariodetalle = Financiero.findByDiarioid(diario.getDiarioid());
            controller.setDetallediario(diariodetalle);
            controller.setDiario(diario, modo);
            // Show the dialog and wait until the user closes it

            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                diariodetalleT = controller.getOl();
            }
            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    public void informeContable() {
        try {
            // Load the fxml file and create a new stage for the popup             
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/ProcesosFecha.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(" Informe contable");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            ProcesosFechaController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.Inicializar();
            // Show the dialog and wait until the user closes it

            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                // Genera informe
                informeImprimir(controller.getFechaInicial(), controller.getFechaFinal());
            }

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }

    public void informeImprimir(XMLGregorianCalendar fechaInicial, XMLGregorianCalendar fechaFinal) {
        try {
            // Rescata datos de la cabecera y detalle y pasa como parámetro
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("Empresa", getApp().getLoggedUser().getEmpresaid().getEmpresanombre());
            parametros.put("Usuario", getApp().getLoggedUser().getUsuarioidentidad());
            parametros.put("FechaInicial", fechaInicial.toString());
            parametros.put("FechaFinal", fechaFinal.toString());
            parametros.put("Empleado", getApp().getLoggedUser().getPersonaid().getPersonaapellidos() + " " + getApp().getLoggedUser().getPersonaid().getPersonanombres());
            String archivo = "C:\\Acuario\\InformeContable.jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(archivo, parametros, new JRBeanCollectionDataSource(detalleReporte(fechaInicial, fechaFinal)));
            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.viewReport(jasperPrint, false);

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getLocalizedMessage());
            System.err.println("Error:" + ex.getLocalizedMessage());
        }
    }

    private List<InformeContable> detalleReporte(XMLGregorianCalendar fechaInicial, XMLGregorianCalendar fechaFinal) {
        List<InformeContable> listInforme = new ArrayList();
        List<Diariodetalle> diario = Financiero.findDiariodetalleByFecha(fechaInicial, fechaFinal, empresaid);
        for (Iterator<Diariodetalle> iterator = diario.iterator(); iterator.hasNext();) {
            Diariodetalle next = iterator.next();
            InformeContable informe = new InformeContable();
            informe.setCodigocuenta(next.getCuentacontable().getCuentacontablecodigo());
            informe.setNombrecuenta(next.getCuentacontable().getCuentacontablenombre());
            informe.setValorhaber(BigDecimal.ZERO);
            informe.setValordebe(BigDecimal.ZERO);
            informe.setValordebe(next.getDiariodetalledebe());
            informe.setValorhaber(next.getDiariodetallehaber());
            listInforme.add(informe);
        }
        return listInforme;
    }

    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaDiario);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        datosDiario((Diario) tabla.getSelectionModel().getSelectedItem());
        mensajeInformativo.setText("");
    }

    public ObservableList<Diario> getListaDiarios() {
        return listaDiario;
    }

    private void grabaDatos(Diario diario, Integer registroid, String modo) {
        getEscena().setCursor(Cursor.WAIT);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                grabaDetalle(diario);
                GenerarAuditoria.grabaRegistro("Diario", registroid, modo, getApp().getLoggedUser().getUsuarioidentidad());
                return null;
            }

            @Override
            protected void succeeded() {
                getEscena().setCursor(Cursor.DEFAULT);
                XMLGregorianCalendar filtro = diario.getDiariofecha();
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

    private void grabaDetalle(Diario diario) {
        // Borra el detalle
        borraDetalle(diario);
        for (Iterator<DiarioDetalleT> iterator = diariodetalleT.iterator(); iterator.hasNext();) {
            DiarioDetalleT detalle = (DiarioDetalleT) iterator.next();
            String codigo = detalle.getCodigocuenta().getValue();
            BigDecimal valordebe = BigDecimal.valueOf(detalle.getValordebe().getValue());
            BigDecimal valorhaber = BigDecimal.valueOf(detalle.getValorhaber().getValue());
            if (!" ".equals(codigo)) {
                Diariodetalle diariodetalle = new Diariodetalle();
                diariodetalle.setDiario(diario);
                Cuentacontable cuentacontable = Financiero.findByCuentacontablecodigo(codigo, empresaid);
                diariodetalle.setCuentacontable(cuentacontable);
                diariodetalle.setDiariodetalledebe(valordebe);
                diariodetalle.setDiariodetallehaber(valorhaber);
                diariodetalle.setDiariodetalleestado("PASIVO");
                DiariodetallePK diariodetallepk = new DiariodetallePK();
                diariodetallepk.setCuentacontableid(cuentacontable.getCuentacontableid());
                diariodetallepk.setDiarioid(diario.getDiarioid());
                diariodetalle.setDiariodetallePK(diariodetallepk);
                Financiero.createDiariodetalle(diariodetalle);
            }
        }
    }

    private void borraDetalle(Diario diario) {
        List<Diariodetalle> diariodetalle = Financiero.findByDiarioid(diario.getDiarioid());
        for (Iterator<Diariodetalle> iterator = diariodetalle.iterator(); iterator.hasNext();) {
            Diariodetalle next = iterator.next();
            Financiero.removeDiariodetalle(next);
        }
    }
}
