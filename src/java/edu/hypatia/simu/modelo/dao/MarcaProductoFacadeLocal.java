/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hypatia.simu.modelo.dao;

import edu.hypatia.simu.modelo.entidades.Accesorio;
import edu.hypatia.simu.modelo.entidades.MarcaProducto;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author davrivas
 */
@Local
public interface MarcaProductoFacadeLocal {

    void create(MarcaProducto marcaProducto);

    void edit(MarcaProducto marcaProducto);

    void remove(MarcaProducto marcaProducto);

    MarcaProducto find(Object id);

    List<MarcaProducto> findAll();

    List<MarcaProducto> findRange(int[] range);

    int count();
    
    
}
