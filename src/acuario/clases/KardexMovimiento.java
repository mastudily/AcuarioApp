/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Marcelo Astudillo
 */

public class KardexMovimiento {
    
    protected String kardexfecha;
    protected Integer solicitudinsumoid;
    protected String solicitudinsumodescripcion;
    protected String solicitudinsumotipo;
    protected BigDecimal kardexultimacantidad;
    protected BigDecimal kardexcantidad;
    protected BigDecimal kardexactualcantidad;

    public String getKardexfecha() {
        return kardexfecha;
    }

    public void setKardexfecha(String kardexfecha) {
        this.kardexfecha = kardexfecha;
    }

    public Integer getSolicitudinsumoid() {
        return solicitudinsumoid;
    }

    public void setSolicitudinsumoid(Integer solicitudinsumoid) {
        this.solicitudinsumoid = solicitudinsumoid;
    }

    public String getSolicitudinsumodescripcion() {
        return solicitudinsumodescripcion;
    }

    public void setSolicitudinsumodescripcion(String solicitudinsumodescripcion) {
        this.solicitudinsumodescripcion = solicitudinsumodescripcion;
    }

    public String getSolicitudinsumotipo() {
        return solicitudinsumotipo;
    }

    public void setSolicitudinsumotipo(String solicitudinsumotipo) {
        this.solicitudinsumotipo = solicitudinsumotipo;
    }

    public BigDecimal getKardexultimacantidad() {
        return kardexultimacantidad;
    }

    public void setKardexultimacantidad(BigDecimal kardexultimacantidad) {
        this.kardexultimacantidad = kardexultimacantidad;
    }

    public BigDecimal getKardexcantidad() {
        return kardexcantidad;
    }

    public void setKardexcantidad(BigDecimal kardexcantidad) {
        this.kardexcantidad = kardexcantidad;
    }

    public BigDecimal getKardexactualcantidad() {
        return kardexactualcantidad;
    }

    public void setKardexactualcantidad(BigDecimal kardexactualcantidad) {
        this.kardexactualcantidad = kardexactualcantidad;
    }


    
    
}