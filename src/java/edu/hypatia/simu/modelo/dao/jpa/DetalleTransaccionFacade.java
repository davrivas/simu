/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hypatia.simu.modelo.dao.jpa;

import edu.hypatia.simu.modelo.dao.DetalleTransaccionFacadeLocal;
import edu.hypatia.simu.modelo.entidades.DetalleTransaccion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author davr
 */
@Stateless
public class DetalleTransaccionFacade extends AbstractFacade<DetalleTransaccion> implements DetalleTransaccionFacadeLocal {

    @PersistenceContext(unitName = "simuPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleTransaccionFacade() {
        super(DetalleTransaccion.class);
    }
    
}
