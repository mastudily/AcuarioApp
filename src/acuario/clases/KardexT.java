/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Marcelo Astudillo
 */
public class KardexT {

    private SimpleIntegerProperty codigoinsumo;
    private SimpleStringProperty nombreinsumo, estado;
    private SimpleDoubleProperty cantidad, precio;

    public KardexT(Integer codigo, String nombre, double cantidad, double precio, String estado) {
        this.codigoinsumo = new SimpleIntegerProperty(codigo);
        this.nombreinsumo = new SimpleStringProperty(nombre);
        this.cantidad = new SimpleDoubleProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
        this.estado = new SimpleStringProperty(estado);
    }
    
    public SimpleIntegerProperty getCodigoinsumo() {
        return codigoinsumo;
    }

    public void setCodigoinsumo(Integer codigo) {
        this.codigoinsumo.set(codigo);
    }

    public SimpleIntegerProperty codigoinsumoProperty() {
        return codigoinsumo;
    }

    public SimpleStringProperty getNombreinsumo() {
        return nombreinsumo;
    }

    public void setNombreinsumo(String nombre) {
        this.nombreinsumo.set(nombre);
    }

    public SimpleStringProperty nombreinsumoProperty() {
        return nombreinsumo;
    }

    public SimpleDoubleProperty getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad.set(cantidad);
    }

    public SimpleDoubleProperty cantidadProperty() {
        return cantidad;
    }
    
    public SimpleDoubleProperty getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
    }

    public SimpleDoubleProperty precioProperty() {
        return precio;
    }    
    
    public SimpleStringProperty getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public SimpleStringProperty estadoProperty() {
        return estado;
    }
    
}
