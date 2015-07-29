/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Perfil;
import acuario.services.Programa;
import acuario.services.Programaperfil;
import asmor.textfield.TextFieldL;
import acuario.clases.ControllerAM;
import acuario.clases.PerfilDetalleT;
import acuario.clases.SelectItem;
import acuario.clases.Util;
import acuario.webservices.Seguridad;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class PerfilEdcController extends ControllerAM {

    @FXML
    Button btnAceptar;
    @FXML
    Button btnCancelar;
    @FXML
    Button btnPadre;
    @FXML
    Button btnImagen;
    @FXML
    TextFieldL perfilNombre;
    @FXML
    TextFieldL perfilDescripcion;
    @FXML
    TableColumn programaIdentidadTC;
    @FXML
    TableColumn programaNombreTC;
    @FXML
    TableView detalle;

    private Stage dialogStage;
    private Perfil perfil;
    private boolean okClicked = false;
    private List<PerfilDetalleT> perfildetalleT;
    private ObservableList<PerfilDetalleT> ol;
    private ObservableList<Programa> listaPrograma;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        perfilNombre.setMaxLength(30);
        perfilNombre.setPrefWidth(300);
        perfilDescripcion.setMaxLength(50);
        perfilDescripcion.setPrefWidth(400);
        List listaAux = Seguridad.findProgramaByTipo("PROGRAMA");
        listaPrograma = FXCollections.observableArrayList(Util.getSelectPrograma(listaAux, true));
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPerfil(Perfil perfil, String modo) {
        this.perfil = perfil;
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
                    perfilNombre.setTexto(perfil.getPerfilnombre());
                    perfilDescripcion.setTexto(perfil.getPerfildescripcion());
                } else {
                    perfilNombre.setTexto("");
                    perfilDescripcion.setTexto("");
                }
                perfilDetalle();
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
            perfil.setPerfilnombre(perfilNombre.getTexto());
            perfil.setPerfildescripcion(perfilDescripcion.getTexto());
            perfil.setPerfilestado("ACTIVO");
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

        if (perfilNombre.getTexto() == null || perfilNombre.getTexto().length() == 0) {
            errorMessage += "Debe ingresar nombre del perfil!\n";
        }
        if (perfilDescripcion.getTexto() == null || perfilDescripcion.getTexto().length() == 0) {
            errorMessage += "Debe ingresar descripcion del perfil!\n";
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
        perfildetalleT = new ArrayList();
        for (Iterator<Programaperfil> iterator = Seguridad.listaProgramas(perfil.getPerfilid()).iterator(); iterator.hasNext();) {
            Programaperfil detalled = (Programaperfil) iterator.next();
            Programa programa = Seguridad.findPrograma(detalled.getProgramaperfilPK().getProgramaid());
            Integer codigo = programa.getProgramaid();
            String nombre = programa.getProgramanombre();
            String tipo = programa.getProgramatipo();
            if ("PROGRAMA".equals(tipo)) {
                perfildetalleT.add(new PerfilDetalleT(nombre, codigo));
            }
        }
    }

    public void perfilDetalle() {
        if (perfildetalleT.isEmpty()) {
            detalle.setPlaceholder(new Label("No existen registros..."));
        }
        ol = FXCollections.observableArrayList(perfildetalleT);

        programaIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("idprograma"));

        programaNombreTC.setCellValueFactory(new PropertyValueFactory<PerfilDetalleT, String>("nombreprograma"));
        programaNombreTC.setCellFactory(ComboBoxTableCell.forTableColumn(listaPrograma));
        programaNombreTC.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<PerfilDetalleT, SelectItem>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<PerfilDetalleT, SelectItem> t) {
                        String ids = (String) t.getNewValue().getId();
                        Integer id = Integer.parseInt(ids);
                        ((PerfilDetalleT) t.getTableView().getItems().get(t.getTablePosition().getRow())).setIdprograma(id);
                        ((PerfilDetalleT) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNombreprograma(t.getNewValue().getDescription());
                    }
                });

        MenuItem mnuDel = new MenuItem("Eliminar");
        mnuDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                PerfilDetalleT item = (PerfilDetalleT) detalle.getSelectionModel().getSelectedItem();
                if (item != null) {
                    ol.remove(item);
                    detalle.getSelectionModel().selectFirst();
                }
            }
        });
        MenuItem mnuNew = new MenuItem("Nuevo");
        mnuNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ol.add(new PerfilDetalleT(" ", 0));
                detalle.getSelectionModel().selectLast();
            }
        });
        detalle.setContextMenu(new ContextMenu(mnuDel, mnuNew));
        detalle.setItems(ol);
    }

    public ObservableList<PerfilDetalleT> getOl() {
        return ol;
    }

    public void setOl(ObservableList<PerfilDetalleT> ol) {
        this.ol = ol;
    }

}
