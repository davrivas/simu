/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hypatia.simu.controlador.usuario.sesion;

import edu.hypatia.simu.modelo.dao.UsuarioFacadeLocal;
import edu.hypatia.simu.modelo.entidades.Usuario;
import edu.hypatia.simu.util.PasswordUtil;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Hypatia
 */
@Named(value = "sesionControlador")
@SessionScoped
public class SesionControlador implements Serializable {

    private Usuario usuario;
    private Locale idioma;
    private String email;
    private String contrasena;

    @EJB
    private UsuarioFacadeLocal ufl;

    /**
     * Creates a new instance of SesionControlador
     */
    public SesionControlador() {
    }

    @PostConstruct
    public void init() {
        idioma = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Locale getIdioma() {
        return idioma;
    }

    public void setIdioma(Locale idioma) {
        this.idioma = idioma;
    }

    public String iniciarSesion() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
//        System.out.println("Contrase??a sin MD5: " + contrasena);
        contrasena = PasswordUtil.getMD5(contrasena);
//        System.out.println("Contrase??a con MD5: " + contrasena);

        try {
            usuario = ufl.findByEmailContrasena(email, contrasena);

            switch (usuario.getRol().getIdRol()) { // Eval??o el rol seleccionado
                case 1:
                    return ""; // me redirige a la pagina en la que haya iniciado sesi??n
                case 2:
                    return "/mecanico/index.xhtml?faces-redirect=true"; // me redirige a la pagina del mec??nico
                case 3:
                    return "/administrador/index.xhtml?faces-redirect=true"; // me redirige a la pagina del administrador
            }
        } catch (NullPointerException e) {
            fc.addMessage("form-login", new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Datos incorrectos:",
                    "email y/o contrase??a no son validos."));
        }

        return "";
    }

    public void validarSesion() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        if (usuario == null) {
            String path = ec.getRequestContextPath()
                    + "/index.xhtml";
            ec.redirect(path);
        }
    }

    public void cerrarSesion() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        usuario = null;
        email = null;
        contrasena = null;
        ec.invalidateSession();
        String path = ec.getRequestContextPath()
                + "/index.xhtml";
        ec.redirect(path);
    }

    public void validarRol(Integer idRol) throws IOException {
        if (usuario != null) {
            if (usuario.getRol().getIdRol() != idRol.intValue()) {
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                String path = ec.getRequestContextPath()
                        + "/index.xhtml";
                ec.redirect(path);
            }
        } else {
            validarSesion();
        }

    }

    public void validarCliente() throws IOException {
        if ((usuario == null) || (usuario != null && usuario.getRol().getIdRol() == 1)) {
            System.out.println("Se valid?? bien la sesi??n");
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            String path = ec.getRequestContextPath();
            switch (usuario.getRol().getIdRol()) {
                case 2:
                    path += "/mecanico/index.xhtml";
                    break;
                case 3:
                    path += "/administrador/index.xhtml";
                    break;
            }
            ec.redirect(path);
        }
    }

    public void changeLanguage(String lang) {
        idioma = new Locale(lang);
    }

    public String editar() {
        String passMD5 = PasswordUtil.getMD5(usuario.getContrasena());
        usuario.setContrasena(passMD5);
        ufl.edit(usuario);

        return "index.xhtml?faces-redirect=true";
    }
}
