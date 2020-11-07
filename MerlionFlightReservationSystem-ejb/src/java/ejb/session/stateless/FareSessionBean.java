/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.FareEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.CabinClassNotFoundException;
import exceptions.FareExistException;
import exceptions.FareNotFoundException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

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
    
    // Not used for now (maybe later on)
    private FareEntity retrieveFareById(Long fareID) throws FareNotFoundException {
        FareEntity fare = em.find(FareEntity.class, fareID);
        if(fare != null) {
            return fare;
        } else {
            throw new FareNotFoundException("Fare " + fareID + " not found!");
        }
    }
    
    // only exposed in local interface, managed instances passed in
    @Override
    public void deleteFares(List<FareEntity> fares) {
        for(FareEntity fare : fares) {
            em.remove(fare);
        }
    }

    /*@Override
    public FareEntity retrieveFareByCabinAndFsp(Long cabinClassID, Long flightScheduleID) {
    Query query = em.createQuery("SELECT f FROM FareEntity f WHERE f.cabinClass.cabinClassID = :cabin AND f.flightSchedulePlan.flightSchedule.FlightScheduleID = :sched");
    query.setParameter("cabin", cabinClassID);
    query.setParameter("sched", flightScheduleID);
    
    List<FareEntity> fares = query.getResultList();
    FareEntity smallest = fares.get(0);
    for(int i = 1; i < fares.size(); i++) {
    if(fares.get(i).getFareAmount().compareTo(smallest.getFareAmount())<0) {
    smallest = fares.get(i);
    }
    }
    return smallest;
    
    
    }*/
}


