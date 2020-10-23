/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.CabinClassEntity;
import exceptions.AircraftConfigNotFoundException;
import exceptions.CabinClassExistException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Local;

/**
 *
 * @author Mitsuki
 */
@Local
public interface CabinClassSessionBeanLocal {
    
    public CabinClassEntity createNewCabinClass(CabinClassEntity cabin, AircraftConfigurationEntity aircraft);
    
}
