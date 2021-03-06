/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hypatia.simu.controlador.reparacion;

import edu.hypatia.simu.controlador.mail.Mail;
import edu.hypatia.simu.controlador.usuario.sesion.SesionControlador;
import edu.hypatia.simu.modelo.dao.EstadoMotoFacadeLocal;
import edu.hypatia.simu.modelo.dao.MarcaFacadeLocal;
import edu.hypatia.simu.modelo.dao.MotoFacadeLocal;
import edu.hypatia.simu.modelo.dao.ProductoFacadeLocal;
import edu.hypatia.simu.modelo.dao.ReparacionFacadeLocal;
import edu.hypatia.simu.modelo.entidades.Marca;
import edu.hypatia.simu.modelo.entidades.Moto;
import edu.hypatia.simu.modelo.entidades.Producto;
import edu.hypatia.simu.modelo.entidades.Reparacion;
import edu.hypatia.simu.modelo.entidades.TipoReparacion;
import edu.hypatia.simu.modelo.entidades.Usuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.inject.Inject;

/**
 *
 * @author davrivas
 */
@Named(value = "reparacionClienteControlador")
@SessionScoped
public class ReparacionClienteControlador implements Serializable {

    @EJB
    private ReparacionFacadeLocal rfl;
    @EJB
    private MotoFacadeLocal mfl;
    @EJB
    private ProductoFacadeLocal pfl;
    @EJB
    private EstadoMotoFacadeLocal efl;
    @EJB
    private MarcaFacadeLocal marcafl;

    @Inject
    private SesionControlador sc;

    private Moto motoSeleccionada = new Moto();
    private Producto productoNuevo = new Producto();
    private Moto motoNueva = new Moto();
    private List<Moto> motosEnReparacion;
    private List<Marca> marcasMoto;

    private Reparacion reparacionSeleccionada = new Reparacion();
    private List<Reparacion> reparacionesDelCliente;
    private Reparacion reparacionAgendada = new Reparacion();

    private final DateFormat hoyFormato = new SimpleDateFormat("yyyy-MM-dd");
    private final String hoyString = hoyFormato.format(new Date());

    /**
     * Creates a new instance of ReparacionCliente
     */
    public ReparacionClienteControlador() {
    }

    public List<Reparacion> getReparacionesDelCliente() {
        return rfl.reparacionesDelCliente(sc.getUsuario());
    }

    public List<Marca> getMarcasMoto() {
        return marcafl.listarMarcaMoto();
    }

    public Moto getMotoSeleccionada() {
        return motoSeleccionada;
    }

    public void setMotoSeleccionada(Moto motoSeleccionada) {
        this.motoSeleccionada = motoSeleccionada;
    }

    public Producto getProductoNuevo() {
        return productoNuevo;
    }

    public void setProductoNuevo(Producto productoNuevo) {
        this.productoNuevo = productoNuevo;
    }

    public Moto getMotoNueva() {
        return motoNueva;
    }

    public void setMotoNueva(Moto motoNueva) {
        this.motoNueva = motoNueva;
    }

    public List<Moto> getMotosEnReparacion() {
        return mfl.motosEnReparacion(sc.getUsuario());
    }

    public Reparacion getReparacionSeleccionada() {
        return reparacionSeleccionada;
    }

    public void setReparacionSeleccionada(Reparacion reparacionSeleccionada) {
        this.reparacionSeleccionada = reparacionSeleccionada;
    }

    public Reparacion getReparacionAgendada() {
        return reparacionAgendada;
    }

    public void setReparacionAgendada(Reparacion reparacionAgendada) {
        this.reparacionAgendada = reparacionAgendada;
    }

    public String getHoyString() {
        return hoyString;
    }

    public void seleccionarMoto(Moto m) {
        motoSeleccionada = m;
    }

    public String getCalificacionReparacion(Reparacion r) {
        if (r.getCalificacion() == null) {
            if (sc.getIdioma().equals(new Locale("es"))) {
                return "<em>No has calificado la reparaci??n</em>";
            } else if (sc.getIdioma().equals(new Locale("en"))) {
                return "<em>You haven't rated the repair</em>";
            }
        }
        String rta = "";
        for (int i = 0; i < r.getCalificacion(); i++) {
            rta += "<span class='fa fa-star' style='color:orange;'></span>";
        }

        return rta;
    }

