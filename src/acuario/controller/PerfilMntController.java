/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Perfil;
import acuario.services.Programa;
import acuario.services.Programaperfil;
import acuario.services.ProgramaperfilPK;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.clases.GenerarAuditoria;
import acuario.clases.PerfilDetalleT;
import acuario.webservices.Seguridad;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class PerfilMntController extends ControllerAM {

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
    TableColumn perfilNombreTC;
    @FXML
    TableColumn perfilIdentidadTC;
    @FXML
    TextField perfilId;
    @FXML
    TextField perfilNombre;
    @FXML
    TextField perfilDescripcion;
    @FXML
    TextField perfilBuscar;
    @FXML
    Label mensajeInformativo;

    private ObservableList<Perfil> listaPerfil = FXCollections.observableArrayList();
    private List<PerfilDetalleT> perfildetalleT;

    /**
     * Initializes the controller class.
     */
    @Override
    public void Inicializar() {
        inicializaTabla();
        datosTabla("A", "nombre");
        Perfil perfil = (Perfil) tabla.getSelectionModel().getSelectedItem();
        if (perfil != null) {
            datosPerfil(perfil);
        }
        // Listen for selection changes
        tabla.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Perfil>() {

            @Override
            public void changed(ObservableValue<? extends Perfil> observable,
                    Perfil oldValue, Perfil newValue) {
                mensajeInformativo.setText("");
                datosPerfil(newValue);
            }
        });
    }

    public void datosTabla(String filtro, String tipo) {
        if ("nombre".equals(tipo)) {
            listaPerfil = FXCollections.observableArrayList(Seguridad.findPerfilByNombre(filtro));
        } else {
            listaPerfil = FXCollections.observableArrayList(Seguridad.findPerfilById(0));
        }
        actualizarTabla();
    }

    public void inicializaTabla() {
        perfilNombreTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Perfil, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Perfil, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getPerfilnombre());
                } else {
                    return new SimpleStringProperty("<>");
                }
            }
        });
        perfilIdentidadTC.setCellValueFactory(new PropertyValueFactory<>("perfilid"));
    }

    private void datosPerfil(Perfil perfil) {
        if (perfil != null) {
            perfilId.setText(perfil.getPerfilid().toString());
            perfilNombre.setText(perfil.getPerfilnombre());
            perfilDescripcion.setText(perfil.getPerfildescripcion());
        } else {
            perfilId.setText("");
            perfilNombre.setText("");
            perfilDescripcion.setText("");
        }

    }

    public void nuevoPerfil() {
        Perfil selectedPerfil = new Perfil();
        boolean okClicked = perfilEditar(selectedPerfil, "Crear");
        if (okClicked) {
            Integer registroid = Seguridad.createIdPerfil(selectedPerfil);
            selectedPerfil.setPerfilid(registroid);
            listaPerfil.add(selectedPerfil);
            grabaDatos(selectedPerfil, registroid, "Crear");
            creaMenus(selectedPerfil);
        }
    }

    public void modificarPerfil() {
        Perfil selectedPerfil = (Perfil) tabla.getSelectionModel().getSelectedItem();
        if (selectedPerfil != null) {
            boolean okClicked = perfilEditar(selectedPerfil, "Editar");
            if (okClicked) {
                Integer registroid = selectedPerfil.getPerfilid();
                Seguridad.editPerfil(selectedPerfil);
                grabaDatos(selectedPerfil, registroid, "Editar");
            }
        } else {
            // Nothing selected
            Dialogs.create().title(getApp().getLoggedUser().getEmpresaid().getEmpresanombre()).masthead(null)
                    .message("Seleccione un registro de la tabla.").showInformation();
        }
    }

    public void eliminarPerfil() {
        Perfil selectedPerfil = (Perfil) tabla.getSelectionModel().getSelectedItem();
        int selectedIndex = tabla.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && "PASIVO".equals(selectedPerfil.getPerfilestado())) {
            try {
                if (confirmaEliminacion()) {
                    borraDetalle(selectedPerfil);
                    Seguridad.removePerfil(selectedPerfil);
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

    public void buscarPerfil() {
        String regex = "[0-9]+";
        String tipo = "nombre";
        if (perfilBuscar.getText().matches(regex)) {
            tipo = "id";
        }
        datosTabla(perfilBuscar.getText(), tipo);
    }

    public boolean perfilEditar(Perfil perfil, String modo) {
        try {            
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(AcuarioApp.class.getResource("fxml/PerfilEdc.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(modo+ " Perfil");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);            
            dialogStage.setScene(scene);
            
            // Set the person into the controller
            PerfilEdcController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setApp(getApp());  
            controller.Inicializar();   
            controller.setPerfil(perfil, modo);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                perfildetalleT = controller.getOl();
            }
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
        tabla.setItems(listaPerfil);
        // Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
        tabla.getSelectionModel().selectFirst();
        mensajeInformativo.setText("");
    }

    public ObservableList<Perfil> getListaPerfils() {
        return listaPerfil;
    }

    private void grabaDatos(Perfil perfil, Integer registroid, String modo) {
        getEscena().setCursor(Cursor.WAIT);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                grabaDetalle(perfil);
                GenerarAuditoria.grabaRegistro("Perfil", registroid, modo, getApp().getLoggedUser().getUsuarioidentidad());
                return null;
            }

            @Override
            protected void succeeded() {
                getEscena().setCursor(Cursor.DEFAULT);
                String filtro = perfil.getPerfilnombre();
                datosTabla(filtro, "nombre");
                datosPerfil(perfil);                
                if ("Crear".equals(modo)) {
                    mensajeInformativo.setText("Datos creados satisfactoriamente...");
                } else {
                    mensajeInformativo.setText("Datos modificados satisfactoriamente...");
                }                
            }
        };
        new Thread(task).start();
    }

    private void grabaDetalle(Perfil perfil) {
        // Borra el detalle
        borraDetalle(perfil);
        for (Iterator<PerfilDetalleT> iterator = perfildetalleT.iterator(); iterator.hasNext();) {
            PerfilDetalleT detalle = (PerfilDetalleT) iterator.next();
            Integer codigo = detalle.getIdprograma().getValue();
            if (codigo != 0) {
                Programa programa = Seguridad.findPrograma(codigo);
                if ("PROGRAMA".equals(programa.getProgramatipo())) {
                    grabaProgramaperfil(perfil, programa);
                    //System.err.println("Graba:" + codigo + " " + new Date(System.currentTimeMillis()));
                }
            }
        }

    }

    private void borraDetalle(Perfil perfil) {
        List<Programaperfil> programaperfil = Seguridad.listaProgramas(perfil.getPerfilid());
        for (Iterator<Programaperfil> iterator = programaperfil.iterator(); iterator.hasNext();) {
            Programaperfil next = iterator.next();
            Programa programa = Seguridad.findPrograma(next.getProgramaperfilPK().getProgramaid());
            if ("PROGRAMA".equals(programa.getProgramatipo())) {
                Seguridad.removeProgramaperfil(next);
                //System.err.println("Borra:" + next.getPrograma().getProgramaid() + " " + new Date(System.currentTimeMillis()));
            }
        }
    }

    private void creaMenus(Perfil perfil) {
        List<Programa> programa = Seguridad.findProgramaByNombre("");
        for (Iterator<Programa> iterator = programa.iterator(); iterator.hasNext();) {
            Programa next = iterator.next();
            if ("MENU".equals(next.getProgramatipo()) || "SUBMENU".equals(next.getProgramatipo())) {
                grabaProgramaperfil(perfil, next);
            }
        }
    }

    private void grabaProgramaperfil(Perfil perfil, Programa programa) {
        Programaperfil programaperfil = new Programaperfil();
        programaperfil.setPerfil(perfil);
        programaperfil.setPrograma(programa);
        programaperfil.setProgramaperfilestado("PASIVO");
        ProgramaperfilPK programaperfilpk = new ProgramaperfilPK();
        programaperfilpk.setPerfilid(perfil.getPerfilid());
        programaperfilpk.setProgramaid(programa.getProgramaid());
        programaperfil.setProgramaperfilPK(programaperfilpk);
        Seguridad.createProgramaperfil(programaperfil);
    }
}
