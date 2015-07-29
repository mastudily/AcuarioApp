/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Insumo;
import acuario.clases.ControllerAM;
import acuario.webservices.Farmacia;
import java.io.ByteArrayInputStream;
import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class InsumoSelController extends ControllerAM {

    @FXML
    Button btnSeleccionar;
    @FXML
    Button btnNuevo;
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
    TextField insumoBuscar;
    @FXML
    Label nombreImagen;
    @FXML
    ImageView imagenImagen;
    @FXML
    Label mensajeInformativo;

    private Stage dialogStage;
    private Insumo insumoSel;
    private boolean okClicked = false;

    private ObservableList<Insumo> listaInsumos = FXCollections.observableArrayList();
    private File file;
    private String url;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        url = getApp().getUrl();
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
            nombreImagen.setText("");
        }

    }
    
    public void buscarInsumo() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (insumoBuscar.getText().matches(regex)) {
            tipo = "codigo";
        }
        datosTabla(insumoBuscar.getText(), tipo);
    }

    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaInsumos);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Insumo> getListaInsumos() {
        return listaInsumos;
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void seleccionInsumo() {
        insumoSel = (Insumo) tabla.getSelectionModel().getSelectedItem();
        okClicked = true;
        dialogStage.close();
    }

    public Insumo getInsumoSel() {
        return insumoSel;
    }

    public void setInsumoSel(Insumo insumoSel) {
        this.insumoSel = insumoSel;
    }

    


}
