/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.webservices;

import acuario.services.Ingresoforpag;


/**
 *
 * @author Marcelo Astudillo
 */
public class Transaccion {
    
    public static Integer createIdIngreso(acuario.services.Ingreso entity) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.createIdIngreso(entity);
    }

    public static boolean editIngreso(acuario.services.Ingreso entity) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.editIngreso(entity);
    }

    public static boolean removeIngreso(acuario.services.Ingreso entity) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.removeIngreso(entity);
    }

    public static acuario.services.Ingreso findByIngresoid(java.lang.Integer id) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.findByIngresoid(id);
    }

    public static java.util.List<acuario.services.Ingreso> findIngresoByFecha(javax.xml.datatype.XMLGregorianCalendar fecha) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.findIngresoByFecha(fecha);
    }

    public static java.util.List<acuario.services.Ingreso> findIngresoByIdentidad(java.lang.String rut) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.findIngresoByIdentidad(rut);
    }

    public static java.util.List<acuario.services.Ingreso> findIngresoByNombre(java.lang.String nombre) {
        acuario.services.IngresoWS_Service service = new acuario.services.IngresoWS_Service();
        acuario.services.IngresoWS port = service.getIngresoWSPort();
        return port.findIngresoByNombre(nombre);
    }

    public static boolean createIngresoforpag(acuario.services.Ingresoforpag entity) {
        acuario.services.IngresoforpagWS_Service service = new acuario.services.IngresoforpagWS_Service();
        acuario.services.IngresoforpagWS port = service.getIngresoforpagWSPort();
        return port.createIngresoforpag(entity);
    }

    public static boolean editIngresoforpag(acuario.services.Ingresoforpag entity) {
        acuario.services.IngresoforpagWS_Service service = new acuario.services.IngresoforpagWS_Service();
        acuario.services.IngresoforpagWS port = service.getIngresoforpagWSPort();
        return port.editIngresoforpag(entity);
    }

    public static boolean removeIngresoforpag(acuario.services.Ingresoforpag entity) {
        acuario.services.IngresoforpagWS_Service service = new acuario.services.IngresoforpagWS_Service();
        acuario.services.IngresoforpagWS port = service.getIngresoforpagWSPort();
        return port.removeIngresoforpag(entity);
    }

    public static java.util.List<acuario.services.Ingresoforpag> findByIngresoforpagIngresoid(java.lang.Integer id) {
        acuario.services.IngresoforpagWS_Service service = new acuario.services.IngresoforpagWS_Service();
        acuario.services.IngresoforpagWS port = service.getIngresoforpagWSPort();
        return port.findByIngresoforpagIngresoid(id);
    }

    public static Ingresoforpag findIngresoforpag(java.lang.Object id) {
        acuario.services.IngresoforpagWS_Service service = new acuario.services.IngresoforpagWS_Service();
        acuario.services.IngresoforpagWS port = service.getIngresoforpagWSPort();
        return port.findIngresoforpag(id);
    }

        
}
