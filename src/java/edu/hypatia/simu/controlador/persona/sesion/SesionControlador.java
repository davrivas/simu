/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hypatia.simu.controlador.persona.sesion;

import edu.hypatia.simu.modelo.dao.UsuarioFacadeLocal;
import edu.hypatia.simu.modelo.entidades.Usuario;
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
        String cp = ec.getRequestContextPath();

        try {
            usuario = ufl.findByEmailContrasena(email, contrasena);
            System.out.println(usuario);

            switch (usuario.getRol().getIdRol()) { // Evalúo el rol seleccionado
                case 1:
                    return ""; // me redirige a la pagina en la que haya iniciado sesión
                case 2:
                    return "/mecanico/index.xhtml?faces-redirect=true"; // me redirige a la pagina del mecánico
                case 3:
                    return "/administrador/index.xhtml?faces-redirect=true"; // me redirige a la pagina del administrador
            }
        } catch (NullPointerException e) {
            // Es recomendable quitar este tipo de mensajes y mostrar unos mas atractivos (alerta)
            fc.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Datos incorrectos",
                    "Email y/o contrasena no son validos."));
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

    public void changeLanguage(String lang) {
        idioma = new Locale(lang);
    }
    
    public String editar() {
        ufl.edit(usuario);
        
        return "index.xhtml?faces-redirect=true";
    }
}
