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
public class DetalleVentaT {

    private SimpleIntegerProperty codigo;
    private SimpleStringProperty nombre;
    private SimpleDoubleProperty cantidad, precio, total;

    public DetalleVentaT(Integer codigo, String nombre, double cantidad, double precio, double total) {
        this.codigo = new SimpleIntegerProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.cantidad = new SimpleDoubleProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
        this.total = new SimpleDoubleProperty(total);
    }
    
    public SimpleIntegerProperty getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo.set(codigo);
    }

    public SimpleIntegerProperty codigoProperty() {
        return codigo;
    }

    public SimpleStringProperty getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
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
    
    public SimpleDoubleProperty getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }    
    
}
