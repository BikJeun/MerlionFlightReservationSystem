/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftTypeEntity;
import exceptions.AircraftTypeExistException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Local;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface AircraftTypeSessionBeanLocal {

    public AircraftTypeEntity createNewAircraftType(AircraftTypeEntity aircraftType) throws AircraftTypeExistException, UnknownPersistenceException;
    
}
