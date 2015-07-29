/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario;

import acuario.services.Constante;
import acuario.controller.PersonaController;
import acuario.services.Usuario;
import acuario.controller.MenuController;
import acuario.webservices.Mantenimiento;
import acuario.webservices.Seguridad;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Marcelo Astudillo
 */
public class AcuarioApp extends Application {

    private Stage stage;
    private Usuario loggedUser;
    private String url;
    private final double MINIMUM_WINDOW_WIDTH = 390.0;
    private final double MINIMUM_WINDOW_HEIGHT = 500.0;
    private Boolean conexion;

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setTitle("Credenciales de Ingreso");
            parametrosSistema();
            gotoLogin();
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(AcuarioApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);

    }

    public Usuario getLoggedUser() {
        return loggedUser;
    }

    public Boolean getConexion() {
        return conexion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void parametrosSistema() {
        List<Constante> configura = Mantenimiento.findConstanteByCodigo("CONFIGURA");
        for (Iterator<Constante> iterator = configura.iterator(); iterator.hasNext();) {
            Constante next = iterator.next();
            if ("URL".equals(next.getConstantetipo())) {
                setUrl(next.getConstantedescripcion());
                return;
            }
        }

    }

    public boolean userLogging(String userId, String password) {
        try {
            conexion = true;
            loggedUser = Seguridad.validarUsuario(userId, password);
            if (loggedUser != null) {
                System.out.println("OK");
                //asignaMenu();
                gotoMenu();
                return true;
            } else {
                System.out.println("NOT OK");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error:" + e);
            conexion = false;
            return false;
        }
    }

    public void userLogout() {
        loggedUser = null;
        gotoLogin();
    }

    private void gotoProfile() {
        try {
            PersonaController profile = (PersonaController) replaceSceneContent("fxml/Persona.fxml");
            profile.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(AcuarioApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gotoMenu() {
        try {
            MenuController menu = (MenuController) replaceSceneContent("fxml/Menu.fxml");
            menu.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(AcuarioApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gotoLogin() {
        try {
            acuario.controller.LoginController login = (acuario.controller.LoginController) replaceSceneContent("fxml/Login.fxml");
            login.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(AcuarioApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Node replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = AcuarioApp.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(AcuarioApp.class.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }

        // Store the stage width and height in case the user has resized the window
        double stageWidth = stage.getWidth();
        if (!Double.isNaN(stageWidth)) {
            stageWidth -= (stage.getWidth() - stage.getScene().getWidth());
        }

        double stageHeight = stage.getHeight();
        if (!Double.isNaN(stageHeight)) {
            stageHeight -= (stage.getHeight() - stage.getScene().getHeight());
        }

        Scene scene = new Scene(page);
        if (!Double.isNaN(stageWidth)) {
            page.setPrefWidth(stageWidth);
        }
        if (!Double.isNaN(stageHeight)) {
            page.setPrefHeight(stageHeight);
        }
        stage.setScene(scene);
        if (fxml.matches("fxml/Menu.fxml")) {
            stage.setTitle(loggedUser.getEmpresaid().getEmpresanombre());
            stage.setMinWidth(800);
            stage.setMinHeight(600);
        } else {
            stage.setTitle("Credenciales de Ingreso");
            stage.setWidth(500);
            stage.setHeight(600);                        
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);            
            
            //stage.sizeToScene();
        }
        
        return (Node) loader.getController();
    }
}
