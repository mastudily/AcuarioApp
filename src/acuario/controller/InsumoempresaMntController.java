/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumoempresa;
import acuario.services.Insumo;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.webservices.Farmacia;
import java.io.File;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class InsumoempresaMntController extends ControllerAM {

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
    TableColumn insumoNombreTC;
    @FXML
    TableColumn insumoCodigoTC;
    @FXML
    TableColumn insumoCantidadTC;
    @FXML
    TextField insumoCodigo;
    @FXML
    TextField insumoNombre;
    @FXML
    TextField insumoDescripcion;
    @FXML
    TextField insumoValor;
    @FXML
    TextField insumoCantidad;
    @FXML
    TextField insumoBuscar;
    @FXML
    Label nombreImagen;
    @FXML
    ImageView imagenImagen;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Insumoempresa> listaInsumoempresa = FXCollections.observableArrayList();
    private File file;
    private String url;
    private Insumo insumo;
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
        Insumoempresa insumoempresa = (Insumoempresa) tabla.getSelectionModel().getSelectedItem();
        if (insumoempresa != null) {
            insumo = insumoempresa.getInsumo();
            datosInsumoempresa(insumoempresa);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Insumoempresa>() {

            @Override
            public void changed(ObservableValue<? extends Insumoempresa> observable,
                    Insumoempresa oldValue, Insumoempresa newValue) {
                mensajeInformativo.setText("");
                datosInsumoempresa(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaInsumoempresa = FXCollections.observableArrayList(Farmacia.findInsumoempresaByNombre(filtro, empresaid));
        } else {
            listaInsumoempresa = FXCollections.observableArrayList(Farmacia.findInsumoempresaByCodigo(filtro, empresaid));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        insumoNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Insumoempresa, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Insumoempresa, String> p) {
                if (p.getValue() != null) {
                    //String nombre = Farmacia.findInsumo(p.getValue().getInsumoempresaPK().getInsumoid()).getInsumonombre();
                    //return new SimpleStringProperty(nombre);
                    return new SimpleStringProperty(p.getValue().getInsumo().getInsumonombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        insumoCodigoTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Insumoempresa, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Insumoempresa, String> p) {
                if (p.getValue() != null) {
                    //String codigo = Farmacia.findInsumo(p.getValue().getInsumoempresaPK().getInsumoid()).getInsumocodigo();
                    //return new SimpleStringProperty(codigo);
                    return new SimpleStringProperty(p.getValue().getInsumo().getInsumocodigo());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        insumoCantidadTC.setCellValueFactory(new PropertyValueFactory<>("insumoempresastockactual"));
    }

    private void datosInsumoempresa(Insumoempresa insumoempresa) {
        if (insumoempresa != null) {
            insumo = Farmacia.findInsumo(insumoempresa.getInsumoempresaPK().getInsumoid());
            insumoCodigo.setText(insumo.getInsumocodigo());
            insumoNombre.setText(insumo.getInsumonombre());
            insumoDescripcion.setText(insumo.getInsumodescripcion());
            insumoValor.setText(insumo.getInsumovalor().toPlainString());
            /*if (insumo.getInsumofoto() != null && insumo.getInsumofoto().length() != 0) {
                imagenImagen.setImage(new Image(url + "Insumo/" + insumo.getInsumofoto()));
            } else {
                imagenImagen.setImage(null);
            }*/
            insumoCantidad.setText(insumoempresa.getInsumoempresastockactual().toPlainString());
        } else {
            insumoCodigo.setText("");
            insumoNombre.setText("");
            insumoDescripcion.setText("");
            insumoValor.setText("");
            insumoCantidad.setText("");
        }

    }

    public void nuevoInsumoempresa() {
        Insumoempresa selectedInsumoempresa = new Insumoempresa();
        boolean okClicked = insumoempresaEditar(selectedInsumoempresa, "Crear");
        if (okClicked) {
            selectedInsumoempresa.setInsumoempresastockactual(selectedInsumoempresa.getInsumoempresastockinicial());
            if (Farmacia.createInsumoempresa(selectedInsumoempresa)) {
                listaInsumoempresa.add(selectedInsumoempresa);
                //tabla.getSelectionModel().selectLast(); 
                String filtro = selectedInsumoempresa.getInsumo().getInsumonombre();
                datosTabla(filtro, "nombre");
                datosInsumoempresa(selectedInsumoempresa);
                Integer registroid = selectedInsumoempresa.getInsumo().getInsumoid();
                GenerarAuditoria.grabaRegistro("Insumoempresa", registroid, "Crear", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos creados satisfactoriamente...");
            } else {
                mensajeInformativo.setText("Ya existe el registro...");
            }
        }
    }

    public void modificarInsumoempresa() {
        Insumoempresa selectedInsumoempresa = (Insumoempresa) tabla.getSelectionModel().getSelectedItem();
        if (selectedInsumoempresa != null) {
            String foto = "";
            boolean okClicked = insumoempresaEditar(selectedInsumoempresa, "Editar");
            if (okClicked) {
                /*if (selectedInsumoempresa.getInsumo().getInsumofoto() != null && selectedInsumoempresa.getInsumo().getInsumofoto().length() != 0) {
                    foto = selectedInsumoempresa.getInsumo().getInsumofoto();
                }*/
                Integer registroid = selectedInsumoempresa.getInsumo().getInsumoid();
                Farmacia.editInsumoempresa(selectedInsumoempresa);
                String filtro = selectedInsumoempresa.getInsumo().getInsumonombre();
                datosTabla(filtro, "nombre");
                datosInsumoempresa(selectedInsumoempresa);
                GenerarAuditoria.grabaRegistro("Insumoempresa", registroid, "Editar", getApp().getLoggedUser().getUsuarioidentidad());
                mensajeInformativo.setText("Datos modificados satisfactoriamente...");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarInsumoempresa() {
        Insumoempresa selectedInsumoempresa = (Insumoempresa) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            try {
                Farmacia.removeInsumoempresa(selectedInsumoempresa);
                tabla.getItems().remove(selectedIndex);
                mensajeInformativo.setText("Datos eliminados satisfactoriamente...");                
            } catch (Exception e) {
                Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                        .message("Registro relacionado no se puede eliminar.").showError();                
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void buscarInsumoempresa() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (insumoBuscar.getText().matches(regex)) {
            tipo = "codigo";
        }
        datosTabla(insumoBuscar.getText(), tipo);
    }

    public boolean insumoempresaEditar(Insumoempresa insumoempresa, String modo) {
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/InsumoempresaEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo + " Insumo");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller
            InsumoempresaEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setInsumoempresa(insumoempresa, modo);
            controller.setApp(getApp());
            controller.Inicializar();

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
        tabla.setItems(listaInsumoempresa);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Insumoempresa> getListaInsumoempresas() {
        return listaInsumoempresa;
    }

}
