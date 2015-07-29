/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.webservices;

import acuario.services.Constante;

/**
 *
 * @author Marcelo Astudillo
 */
public class Mantenimiento {

    public static java.util.List<acuario.services.Persona> findPersonaByIdentidad(java.lang.String rut) {
        acuario.services.PersonaWS_Service service = new acuario.services.PersonaWS_Service();
        acuario.services.PersonaWS port = service.getPersonaWSPort();
        return port.findPersonaByIdentidad(rut);
    }

    public static java.util.List<acuario.services.Persona> findPersonaByNombre(java.lang.String nombre) {
        acuario.services.PersonaWS_Service service = new acuario.services.PersonaWS_Service();
        acuario.services.PersonaWS port = service.getPersonaWSPort();
        return port.findPersonaByNombre(nombre);
    }

    public static java.util.List<acuario.services.Constante> findConstanteByCodigo(java.lang.String constantecodigo) {
        acuario.services.ConstanteWS_Service service = new acuario.services.ConstanteWS_Service();
        acuario.services.ConstanteWS port = service.getConstanteWSPort();
        return port.findConstanteByCodigo(constantecodigo);
    }

    public static boolean editPersona(acuario.services.Persona entity) {
        acuario.services.PersonaWS_Service service = new acuario.services.PersonaWS_Service();
        acuario.services.PersonaWS port = service.getPersonaWSPort();
        return port.editPersona(entity);
    }

    public static boolean removePersona(acuario.services.Persona entity) {
        acuario.services.PersonaWS_Service service = new acuario.services.PersonaWS_Service();
        acuario.services.PersonaWS port = service.getPersonaWSPort();
        return port.removePersona(entity);
    }

    public static Integer createIdPersona(acuario.services.Persona entity) {
        acuario.services.PersonaWS_Service service = new acuario.services.PersonaWS_Service();
        acuario.services.PersonaWS port = service.getPersonaWSPort();
        return port.createIdPersona(entity);
    }

    public static java.util.List<acuario.services.Cliente> findClienteByIdentidad(java.lang.String rut) {
        acuario.services.ClienteWS_Service service = new acuario.services.ClienteWS_Service();
        acuario.services.ClienteWS port = service.getClienteWSPort();
        return port.findClienteByIdentidad(rut);
    }

    public static java.util.List<acuario.services.Cliente> findClienteByNombre(java.lang.String nombre) {
        acuario.services.ClienteWS_Service service = new acuario.services.ClienteWS_Service();
        acuario.services.ClienteWS port = service.getClienteWSPort();
        return port.findClienteByNombre(nombre);
    }

    public static boolean editCliente(acuario.services.Cliente entity) {
        acuario.services.ClienteWS_Service service = new acuario.services.ClienteWS_Service();
        acuario.services.ClienteWS port = service.getClienteWSPort();
        return port.editCliente(entity);
    }

    public static Integer createIdCliente(acuario.services.Cliente entity) {
        acuario.services.ClienteWS_Service service = new acuario.services.ClienteWS_Service();
        acuario.services.ClienteWS port = service.getClienteWSPort();
        return port.createIdCliente(entity);
    }

    public static boolean removeCliente(acuario.services.Cliente entity) {
        acuario.services.ClienteWS_Service service = new acuario.services.ClienteWS_Service();
        acuario.services.ClienteWS port = service.getClienteWSPort();
        return port.removeCliente(entity);
    }

    public static Integer createIdConstante(acuario.services.Constante entity) {
        acuario.services.ConstanteWS_Service service = new acuario.services.ConstanteWS_Service();
        acuario.services.ConstanteWS port = service.getConstanteWSPort();
        return port.createIdConstante(entity);
    }

    public static boolean editConstante(acuario.services.Constante entity) {
        acuario.services.ConstanteWS_Service service = new acuario.services.ConstanteWS_Service();
        acuario.services.ConstanteWS port = service.getConstanteWSPort();
        return port.editConstante(entity);
    }

