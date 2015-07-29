/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Marcelo Astudillo
 */
public class PerfilDetalleT {

    private SimpleStringProperty nombreprograma;
    private SimpleIntegerProperty idprograma;

    public PerfilDetalleT(String nombre, Integer id) {
        this.nombreprograma = new SimpleStringProperty(nombre);
        this.idprograma = new SimpleIntegerProperty(id);

    }
    
    public SimpleStringProperty getNombreprograma() {
        return nombreprograma;
    }

    public void setNombreprograma(String nombre) {
        this.nombreprograma.set(nombre);
    }

    public SimpleStringProperty nombreprogramaProperty() {
        return nombreprograma;
    }

    public SimpleIntegerProperty getIdprograma() {
        return idprograma;
    }

    public void setIdprograma(Integer idprograma) {
        this.idprograma.set(idprograma);
    }

    public SimpleIntegerProperty idprogramaProperty() {
        return idprograma;
    }
        
}
