
package acuario.controller;

import acuario.services.Persona;
import acuario.services.Usuario;
import acuario.AcuarioApp;
import acuario.webservices.Seguridad;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


/**
 * Profile Controller.
 */
public class PersonaController extends AnchorPane {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML
    private TextField user;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private TextArea address;
    @FXML
    private CheckBox subscribed;
    @FXML
    private Hyperlink logout;
    @FXML 
    private Button update;
    
    @FXML 
    private Label success;
    
    private AcuarioApp application;
    
    public void setApp(AcuarioApp application){
        this.application = application;
        Usuario loggedUser = application.getLoggedUser();
        Persona persona = loggedUser.getPersonaid();
        user.setText(persona.getPersonaapellidos()+" "+persona.getPersonanombres());
        email.setText(persona.getPersonacorreo());
        phone.setText(persona.getPersonamovil());
        if (persona.getPersonadireccion() != null) {
            address.setText(persona.getPersonadireccion());
        }
        subscribed.setSelected("ACTIVO".equals(persona.getPersonaestado()));
        success.setOpacity(0);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    }
    
    public void processLogout(ActionEvent event) {
        if (application == null){
            return;
        }        
        application.userLogout();
    }
    
    public void processUpdate(ActionEvent event) {
        if (application == null){
            animateMessage();
            return;
        }
        Usuario loggedUser = application.getLoggedUser();
        Persona persona = loggedUser.getPersonaid();
        persona.setPersonacorreo(email.getText());
        persona.setPersonamovil(phone.getText());
        //loggedUser.setSubscribed(subscribed.isSelected());
        persona.setPersonadireccion(address.getText());
        Seguridad.actualizaPersona(persona);
        animateMessage();
    }

    private void animateMessage() {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), success);
        ft.setFromValue(0.0);
        ft.setToValue(1);
        ft.play();
    }
}
