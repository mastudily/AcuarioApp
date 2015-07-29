/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.webservices;

import acuario.services.Cuentacontable;
import acuario.services.Diariodetalle;

/**
 *
 * @author Marcelo Astudillo
 */
public class Financiero {

    public static Integer createIdCuentacontable(acuario.services.Cuentacontable entity) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.createIdCuentacontable(entity);
    }

    public static boolean editCuentacontable(acuario.services.Cuentacontable entity) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.editCuentacontable(entity);
    }

    public static boolean removeCuentacontable(acuario.services.Cuentacontable entity) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.removeCuentacontable(entity);
    }    

    public static Cuentacontable findCuentacontable(java.lang.Object id) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.findCuentacontable(id);
    }

    public static Integer createIdDiario(acuario.services.Diario entity) {
        acuario.services.DiarioWS_Service service = new acuario.services.DiarioWS_Service();
        acuario.services.DiarioWS port = service.getDiarioWSPort();
        return port.createIdDiario(entity);
    }

    public static boolean editDiario(acuario.services.Diario entity) {
        acuario.services.DiarioWS_Service service = new acuario.services.DiarioWS_Service();
        acuario.services.DiarioWS port = service.getDiarioWSPort();
        return port.editDiario(entity);
    }

    public static boolean removeDiario(acuario.services.Diario entity) {
        acuario.services.DiarioWS_Service service = new acuario.services.DiarioWS_Service();
        acuario.services.DiarioWS port = service.getDiarioWSPort();
        return port.removeDiario(entity);
    }

    

    public static java.util.List<acuario.services.Cuentacontable> findByCuentacontablemayor(boolean mayor, java.lang.Integer empresaid) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.findByCuentacontablemayor(mayor, empresaid);
    }

    public static java.util.List<acuario.services.Cuentacontable> findCuentacontableByCodigo(java.lang.String codigo, java.lang.Integer empresaid) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.findCuentacontableByCodigo(codigo, empresaid);
    }

    public static java.util.List<acuario.services.Cuentacontable> findCuentacontableByNombre(java.lang.String nombre, java.lang.Integer empresaid) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.findCuentacontableByNombre(nombre, empresaid);
    }

    public static java.util.List<acuario.services.Diario> findDiarioByFecha(javax.xml.datatype.XMLGregorianCalendar fecha, java.lang.Integer empresaid) {
        acuario.services.DiarioWS_Service service = new acuario.services.DiarioWS_Service();
        acuario.services.DiarioWS port = service.getDiarioWSPort();
        return port.findDiarioByFecha(fecha, empresaid);
    }

    public static boolean createDiariodetalle(acuario.services.Diariodetalle entity) {
        acuario.services.DiariodetalleWS_Service service = new acuario.services.DiariodetalleWS_Service();
        acuario.services.DiariodetalleWS port = service.getDiariodetalleWSPort();
        return port.createDiariodetalle(entity);
    }

    public static boolean editDiariodetalle(acuario.services.Diariodetalle entityDiariodetalle) {
        acuario.services.DiariodetalleWS_Service service = new acuario.services.DiariodetalleWS_Service();
        acuario.services.DiariodetalleWS port = service.getDiariodetalleWSPort();
        return port.editDiariodetalle(entityDiariodetalle);
    }

    public static boolean removeDiariodetalle(acuario.services.Diariodetalle entity) {
        acuario.services.DiariodetalleWS_Service service = new acuario.services.DiariodetalleWS_Service();
        acuario.services.DiariodetalleWS port = service.getDiariodetalleWSPort();
        return port.removeDiariodetalle(entity);
    }    

    public static Diariodetalle findDiariodetalle(java.lang.Object id) {
        acuario.services.DiariodetalleWS_Service service = new acuario.services.DiariodetalleWS_Service();
        acuario.services.DiariodetalleWS port = service.getDiariodetalleWSPort();
        return port.findDiariodetalle(id);
    }
    
    public static java.util.List<acuario.services.Diariodetalle> findByDiarioid(java.lang.Integer id) {
        acuario.services.DiariodetalleWS_Service service = new acuario.services.DiariodetalleWS_Service();
        acuario.services.DiariodetalleWS port = service.getDiariodetalleWSPort();
        return port.findByDiarioid(id);
    }

    public static Cuentacontable findByCuentacontablecodigo(java.lang.String codigo, java.lang.Integer empresaid) {
        acuario.services.CuentacontableWS_Service service = new acuario.services.CuentacontableWS_Service();
        acuario.services.CuentacontableWS port = service.getCuentacontableWSPort();
        return port.findByCuentacontablecodigo(codigo, empresaid);
    }

    public static java.util.List<acuario.services.Diariodetalle> findDiariodetalleByFecha(javax.xml.datatype.XMLGregorianCalendar fechaInicial, javax.xml.datatype.XMLGregorianCalendar fechaFinal, java.lang.Integer empresaid) {
        acuario.services.DiariodetalleWS_Service service = new acuario.services.DiariodetalleWS_Service();
        acuario.services.DiariodetalleWS port = service.getDiariodetalleWSPort();
        return port.findDiariodetalleByFecha(fechaInicial, fechaFinal, empresaid);
    }
    
    
}
