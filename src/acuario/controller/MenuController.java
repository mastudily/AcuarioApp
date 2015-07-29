/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.controller;

import acuario.services.Programa;
import acuario.AcuarioApp;
import acuario.clases.ControllerAM;
import acuario.webservices.Seguridad;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Marcelo Astudillo
 */
public class MenuController extends AnchorPane {

    @FXML
    BorderPane border;
    @FXML
    MenuBar menuBar;
    private Menu menu;
    private MenuItem menuItem;
    private AcuarioApp application;

    public void setApp(AcuarioApp application) {
        this.application = application;        
        menuBar.getMenus().clear();
        generaMenu();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    void initialize() {
    }

    private void generaMenu() {
        List listaMenus = new ArrayList();
        List listaSubmenus = new ArrayList();
        List listaProgramas = new ArrayList();
        listaMenus = Seguridad.listaProgramasTipo(application.getLoggedUser().getPerfilid().getPerfilid(), "MENU", 0);
        for (Iterator iterator = listaMenus.iterator(); iterator.hasNext();) {
            Programa programamenu = (Programa) iterator.next();
            System.out.println("Menu MC:" + programamenu.getProgramanombre());
            menu = new Menu();
            menu.setText(programamenu.getProgramanombre());
            listaSubmenus = Seguridad.listaProgramasTipo(application.getLoggedUser().getPerfilid().getPerfilid(), "SUBMENU", programamenu.getProgramaid());
            boolean submenuV = false;
            for (Iterator itsubmenu = listaSubmenus.iterator(); itsubmenu.hasNext();) {
                Programa submenu = (Programa) itsubmenu.next();
                System.out.println("Submenu MC:" + submenu.getProgramanombre());
                menuItem = new MenuItem(submenu.getProgramanombre());
                menu.getItems().add(menuItem);
                listaProgramas = Seguridad.listaProgramasTipo(application.getLoggedUser().getPerfilid().getPerfilid(), "PROGRAMA", submenu.getProgramaid());
                for (Iterator itprograma = listaProgramas.iterator(); itprograma.hasNext();) {
                    Programa programa = (Programa) itprograma.next();
                    System.out.println("Programa submenu MC:" + programa.getProgramanombre());
                    menuItem = new MenuItem(programa.getProgramanombre());
                    menu.getItems().add(menuItem);
                    submenuV = true;
                }
            }
            if (!submenuV) {
                listaProgramas = Seguridad.listaProgramasTipo(application.getLoggedUser().getPerfilid().getPerfilid(), "PROGRAMA", programamenu.getProgramaid());
                for (Iterator itprograma = listaProgramas.iterator(); itprograma.hasNext();) {
                    Programa programa = (Programa) itprograma.next();
                    System.out.println("Programa no submenu MC:" + programa.getProgramanombre());
                    menuItem = new MenuItem(programa.getProgramanombre());
                    menuItem.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent t) {
                            if (!"Salir".equals(programa.getProgramaabreviatura())) {
                                try {
                                    cargarFormulario("fxml/" + programa.getProgramaabreviatura() + ".fxml");
                                } catch (Exception ex) {
                                    System.err.println(MenuController.class.getName() + " " + ex);
                                }
                            } else {
                                application.gotoLogin();
                            }
                        }
                    });
                    menu.getItems().add(menuItem);
                }
            }
            menuBar.getMenus().add(menu);
        }

    }

    public void cargarFormulario(String fxml) {
        try {
            border.getScene().setCursor(Cursor.WAIT);
            FXMLLoader loader = new FXMLLoader();
            InputStream in = AcuarioApp.class.getResourceAsStream(fxml);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(AcuarioApp.class.getResource(fxml));
            AnchorPane page;
            try {
                page = (AnchorPane) loader.load(in);
                final ControllerAM controller = (ControllerAM) loader.getController();
                controller.setApp(application);
                controller.setEscena(border.getScene());
                controller.Inicializar();
            } finally {
                in.close();
            }
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    //control.inicializaDatos();
                    return null;
                }

                @Override
                protected void succeeded() {
                    border.getScene().setCursor(Cursor.DEFAULT);
                    border.setCenter(page);
                }
            };
            new Thread(task).start();
        } catch (IOException ex) {
            System.err.println(AcuarioApp.class.getName() + " " + ex);
        }
    }

   

}
