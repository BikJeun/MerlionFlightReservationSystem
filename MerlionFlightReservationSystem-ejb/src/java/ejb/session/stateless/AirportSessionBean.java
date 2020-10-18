/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import exceptions.AirportExistException;
import exceptions.AirportNotFoundException;
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
public class AirportSessionBean implements AirportSessionBeanRemote, AirportSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    public AirportSessionBean() {
    }

    @Override
    public AirportEntity createNewAirport(AirportEntity airport) throws AirportExistException, UnknownPersistenceException {
        try {
            em.persist(airport);
            em.flush();
            return airport;
        } catch (PersistenceException ex) {
           if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new AirportExistException("Airport same IATA code exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public AirportEntity retrieveAirportById(Long id) throws AirportNotFoundException {
        AirportEntity airport = em.find(AirportEntity.class, id);
        if(airport != null) {
            return airport;
        } else {
            throw new AirportNotFoundException("Airport id " + id.toString() + " does not exist!");
        }
    }
    }