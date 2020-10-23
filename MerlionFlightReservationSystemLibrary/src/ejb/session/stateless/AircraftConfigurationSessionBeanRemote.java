/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import exceptions.AircraftConfigExistException;
import exceptions.AircraftConfigNotFoundException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Remote;

/**
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Remote
public interface AircraftConfigurationSessionBeanRemote {

    public AircraftConfigurationEntity createNewAircraftConfig(AircraftConfigurationEntity aircraftConfig) throws AircraftConfigExistException, UnknownPersistenceException;

    public int calculateMaxCapacity(AircraftConfigurationEntity aircraftConfig);

    public void associateTypeWithConfig(Long valueOf, Long aircraftConfigID);

    public List<AircraftConfigurationEntity> retrieveAllConfiguration();
    
    public AircraftConfigurationEntity retriveAircraftConfigByID(Long aircraftConfigID) throws AircraftConfigNotFoundException;
    
}
