/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import java.math.BigDecimal;

/**
 *
 * @author Marcelo Astudillo
 */

public class SolInsReporte {
    
    protected String insumocodigo;
    protected String insumonombre;
    protected String insumounmed;
    protected String insumopresentacion;
    protected BigDecimal kardexcantidad;
    protected BigDecimal kardexusado;

    public String getInsumocodigo() {
        return insumocodigo;
    }

    public void setInsumocodigo(String insumocodigo) {
        this.insumocodigo = insumocodigo;
    }

    public String getInsumonombre() {
        return insumonombre;
    }

    public void setInsumonombre(String insumonombre) {
        this.insumonombre = insumonombre;
    }

    public String getInsumounmed() {
        return insumounmed;
    }

    public void setInsumounmed(String insumounmed) {
        this.insumounmed = insumounmed;
    }

    public String getInsumopresentacion() {
        return insumopresentacion;
    }

    public void setInsumopresentacion(String insumopresentacion) {
        this.insumopresentacion = insumopresentacion;
    }

    public BigDecimal getKardexcantidad() {
        return kardexcantidad;
    }

    public void setKardexcantidad(BigDecimal kardexcantidad) {
        this.kardexcantidad = kardexcantidad;
    }

    public BigDecimal getKardexusado() {
        return kardexusado;
    }

    public void setKardexusado(BigDecimal kardexusado) {
        this.kardexusado = kardexusado;
    }

    

    
    
}