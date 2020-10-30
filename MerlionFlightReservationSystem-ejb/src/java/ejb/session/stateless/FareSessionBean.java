/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.FareEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.AircraftConfigExistException;
import exceptions.CabinClassNotFoundException;
import exceptions.FareExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.UnknownPersistenceException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class FareSessionBean implements FareSessionBeanRemote, FareSessionBeanLocal {

    @EJB
    private CabinClassSessionBeanLocal cabinClassSessionBean;

    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBean;

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public FareEntity createFareEntity(FareEntity fare, long flightSchedulePlanID, long cabinClassID) throws FlightSchedulePlanNotFoundException, CabinClassNotFoundException, FareExistException, UnknownPersistenceException {
        CabinClassEntity cabinClass = cabinClassSessionBean.retrieveCabinByID(cabinClassID);
        FlightSchedulePlanEntity flightSchedulePlan = flightSchedulePlanSessionBean.retrieveFlightSchedulePlanEntityById(flightSchedulePlanID);
        try {
            em.persist(fare);
            
            // Bidirection association between fare and flight schedule plan
            if (!flightSchedulePlan.getFares().contains(fare)) {
                flightSchedulePlan.getFares().add(fare);
            }
            fare.setFlightSchedulePlan(flightSchedulePlan);
            
            // Unidirection association between fare and cabin class
            fare.setCabin(cabinClass);
            
            em.flush();
            return fare;
        } catch (PersistenceException ex) { 
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new FareExistException("Fare with same fare code exists");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
           
}
