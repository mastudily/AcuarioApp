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

public class InformeContable {
    
    protected String codigocuenta;
    protected String nombrecuenta;
    protected BigDecimal valordebe;
    protected BigDecimal valorhaber;

    public String getCodigocuenta() {
        return codigocuenta;
    }

    public void setCodigocuenta(String codigocuenta) {
        this.codigocuenta = codigocuenta;
    }

    public String getNombrecuenta() {
        return nombrecuenta;
    }

    public void setNombrecuenta(String nombrecuenta) {
        this.nombrecuenta = nombrecuenta;
    }

    public BigDecimal getValordebe() {
        return valordebe;
    }

    public void setValordebe(BigDecimal valordebe) {
        this.valordebe = valordebe;
    }

    public BigDecimal getValorhaber() {
        return valorhaber;
    }

    public void setValorhaber(BigDecimal valorhaber) {
        this.valorhaber = valorhaber;
    }
    
    
}