/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Marcelo Astudillo
 */
public class DiarioDetalleT {

    private SimpleStringProperty codigocuenta, nombrecuenta;
    private SimpleDoubleProperty valordebe, valorhaber;

    public DiarioDetalleT(String codigo, String nombre, double debe, double haber) {
        this.codigocuenta = new SimpleStringProperty(codigo);
        this.nombrecuenta = new SimpleStringProperty(nombre);
        this.valordebe = new SimpleDoubleProperty(debe);
        this.valorhaber = new SimpleDoubleProperty(haber);        
    }
    
    public SimpleStringProperty getCodigocuenta() {
        return codigocuenta;
    }

    public void setCodigocuenta(String codigo) {
        this.codigocuenta.set(codigo);
    }

    public SimpleStringProperty codigocuentaProperty() {
        return codigocuenta;
    }

    public SimpleStringProperty getNombrecuenta() {
        return nombrecuenta;
    }

    public void setNombrecuenta(String nombre) {
        this.nombrecuenta.set(nombre);
    }

    public SimpleStringProperty nombrecuentaProperty() {
        return nombrecuenta;
    }

    public SimpleDoubleProperty getValordebe() {
        return valordebe;
    }

    public void setValordebe(double valor) {
        this.valordebe.set(valor);
    }

    public SimpleDoubleProperty valordebeProperty() {
        return valordebe;
    }

    public SimpleDoubleProperty getValorhaber() {
        return valorhaber;
    }

    public void setValorhaber(double valor) {
        this.valorhaber.set(valor);
    }

    public SimpleDoubleProperty valorhaberProperty() {
        return valorhaber;
    }
    
}