    public static boolean removeConstante(acuario.services.Constante entity) {
        acuario.services.ConstanteWS_Service service = new acuario.services.ConstanteWS_Service();
        acuario.services.ConstanteWS port = service.getConstanteWSPort();
        return port.removeConstante(entity);
    }

    public static java.util.List<acuario.services.Constante> findConstanteByCodigoMnt(java.lang.String constantecodigo) {
        acuario.services.ConstanteWS_Service service = new acuario.services.ConstanteWS_Service();
        acuario.services.ConstanteWS port = service.getConstanteWSPort();
        return port.findConstanteByCodigoMnt(constantecodigo);
    }

    public static Constante findConstanteByCodigoTipo(java.lang.String codigo, java.lang.String tipo) {
        acuario.services.ConstanteWS_Service service = new acuario.services.ConstanteWS_Service();
        acuario.services.ConstanteWS port = service.getConstanteWSPort();
        return port.findConstanteByCodigoTipo(codigo, tipo);
    }

    public static Integer createIdEmpresa(acuario.services.Empresa entity) {
        acuario.services.EmpresaWS_Service service = new acuario.services.EmpresaWS_Service();
        acuario.services.EmpresaWS port = service.getEmpresaWSPort();
        return port.createIdEmpresa(entity);
    }

    public static boolean editEmpresa(acuario.services.Empresa entity) {
        acuario.services.EmpresaWS_Service service = new acuario.services.EmpresaWS_Service();
        acuario.services.EmpresaWS port = service.getEmpresaWSPort();
        return port.editEmpresa(entity);
    }

    public static boolean removeEmpresa(acuario.services.Empresa entity) {
        acuario.services.EmpresaWS_Service service = new acuario.services.EmpresaWS_Service();
        acuario.services.EmpresaWS port = service.getEmpresaWSPort();
        return port.removeEmpresa(entity);
    }

    public static java.util.List<acuario.services.Empresa> findEmpresaByIdentidad(java.lang.String rut) {
        acuario.services.EmpresaWS_Service service = new acuario.services.EmpresaWS_Service();
        acuario.services.EmpresaWS port = service.getEmpresaWSPort();
        return port.findEmpresaByIdentidad(rut);
    }

    public static java.util.List<acuario.services.Empresa> findEmpresaByNombre(java.lang.String nombre) {
        acuario.services.EmpresaWS_Service service = new acuario.services.EmpresaWS_Service();
        acuario.services.EmpresaWS port = service.getEmpresaWSPort();
        return port.findEmpresaByNombre(nombre);
    }

    public static Integer createIdFormapago(acuario.services.Formapago entity) {
        acuario.services.FormapagoWS_Service service = new acuario.services.FormapagoWS_Service();
        acuario.services.FormapagoWS port = service.getFormapagoWSPort();
        return port.createIdFormapago(entity);
    }

    public static boolean editFormapago(acuario.services.Formapago entity) {
        acuario.services.FormapagoWS_Service service = new acuario.services.FormapagoWS_Service();
        acuario.services.FormapagoWS port = service.getFormapagoWSPort();
        return port.editFormapago(entity);
    }

    public static boolean removeFormapago(acuario.services.Formapago entity) {
        acuario.services.FormapagoWS_Service service = new acuario.services.FormapagoWS_Service();
        acuario.services.FormapagoWS port = service.getFormapagoWSPort();
        return port.removeFormapago(entity);
    }

    public static java.util.List<acuario.services.Formapago> findFormapagoById(java.lang.Integer id) {
        acuario.services.FormapagoWS_Service service = new acuario.services.FormapagoWS_Service();
        acuario.services.FormapagoWS port = service.getFormapagoWSPort();
        return port.findFormapagoById(id);
    }

    public static java.util.List<acuario.services.Formapago> findFormapagoByNombre(java.lang.String nombre) {
        acuario.services.FormapagoWS_Service service = new acuario.services.FormapagoWS_Service();
        acuario.services.FormapagoWS port = service.getFormapagoWSPort();
        return port.findFormapagoByNombre(nombre);
    }
    
    
}
