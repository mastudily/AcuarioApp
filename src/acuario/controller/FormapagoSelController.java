/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Formapago;
import acuario.clases.ControllerAM;
import acuario.webservices.Mantenimiento;
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
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class FormapagoSelController extends ControllerAM {

    @FXML
    Button btnSeleccion;
    @FXML
    Button btnBuscar;
    @FXML
    TableView tabla;
    @FXML
    TableColumn formapagoNombreTC;
    @FXML
    TableColumn formapagoIdTC;
    @FXML
    TextField formapagoId;
    @FXML
    TextField formapagoNombre;
    @FXML
    TextField formapagoDescripcion;
    @FXML
    TextField formapagoCtaCbe;
    @FXML
    TextField formapagoConCtaCbe;
    @FXML
    TextField formapagoBuscar;
    @FXML
    Label mensajeInformativo;

    
    private Stage dialogStage;
    private Formapago formapagoSel;
    private boolean okClicked = false;
    
    private ObservableList<Formapago> listaFormapago = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        datosTabla("A", "nombre");
        Formapago formapago = (Formapago) tabla.getSelectionModel().getSelectedItem();
        if (formapago != null) {
            datosFormapago(formapago);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Formapago>() {

            @Override
            public void changed(ObservableValue<? extends Formapago> observable,
                    Formapago oldValue, Formapago newValue) {
                mensajeInformativo.setText("");
                datosFormapago(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaFormapago = FXCollections.observableArrayList(Mantenimiento.findFormapagoByNombre(filtro));
        } else {
            listaFormapago = FXCollections.observableArrayList(Mantenimiento.findFormapagoById(0));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        formapagoIdTC.setCellValueFactory(new PropertyValueFactory<>("formapagoid"));
        formapagoNombreTC.setCellValueFactory(new PropertyValueFactory<>("formapagonombre"));
    }

    private void datosFormapago(Formapago formapago) {
        if (formapago != null) {
            formapagoId.setText(formapago.getFormapagoid().toString());
            formapagoNombre.setText(formapago.getFormapagonombre());
            formapagoDescripcion.setText(formapago.getFormapagodescripcion());
            formapagoCtaCbe.setText(formapago.getFormapagoctacble().getCuentacontabledescripcion());
            formapagoConCtaCbe.setText(formapago.getFormapagoconctacble().getCuentacontabledescripcion());
        } else {
            formapagoId.setText("");
            formapagoNombre.setText("");
            formapagoDescripcion.setText("");
            formapagoCtaCbe.setText("");
            formapagoConCtaCbe.setText("");            
        }

    }

    public void buscarFormapago() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (formapagoBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(formapagoBuscar.getText(), tipo);
    }

    private void actualizarTabla() {
        tabla.setItems(null);
        tabla.layout();
        tabla.setItems(listaFormapago);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    @FXML
    private void seleccionFormapago() {
        formapagoSel = (Formapago) tabla.getSelectionModel().getSelectedItem();
        okClicked = true;
        dialogStage.close();
    }
    
    public ObservableList<Formapago> getListaFormapagos() {
        return listaFormapago;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public Formapago getFormapagoSel() {
        return formapagoSel;
    }

    public void setFormapagoSel(Formapago formapagoSel) {
        this.formapagoSel = formapagoSel;
    }
    
    
}
