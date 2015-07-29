/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import acuario.services.Auditoria;
import acuario.webservices.Seguridad;
import java.net.InetAddress;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Marcelo Astudillo
 */
public class GenerarAuditoria {

    public static void grabaRegistro(String tabla, Integer registroid, String transaccion, String usuario) {
        Auditoria auditoria = new Auditoria();
        auditoria.setAuditoriatabla(tabla);
        auditoria.setAuditoriaregistroid(registroid);
        auditoria.setAuditoriatransaccion(transaccion);
        auditoria.setAuditoriausuario(usuario);
        XMLGregorianCalendar fecha = null;
        try {
            fecha = Util.fechaSistema();
            auditoria.setAuditoriaestacion(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception ex) {
            System.out.println("Error:"+ex);
        }
        auditoria.setAuditoriafecha(fecha);        
        auditoria.setAuditoriaprograma(System.getenv("COMPUTERNAME"));
        auditoria.setAuditoriaestado("ACTIVO");
        Seguridad.createAuditoria(auditoria);
    }
}
