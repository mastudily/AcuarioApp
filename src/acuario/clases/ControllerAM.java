/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import acuario.AcuarioApp;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Marcelo Astudillo
 */
public class ControllerAM extends AnchorPane{
    private AcuarioApp application;
    private Scene escena;
    
    
    public void setApp(AcuarioApp application) {
        this.application = application;
    }

    public AcuarioApp getApp() {
        return application;
    }

    public Scene getEscena() {
        return escena;
    }

    public void setEscena(Scene escena) {
        this.escena = escena;
    }
    
    
    public void Inicializar() {
        
    }
    
}