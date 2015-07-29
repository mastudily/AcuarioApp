/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.webservices;

import acuario.services.Perfil;
import acuario.services.Programa;
import acuario.services.Usuario;

/**
 *
 * @author Marcelo Astudillo
 */
public class Seguridad {

    public static Usuario validarUsuario(java.lang.String identificacion, java.lang.String clave) {
        acuario.services.UsuarioWS_Service service = new acuario.services.UsuarioWS_Service();
        acuario.services.UsuarioWS port = service.getUsuarioWSPort();
        return port.validarUsuario(identificacion, clave);
    }
    

    public static void actualizaPersona(acuario.services.Persona entity) {
        acuario.services.PersonaWS_Service service = new acuario.services.PersonaWS_Service();
        acuario.services.PersonaWS port = service.getPersonaWSPort();
        port.editPersona(entity);
    }

    public static java.util.List<acuario.services.Programaperfil> listaProgramas(java.lang.Integer perfilid) {
        acuario.services.ProgramaperfilWS_Service service = new acuario.services.ProgramaperfilWS_Service();
        acuario.services.ProgramaperfilWS port = service.getProgramaperfilWSPort();
        return port.listaProgramas(perfilid);
    }

    public static java.util.List<acuario.services.Programa> listaProgramasTipo(java.lang.Integer perfilid, java.lang.String programatipo, java.lang.Integer programapadreid) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.listaProgramasTipo(perfilid, programatipo, programapadreid);
    }

    public static boolean createAuditoria(acuario.services.Auditoria entity) {
        acuario.services.AuditoriaWS_Service service = new acuario.services.AuditoriaWS_Service();
        acuario.services.AuditoriaWS port = service.getAuditoriaWSPort();
        return port.createAuditoria(entity);
    }

    public static Integer createIdPrograma(acuario.services.Programa entity) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.createIdPrograma(entity);
    }

    public static boolean editPrograma(acuario.services.Programa entity) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.editPrograma(entity);
    }

    public static boolean removePrograma(acuario.services.Programa entity) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.removePrograma(entity);
    }

    public static java.util.List<acuario.services.Programa> findProgramaById(java.lang.Integer id) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.findProgramaById(id);
    }

    public static java.util.List<acuario.services.Programa> findProgramaByNombre(java.lang.String nombre) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.findProgramaByNombre(nombre);
    }

    public static Programa findPrograma(java.lang.Object id) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.findPrograma(id);
    }

    public static Integer createIdPerfil(acuario.services.Perfil entity) {
        acuario.services.PerfilWS_Service service = new acuario.services.PerfilWS_Service();
        acuario.services.PerfilWS port = service.getPerfilWSPort();
        return port.createIdPerfil(entity);
    }

    public static boolean editPerfil(acuario.services.Perfil entity) {
        acuario.services.PerfilWS_Service service = new acuario.services.PerfilWS_Service();
        acuario.services.PerfilWS port = service.getPerfilWSPort();
        return port.editPerfil(entity);
    }

    public static boolean removePerfil(acuario.services.Perfil entity) {
        acuario.services.PerfilWS_Service service = new acuario.services.PerfilWS_Service();
        acuario.services.PerfilWS port = service.getPerfilWSPort();
        return port.removePerfil(entity);
    }

    public static java.util.List<acuario.services.Perfil> findPerfilById(java.lang.Integer id) {
        acuario.services.PerfilWS_Service service = new acuario.services.PerfilWS_Service();
        acuario.services.PerfilWS port = service.getPerfilWSPort();
        return port.findPerfilById(id);
    }

    public static java.util.List<acuario.services.Perfil> findPerfilByNombre(java.lang.String nombre) {
        acuario.services.PerfilWS_Service service = new acuario.services.PerfilWS_Service();
        acuario.services.PerfilWS port = service.getPerfilWSPort();
        return port.findPerfilByNombre(nombre);
    }

    public static Integer createIdUsuario(acuario.services.Usuario entity) {
        acuario.services.UsuarioWS_Service service = new acuario.services.UsuarioWS_Service();
        acuario.services.UsuarioWS port = service.getUsuarioWSPort();
        return port.createIdUsuario(entity);
    }

    public static boolean editUsuario(acuario.services.Usuario entity) {
        acuario.services.UsuarioWS_Service service = new acuario.services.UsuarioWS_Service();
        acuario.services.UsuarioWS port = service.getUsuarioWSPort();
        return port.editUsuario(entity);
    }

    public static boolean removeUsuario(acuario.services.Usuario entity) {
        acuario.services.UsuarioWS_Service service = new acuario.services.UsuarioWS_Service();
        acuario.services.UsuarioWS port = service.getUsuarioWSPort();
        return port.removeUsuario(entity);
    }

    public static java.util.List<acuario.services.Usuario> findUsuarioByIdentidad(java.lang.String rut) {
        acuario.services.UsuarioWS_Service service = new acuario.services.UsuarioWS_Service();
        acuario.services.UsuarioWS port = service.getUsuarioWSPort();
        return port.findUsuarioByIdentidad(rut);
    }

    public static java.util.List<acuario.services.Usuario> findUsuarioByNombre(java.lang.String nombre) {
        acuario.services.UsuarioWS_Service service = new acuario.services.UsuarioWS_Service();
        acuario.services.UsuarioWS port = service.getUsuarioWSPort();
        return port.findUsuarioByNombre(nombre);
    }

    public static Perfil findPerfil(java.lang.Object id) {
        acuario.services.PerfilWS_Service service = new acuario.services.PerfilWS_Service();
        acuario.services.PerfilWS port = service.getPerfilWSPort();
        return port.findPerfil(id);
    }

    public static boolean createProgramaperfil(acuario.services.Programaperfil entity) {
        acuario.services.ProgramaperfilWS_Service service = new acuario.services.ProgramaperfilWS_Service();
        acuario.services.ProgramaperfilWS port = service.getProgramaperfilWSPort();
        return port.createProgramaperfil(entity);
    }

    public static boolean editProgramaperfil(acuario.services.Programaperfil entity) {
        acuario.services.ProgramaperfilWS_Service service = new acuario.services.ProgramaperfilWS_Service();
        acuario.services.ProgramaperfilWS port = service.getProgramaperfilWSPort();
        return port.editProgramaperfil(entity);
    }

    public static boolean removeProgramaperfil(acuario.services.Programaperfil entity) {
        acuario.services.ProgramaperfilWS_Service service = new acuario.services.ProgramaperfilWS_Service();
        acuario.services.ProgramaperfilWS port = service.getProgramaperfilWSPort();
        return port.removeProgramaperfil(entity);
    }

    public static java.util.List<acuario.services.Programa> findProgramaByTipo(java.lang.String tipo) {
        acuario.services.ProgramaWS_Service service = new acuario.services.ProgramaWS_Service();
        acuario.services.ProgramaWS port = service.getProgramaWSPort();
        return port.findProgramaByTipo(tipo);
    }
    
    
    
}