    public void seleccionarReparacion(Reparacion r) {
        reparacionSeleccionada = r;
    }

    public String agendarCita() throws ParseException {
        DateFormat formatoFecha = new SimpleDateFormat("yyyy/MM/dd");
        String fecha = formatoFecha.format(reparacionAgendada.getFecha());
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
        String hora = formatoHora.format(reparacionAgendada.getHora());
        List<TipoReparacion> servicios = reparacionAgendada.getTipoReparacionList();

        rfl.create(reparacionAgendada);

        // Enviar mail
        String nombreCliente = sc.getUsuario().getNombre() + " " + sc.getUsuario().getApellido();
        String placaMoto = reparacionAgendada.getMoto().getPlaca();
        String nombreMecanico = reparacionAgendada.getMecanico().getNombre() + " " + reparacionAgendada.getMecanico().getApellido();
        String tiposDeServicio = "Los tipos de servicio son<br>";
        for (TipoReparacion s : servicios) {
            tiposDeServicio += "<li>" + s.getServicio() + "</li>";
        }
        String cuerpoHTML;

        // Para el cliente
        String destinatario = sc.getUsuario().getEmail();
        String asunto = "Reparaci??n programada";
        cuerpoHTML = "<h1>Hola " + nombreCliente + "</h1>"
                + "Has programado una reparacion para tu moto con placa " + placaMoto + "<br>"
                + "Para el d??a " + fecha + " a las " + hora + "<br>"
                + "<br>" + tiposDeServicio + "<br>"
                + "Te atender?? " + nombreMecanico;
        Mail.sendMail(destinatario, asunto, cuerpoHTML);

        // Para el mec??nico
        destinatario = reparacionAgendada.getMecanico().getEmail();
        cuerpoHTML = "<h1>Hola " + nombreMecanico + "</h1>"
                + "El cliente " + nombreCliente + " ha programado una reparacion "
                + "para la moto con placa " + placaMoto + "<br>"
                + "Para el d??a " + fecha + " a las " + hora + "<br>"
                + "<br>" + tiposDeServicio;
        Mail.sendMail(destinatario, asunto, cuerpoHTML);

        reparacionAgendada = new Reparacion();

        return "";
    }

    public String calificar() {
        // Enviar mail
        String nombreCliente = sc.getUsuario().getNombre() + " " + sc.getUsuario().getApellido();
        String nombreMecanico = reparacionSeleccionada.getMecanico().getNombre() + " " + reparacionSeleccionada.getMecanico().getApellido();

        String asunto = "Reparaci??n calificada";
        String destinatario = reparacionSeleccionada.getMecanico().getEmail();
        String cuerpoHTML = "<h1>Hola " + nombreMecanico + "</h1>"
                + "El cliente " + nombreCliente + " ha calificado una reparaci??n "
                + "con " + reparacionSeleccionada.getCalificacion() + " de 5.<br>"
                + "Los datos de la reparacion son:";
        Mail.sendMail(destinatario, asunto, cuerpoHTML);

        rfl.edit(reparacionSeleccionada);
        reparacionSeleccionada = new Reparacion();

        return "";
    }

    public void registrarNuevaMotoEnReparacion() {
        productoNuevo.setPrecio(0);
        productoNuevo.setUrlFoto("");
        productoNuevo.setAltFoto("");
        pfl.create(productoNuevo);
        motoNueva.setProducto(productoNuevo);
        motoNueva.setCliente(sc.getUsuario());
        motoNueva.setEstadoMoto(efl.find(5));
        mfl.create(motoNueva);
    }
    
    public String getPromedioMecanico(Usuario mecanico) {
        if (mecanico.getReparacionList() == null || mecanico.getReparacionList().isEmpty()) {
            return "";
        }
        
        List<Integer> acumuladoList = new ArrayList<>();
        
        for (Reparacion r : mecanico.getReparacionList()) {
            if (r.getCalificacion() != null) {
                acumuladoList.add(r.getCalificacion());
            }
        }
        
        if (acumuladoList.isEmpty()) {
            return "";
        }
        
        int acumulado = 0;
        
        for (Integer a : acumuladoList) {
            acumulado += a;
        }
        
        double promedio = acumulado / acumuladoList.size();
        
        return "(" + promedio + " / 5)";
    }
}
