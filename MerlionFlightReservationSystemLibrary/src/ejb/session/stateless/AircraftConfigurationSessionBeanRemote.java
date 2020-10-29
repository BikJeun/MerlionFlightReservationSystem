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
import javax.ejb.Remote;
import javax.persistence.NoResultException;

/**
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Remote
public interface AircraftConfigurationSessionBeanRemote {

    public AircraftConfigurationEntity createNewAircraftConfig(AircraftConfigurationEntity aircraftConfig, long aircraftTypeID, List<CabinClassEntity> cabinClasses) throws CreateNewAircraftConfigException, AircraftConfigExistException, UnknownPersistenceException;

    public void associateTypeWithConfig(Long valueOf, Long aircraftConfigID);

    public List<AircraftConfigurationEntity> retrieveAllConfiguration();
    
    public AircraftConfigurationEntity retriveAircraftConfigByID(Long aircraftConfigID) throws AircraftConfigNotFoundException;
    
}
