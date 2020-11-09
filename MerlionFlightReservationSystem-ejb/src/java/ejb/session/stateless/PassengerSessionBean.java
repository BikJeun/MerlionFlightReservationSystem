/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.PassengerEntity;
import exceptions.PassengerAlreadyExistException;
import exceptions.PassengerNotFoundException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class PassengerSessionBean implements PassengerSessionBeanRemote, PassengerSessionBeanLocal {
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public PassengerSessionBean() {
    }
    
    @Override
    public PassengerEntity createNewPassenger(PassengerEntity passenger) throws PassengerAlreadyExistException, UnknownPersistenceException {
        try {
            em.persist(passenger);
            em.flush();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new PassengerAlreadyExistException("Passenger with " + passenger.getPassengerID() + " already exist");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
        return passenger;
    }

    @Override
    public PassengerEntity retrievePassengerByPassport(String passport) {
        Query query = em.createQuery("SELECT p FROM PassengerEntity p WHERE p.passportNumber = :passport");
        query.setParameter("passport", passport);
        
        return (PassengerEntity)query.getSingleResult();
    }
    
}
