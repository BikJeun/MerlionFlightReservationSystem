/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassEntity;
import exceptions.AircraftConfigExistException;
import exceptions.AircraftConfigNotFoundException;
import exceptions.AircraftTypeNotFoundException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Stateless
public class AircraftConfigurationSessionBean implements AircraftConfigurationSessionBeanRemote, AircraftConfigurationSessionBeanLocal {

    @EJB
    private AircraftTypeSessionBeanLocal aircraftTypeSessionBean;
    
    

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    public AircraftConfigurationSessionBean() {
    }

    @Override
    public AircraftConfigurationEntity createNewAircraftConfig(AircraftConfigurationEntity aircraftConfig) throws AircraftConfigExistException, UnknownPersistenceException {
        try {
            em.persist(aircraftConfig);
            em.flush();
            return aircraftConfig;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AircraftConfigExistException("This configuration already exist!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
@Override
    public AircraftConfigurationEntity retriveAircraftConfigByID(Long aircraftConfigID) throws AircraftConfigNotFoundException {
        AircraftConfigurationEntity config = em.find(AircraftConfigurationEntity.class, aircraftConfigID);
        if(config != null) {
            return config;
        } else {
            throw new AircraftConfigNotFoundException("Aircraft Configuration with " + aircraftConfigID + " does not exist!\n");
        }
    }
    
    @Override
    public int calculateMaxCapacity(AircraftConfigurationEntity aircraftConfig) {
        int max = 0;
        
        for(CabinClassEntity cabin: aircraftConfig.getCabin()) {
            max += cabin.getMaxSeatCapacity();
        }
        return max;
    }

    @Override
    public void associateTypeWithConfig(Long i, Long aircraftConfigID) {
        try {
            AircraftTypeEntity type = aircraftTypeSessionBean.retrieveAircraftTypeById(i);
            AircraftConfigurationEntity config = retriveAircraftConfigByID(aircraftConfigID);
            
            if(type != null && config != null) {
                type.getAircraftConfig().add(config);
                config.setAircraftType(type);
            }
        } catch (AircraftTypeNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (AircraftConfigNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public List<AircraftConfigurationEntity> retrieveAllConfiguration() {
        Query query = em.createQuery("SELECT a FROM AircraftConfigurationEntity a ORDER BY a.aircraftType ASC, a.name ASC ");
        return query.getResultList();
    } 
}

    
    
    

   
    

   

