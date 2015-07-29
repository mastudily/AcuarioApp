
package acuario.controller;

import acuario.AcuarioApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * Login Controller.
 */
public class LoginController extends AnchorPane {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    Button login;
    @FXML
    Label errorMessage;

    private AcuarioApp application;
    
    
    public void setApp(AcuarioApp application){
        this.application = application;
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        errorMessage.setText("");
        errorMessage.setOpacity(0);
    }

    public void processLogin(ActionEvent event) {
        if (application == null){
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            errorMessage.setText("Hello " + userId.getText());
        } else {
            if (!application.userLogging(userId.getText(), password.getText())){
                if (application.getConexion()) {
                    animateMessage();
                    errorMessage.setText("Usuario desconocido " + userId.getText());
                } else {
                    animateMessage();
                    errorMessage.setText("No existe conexion con el servidor ");
                }
            }
        }        
    }
    
    private void animateMessage() {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), errorMessage);
        ft.setFromValue(0.0);
        ft.setToValue(1);
        ft.play();
    }
}
