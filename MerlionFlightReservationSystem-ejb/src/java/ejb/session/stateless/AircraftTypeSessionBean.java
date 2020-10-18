/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.AircraftTypeEntity;
import exceptions.AircraftTypeExistException;
import exceptions.AircraftTypeNotFoundException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class AircraftTypeSessionBean implements AircraftTypeSessionBeanRemote, AircraftTypeSessionBeanLocal {
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public AircraftTypeSessionBean() {
    }
    
    @Override
    public AircraftTypeEntity createNewAircraftType(AircraftTypeEntity aircraftType) throws AircraftTypeExistException, UnknownPersistenceException {
        try {
            em.persist(aircraftType);
            em.flush();
            return aircraftType;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AircraftTypeExistException("An aircraft type with the same type name exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public AircraftTypeEntity retrieveAircraftTypeById(Long id) throws AircraftTypeNotFoundException {
        AircraftTypeEntity aircraftType = em.find(AircraftTypeEntity.class, id);
        if(aircraftType != null) {
            return aircraftType;
        } else {
            throw new AircraftTypeNotFoundException("AircraftType id " + id.toString() + " does not exist!");
        }
    }
}