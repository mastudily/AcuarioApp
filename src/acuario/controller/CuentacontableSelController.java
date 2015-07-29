/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Cuentacontable;
import acuario.clases.ControllerAM;
import acuario.webservices.Financiero;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class CuentacontableSelController extends ControllerAM {

    @FXML
    Button btnSeleccionar;
    @FXML
    Button btnNuevo;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn cuentacontableNombreTC;
    @FXML
    TableColumn cuentacontableCodigoTC;
    @FXML
    TableColumn cuentacontableSignoTC;    
    @FXML
    TextField cuentacontableId;
    @FXML
    TextField cuentacontableNombre;
    @FXML
    TextField cuentacontableDescripcion;
    @FXML
    TextField cuentacontableCodigo;
    @FXML
    TextField cuentacontablePadre;  
    @FXML
    CheckBox cuentacontableMayor;  
    @FXML
    TextField cuentacontableBuscar;    

    private Stage dialogStage;
    private Cuentacontable cuentacontableSel;
    private boolean okClicked = false;

    private ObservableList<Cuentacontable> listaCuentacontable = FXCollections.observableArrayList();
   private Integer empresaid;
    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        empresaid = getApp().getLoggedUser().getEmpresaid().getEmpresaid();
        datosTabla("A", "nombre");
        Cuentacontable cuentacontable = (Cuentacontable) tabla.getSelectionModel().getSelectedItem();
        if (cuentacontable != null) {
            datosCuentacontable(cuentacontable);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cuentacontable>() {

            @Override
            public void changed(ObservableValue<? extends Cuentacontable> observable,
                    Cuentacontable oldValue, Cuentacontable newValue) {
                datosCuentacontable(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaCuentacontable = FXCollections.observableArrayList(Financiero.findCuentacontableByNombre(filtro, empresaid));
        } else {
            listaCuentacontable = FXCollections.observableArrayList(Financiero.findCuentacontableByCodigo(filtro, empresaid));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        cuentacontableNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Cuentacontable, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Cuentacontable, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getCuentacontablenombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        cuentacontableCodigoTC.setCellValueFactory(new PropertyValueFactory<>("cuentacontablecodigo"));
        cuentacontableSignoTC.setCellValueFactory(new PropertyValueFactory<>("cuentacontablesigno"));
    }

    private void datosCuentacontable(Cuentacontable cuentacontable) {
        if (cuentacontable != null) {
            cuentacontableNombre.setText(cuentacontable.getCuentacontablenombre());           
            cuentacontableDescripcion.setText(cuentacontable.getCuentacontabledescripcion());
            cuentacontableCodigo.setText(cuentacontable.getCuentacontablecodigo());
            Cuentacontable padre = Financiero.findCuentacontable(cuentacontable.getCuentacontablepadreid());
            cuentacontablePadre.setText("");
            if (padre!=null) {
                cuentacontablePadre.setText(padre.getCuentacontablenombre());
            }
            cuentacontableMayor.setSelected(cuentacontable.isCuentacontablemayor());
        } else {
            cuentacontableNombre.setText("");
            cuentacontableDescripcion.setText("");
            cuentacontableCodigo.setText("");
            cuentacontablePadre.setText("");
        }

    }

    public void buscarCuentacontable() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (cuentacontableBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(cuentacontableBuscar.getText(), tipo);
    }


    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaCuentacontable);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
    }

    public ObservableList<Cuentacontable> getListaCuentacontables() {
        return listaCuentacontable;
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
    private void seleccionCuentacontable() {
        cuentacontableSel = (Cuentacontable) tabla.getSelectionModel().getSelectedItem();
        okClicked = true;
        dialogStage.close();
    }

    public Cuentacontable getCuentacontableSel() {
        return cuentacontableSel;
    }

    public void setCuentacontableSel(Cuentacontable cuentacontableSel) {
        this.cuentacontableSel = cuentacontableSel;
    }

    


}
