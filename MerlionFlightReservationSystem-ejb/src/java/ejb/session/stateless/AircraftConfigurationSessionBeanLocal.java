/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.CabinClassEntity;
import exceptions.AircraftConfigExistException;
import exceptions.AircraftConfigNotFoundException;
import exceptions.CreateNewAircraftConfigException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface AircraftConfigurationSessionBeanLocal {

    public AircraftConfigurationEntity createNewAircraftConfig(AircraftConfigurationEntity aircraftConfig, long aircraftTypeID, List<CabinClassEntity> cabinClasses) throws CreateNewAircraftConfigException, AircraftConfigExistException, UnknownPersistenceException;
    
    public AircraftConfigurationEntity retriveAircraftConfigByID(Long aircraftConfigID) throws AircraftConfigNotFoundException;
    
}
