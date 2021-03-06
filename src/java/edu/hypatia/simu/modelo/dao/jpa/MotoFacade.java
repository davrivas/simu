/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hypatia.simu.modelo.dao.jpa;

import edu.hypatia.simu.modelo.dao.MotoFacadeLocal;
import edu.hypatia.simu.modelo.entidades.Moto;
import edu.hypatia.simu.modelo.entidades.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author davr
 */
@Stateless
public class MotoFacade extends AbstractFacade<Moto> implements MotoFacadeLocal {

    @PersistenceContext(unitName = "simuPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotoFacade() {
        super(Moto.class);
    }

    @Override
    public List<Moto> motosEnReparacion(Usuario cliente) {
        try {
            TypedQuery<Moto> q = getEntityManager().createQuery("SELECT m FROM Moto m WHERE m.cliente = :cliente", Moto.class);
            q.setParameter("cliente", cliente);
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Moto> listarMoto() {
        TypedQuery<Moto> t = getEntityManager().createQuery("SELECT m FROM Moto m INNER JOIN m.estadoMoto em INNER JOIN m.producto p INNER JOIN p.marca mp INNER JOIN mp.tipoProducto t WHERE t.idTipoProducto = 1 AND em.idEstadoMoto = 3", Moto.class);
        return t.getResultList();
    }

    @Override
    public List<Moto> filtrarPorMarcaMoto(String marca) {
        Query q = getEntityManager().createQuery("SELECT m FROM Moto m  INNER JOIN m.producto p INNER JOIN p.marca mp WHERE mp.marca = :marca");
        q.setParameter("marca", marca);
        return q.getResultList();
    }

    @Override
    public List<Moto> filtrarPorPrecio(Double precioMin, Double precioMax) {
        Query q = getEntityManager().createQuery("SELECT m FROM Moto m INNER JOIN m.producto p WHERE p.precio BETWEEN :precioMin AND :precioMax");
        q.setParameter("precioMin", precioMin);
        q.setParameter("precioMax", precioMax);
        return q.getResultList();
    }

    @Override
    public List<Moto> listarMotosOfrecidas() {
        try {
            TypedQuery<Moto> t = getEntityManager().createQuery("SELECT m FROM Moto m INNER JOIN m.estadoMoto em INNER JOIN m.producto p INNER JOIN p.marca mp INNER JOIN mp.tipoProducto t WHERE t.idTipoProducto = 1 AND em.idEstadoMoto = 1", Moto.class);
            return t.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

}
