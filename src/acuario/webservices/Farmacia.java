/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.webservices;

import acuario.services.Insumo;
import acuario.services.Insumoempresa;
import acuario.services.Kardex;
import acuario.services.Solicitudinsumo;

/**
 *
 * @author Marcelo Astudillo
 */
public class Farmacia {

    public static Integer createIdInsumo(acuario.services.Insumo entity) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.createIdInsumo(entity);
    }

    public static boolean editInsumo(acuario.services.Insumo entity) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.editInsumo(entity);
    }

    public static boolean removeInsumo(acuario.services.Insumo entity) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.removeInsumo(entity);
    }

    public static Insumo findByInsumocodigo(java.lang.String codigo) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.findByInsumocodigo(codigo);
    }

    public static java.util.List<acuario.services.Insumo> findInsumoByCodigo(java.lang.String codigo) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.findInsumoByCodigo(codigo);
    }

    public static java.util.List<acuario.services.Insumo> findInsumoByNombre(java.lang.String nombre) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.findInsumoByNombre(nombre);
    }

    public static Insumo findByInsumoid(java.lang.Integer id) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.findByInsumoid(id);
    }
    
    public static boolean createInsumoempresa(acuario.services.Insumoempresa entity) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.createInsumoempresa(entity);
    }

    public static boolean editInsumoempresa(acuario.services.Insumoempresa entity) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.editInsumoempresa(entity);
    }

    public static boolean removeInsumoempresa(acuario.services.Insumoempresa entity) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.removeInsumoempresa(entity);
    }

    public static java.util.List<acuario.services.Insumoempresa> findInsumoempresaByCodigo(java.lang.String codigo, java.lang.Integer empresaid) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.findInsumoempresaByCodigo(codigo, empresaid);
    }

    public static java.util.List<acuario.services.Insumoempresa> findInsumoempresaByNombre(java.lang.String nombre, java.lang.Integer empresaid) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.findInsumoempresaByNombre(nombre, empresaid);
    }

    public static Integer createIdSolicitudinsumo(acuario.services.Solicitudinsumo entity) {
        acuario.services.SolicitudinsumoWS_Service service = new acuario.services.SolicitudinsumoWS_Service();
        acuario.services.SolicitudinsumoWS port = service.getSolicitudinsumoWSPort();
        return port.createIdSolicitudinsumo(entity);
    }

    public static boolean editSolicitudinsumo(acuario.services.Solicitudinsumo entity) {
        acuario.services.SolicitudinsumoWS_Service service = new acuario.services.SolicitudinsumoWS_Service();
        acuario.services.SolicitudinsumoWS port = service.getSolicitudinsumoWSPort();
        return port.editSolicitudinsumo(entity);
    }

    public static boolean removeSolicitudinsumo(acuario.services.Solicitudinsumo entity) {
        acuario.services.SolicitudinsumoWS_Service service = new acuario.services.SolicitudinsumoWS_Service();
        acuario.services.SolicitudinsumoWS port = service.getSolicitudinsumoWSPort();
        return port.removeSolicitudinsumo(entity);
    }

    public static Solicitudinsumo findSolicitudinsumoid(java.lang.Integer id) {
        acuario.services.SolicitudinsumoWS_Service service = new acuario.services.SolicitudinsumoWS_Service();
        acuario.services.SolicitudinsumoWS port = service.getSolicitudinsumoWSPort();
        return port.findSolicitudinsumoid(id);
    }
    
    public static java.util.List<acuario.services.Solicitudinsumo> findSolicitudinsumoByFecha(javax.xml.datatype.XMLGregorianCalendar fecha, java.lang.Integer empresaid) {
        acuario.services.SolicitudinsumoWS_Service service = new acuario.services.SolicitudinsumoWS_Service();
        acuario.services.SolicitudinsumoWS port = service.getSolicitudinsumoWSPort();
        return port.findSolicitudinsumoByFecha(fecha, empresaid);
    }

    public static boolean createKardex(acuario.services.Kardex entity) {
        acuario.services.KardexWS_Service service = new acuario.services.KardexWS_Service();
        acuario.services.KardexWS port = service.getKardexWSPort();
        return port.createKardex(entity);
    }

    public static boolean editKardex(acuario.services.Kardex entity) {
        acuario.services.KardexWS_Service service = new acuario.services.KardexWS_Service();
        acuario.services.KardexWS port = service.getKardexWSPort();
        return port.editKardex(entity);
    }

    public static boolean removeKardex(acuario.services.Kardex entity) {
        acuario.services.KardexWS_Service service = new acuario.services.KardexWS_Service();
        acuario.services.KardexWS port = service.getKardexWSPort();
        return port.removeKardex(entity);
    }

    public static java.util.List<acuario.services.Kardex> findByInsumoidPeriodo(java.lang.Integer id, javax.xml.datatype.XMLGregorianCalendar fecini, javax.xml.datatype.XMLGregorianCalendar fecfin, java.lang.String estado) {
        acuario.services.KardexWS_Service service = new acuario.services.KardexWS_Service();
        acuario.services.KardexWS port = service.getKardexWSPort();
        return port.findByInsumoidPeriodo(id, fecini, fecfin, estado);
    }
    
    public static Insumoempresa findInsumoempresa(java.lang.Object id) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.findInsumoempresa(id);
    }

    public static java.util.List<acuario.services.Kardex> findBySolicitudinsumoid(java.lang.Integer id) {
        acuario.services.KardexWS_Service service = new acuario.services.KardexWS_Service();
        acuario.services.KardexWS port = service.getKardexWSPort();
        return port.findBySolicitudinsumoid(id);
    }
    
    public static Insumoempresa findByInsumoempresacodigo(java.lang.String codigo, java.lang.Integer empresaid) {
        acuario.services.InsumoempresaWS_Service service = new acuario.services.InsumoempresaWS_Service();
        acuario.services.InsumoempresaWS port = service.getInsumoempresaWSPort();
        return port.findByInsumoempresacodigo(codigo, empresaid);
    }

    public static Insumo findInsumo(java.lang.Object id) {
        acuario.services.InsumoWS_Service service = new acuario.services.InsumoWS_Service();
        acuario.services.InsumoWS port = service.getInsumoWSPort();
        return port.findInsumo(id);
    }

    public static Kardex findKardex(java.lang.Object id) {
        acuario.services.KardexWS_Service service = new acuario.services.KardexWS_Service();
        acuario.services.KardexWS port = service.getKardexWSPort();
        return port.findKardex(id);
    }

    
}
