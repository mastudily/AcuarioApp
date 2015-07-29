/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumo;
import acuario.services.Insumoempresa;
import acuario.services.InsumoempresaPK;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Farmacia;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class InsumoMntController extends ControllerAM {

    @FXML
    Button btnNuevo;
    @FXML
    Button btnModificar;
    @FXML
    Button btnEliminar;
    @FXML
    Button btnMovimientos;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn insumoNombreTC;
    @FXML
    TableColumn insumoCodigoTC;
    @FXML
    TextField insumoNombre;
    @FXML
    TextField insumoDescripcion;
    @FXML
    TextField insumoCodigo;
    @FXML
    TextField insumoValor;
    @FXML
    TextField insumoCantidad;
    @FXML
    ImageView imagenImagen;     
    @FXML
    TextField insumoBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Insumo> listaInsumos = FXCollections.observableArrayList();
    private File file;
    private String url;
    private Integer empresaid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        url = getApp().getUrl();
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        datosTabla("A", "nombre");
        Insumo insumo = (Insumo) tabla.getSelectionModel().getSelectedItem();
        datosInsumo(insumo);
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Insumo>() {

            @Override
            public void changed(ObservableValue<? extends Insumo> observable,
                    Insumo oldValue, Insumo newValue) {
                mensajeInformativo.setText("");
                datosInsumo(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaInsumos = FXCollections.observableArrayList(Farmacia.findInsumoByNombre(filtro));
        } else {
            listaInsumos = FXCollections.observableArrayList(Farmacia.findInsumoByCodigo(filtro));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        insumoNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Insumo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Insumo, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getInsumonombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        insumoCodigoTC.setCellValueFactory(new PropertyValueFactory<>("insumocodigo"));
    }

    private void datosInsumo(Insumo insumo) {
        if (insumo != null) {
            insumoNombre.setText(insumo.getInsumonombre());
            insumoDescripcion.setText(insumo.getInsumodescripcion());
            insumoCodigo.setText(insumo.getInsumocodigo());
            insumoValor.setText(insumo.getInsumovalor().toPlainString());
            InsumoempresaPK insumoempresaPK = new InsumoempresaPK();
            insumoempresaPK.setInsumoid(insumo.getInsumoid());
            insumoempresaPK.setEmpresaid(empresaid);
            Insumoempresa insumoempresa = Farmacia.findInsumoempresa(insumoempresaPK);
            insumoCantidad.setText(insumoempresa.getInsumoempresastockactual().toPlainString());
            if (insumo.getInsumofoto() != null) {
                byte[] foto = insumo.getInsumofoto();
                ByteArrayInputStream bis = new ByteArrayInputStream(foto);
                imagenImagen.setImage(new Image(bis));
            } else {
                imagenImagen.setImage(null);
            }            
        } else {
            insumoNombre.setText("");
            insumoDescripcion.setText("");
            insumoCodigo.setText("");
            insumoValor.setText("");
        }

    }

    public void nuevoInsumo() {
        Insumo selectedInsumo = new Insumo();
        Insumoempresa selectedInsumoempresa = new Insumoempresa();
        boolean okClicked = insumoEditar(selectedInsumo, selectedInsumoempresa, "Crear");
        if (okClicked) {
            Integer registroid = Farmacia.createIdInsumo(selectedInsumo);
            selectedInsumo.setInsumoid(registroid);
            if (selectedInsumo.getInsumocodigo().length() == 0 || "0".equals(selectedInsumo.getInsumocodigo())) {
                selectedInsumo.setInsumocodigo(registroid.toString());
            }
            listaInsumos.add(selectedInsumo);
            InsumoempresaPK insumoempresaPK = new InsumoempresaPK();
            insumoempresaPK.setInsumoid(registroid);
            insumoempresaPK.setEmpresaid(empresaid);
            selectedInsumoempresa.setInsumoempresaPK(insumoempresaPK);
            selectedInsumoempresa.setInsumo(selectedInsumo);
            selectedInsumoempresa.setEmpresa(getApp().getLoggedUser().getEmpresaid());
            Farmacia.createInsumoempresa(selectedInsumoempresa);
            //tabla.getSelectionModel().selectLast(); 
            String filtro = selectedInsumo.getInsumonombre();
            datosTabla(filtro, "nombre");
            datosInsumo(selectedInsumo);
            GenerarAuditoria.grabaRegistro("Insumo", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
            mensajeInformativo.setText("Datos creados satisfactoriamente...");
        }
    }

    public void modificarInsumo() {
        Insumo selectedInsumo = (Insumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            InsumoempresaPK insumoempresaPK = new InsumoempresaPK();
            insumoempresaPK.setInsumoid(selectedInsumo.getInsumoid());
            insumoempresaPK.setEmpresaid(empresaid);
            Insumoempresa selectedInsumoempresa = Farmacia.findInsumoempresa(insumoempresaPK);
            boolean okClicked = insumoEditar(selectedInsumo, selectedInsumoempresa, "Editar");
            if (okClicked) {
                Integer registroid = selectedInsumo.getInsumoid();
                if (selectedInsumo.getInsumocodigo().length() == 0 || "0".equals(selectedInsumo.getInsumocodigo())) {
                    selectedInsumo.setInsumocodigo(registroid.toString());
                }
                Farmacia.editInsumo(selectedInsumo);
                selectedInsumoempresa.setInsumo(selectedInsumo);
                Farmacia.editInsumoempresa(selectedInsumoempresa);
                String filtro = selectedInsumo.getInsumonombre();
                datosTabla(filtro, "nombre");
                datosInsumo(selectedInsumo);
                GenerarAuditoria.grabaRegistro("Insumo", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }

        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarInsumo() {
        Insumo selectedInsumo = (Insumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedInsumo.getInsumoestado())) {
            InsumoempresaPK insumoempresaPK = new InsumoempresaPK();
            insumoempresaPK.setInsumoid(selectedInsumo.getInsumoid());
            insumoempresaPK.setEmpresaid(empresaid);
            Insumoempresa selectedInsumoempresa = Farmacia.findInsumoempresa(insumoempresaPK);
            try {
                if (confirmaEliminacion()) {
                    Farmacia.removeInsumo(selectedInsumo);
                    Farmacia.removeInsumoempresa(selectedInsumoempresa);
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
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("");
        alert.setContentText("Esta seguro de eliminar el registro?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void buscarInsumo() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (insumoBuscar.getText().matches(regex)) {
            tipo = "codigo";
        }
        datosTabla(insumoBuscar.getText(), tipo);
    }

    public boolean insumoEditar(Insumo insumo, Insumoempresa insumoempresa, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/InsumoEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Producto");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            InsumoEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());
            controller.Inicializar();
            controller.setInsumo(insumo, modo);
            controller.datosInsumoempresa(insumoempresa, modo);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
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
        tabla.setItems(listaInsumos);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public boolean insumoMovimiento() {
        Insumo insumo = (Insumo) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            try {
                // Load the fxml file and create a new stage for the popup
                FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/KardexMov.fxml"));
                AnchorPane page = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Movimiento de Productos");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                // Set the person into the controller
                KardexMovController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setApp(getApp());
                controller.setInsumo(insumo);
                controller.Inicializar();

                // Show the dialog and wait until the user closes it
                dialogStage.showAndWait();
                return controller.isOkClicked();

            } catch (IOException e) {
                // Exception gets thrown if the fxml file could not be loaded
                e.printStackTrace();
                return false;
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
            return false;
        }
    }

    public ObservableList<Insumo> getListaInsumos() {
        return listaInsumos;
    }

}
