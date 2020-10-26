/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import entity.FlightRouteEntity;
import exceptions.AirportNotFoundException;
import exceptions.FlightRouteExistException;
import exceptions.FlightRouteNotFoundException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class FlightRouteSessionBean implements FlightRouteSessionBeanRemote, FlightRouteSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    public FlightRouteSessionBean() {
    }
    
    @Override
    public FlightRouteEntity createNewFlightRoute(FlightRouteEntity flightRoute, long originAirportID, long destinationAirportID) throws FlightRouteExistException, UnknownPersistenceException, AirportNotFoundException {
        try {
            
            AirportEntity originAirport = em.find(AirportEntity.class, originAirportID);
            AirportEntity destinationAirport = em.find(AirportEntity.class, destinationAirportID);
            
            if (originAirport == null || destinationAirport == null) {
                throw new AirportNotFoundException("Airport does not exist!");
            }
            
            em.persist(flightRoute); //unidirectional, so there is no need to associate on airport side
            
            flightRoute.setOrigin(originAirport); // QN: neccesary to set with managed instances? should i set then persist or persist then set?
            flightRoute.setDestination(destinationAirport);
            
            em.flush();
            return flightRoute;
        } catch (PersistenceException ex) {                     
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {             
                        throw new FlightRouteExistException("This flight route already exists!"); 
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public FlightRouteEntity searchForFlightRouteByOriginAndDestination(String originAirportIATA, String destinationAirportIATA) throws FlightRouteNotFoundException {
        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f WHERE f.origin.IATACode = :codeOne AND f.destination.IATACode = :codeTwo AND f.disabled=false");
        query.setParameter("codeOne", originAirportIATA);
        query.setParameter("codeTwo", destinationAirportIATA);
        try{
            return (FlightRouteEntity)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new FlightRouteNotFoundException("Flight Route does not exist in system!");
        }
    }
    
    
    @Override
    public long setComplementaryFlightRoute(long routeID) throws FlightRouteNotFoundException {
        FlightRouteEntity route = em.find(FlightRouteEntity.class, routeID);
        if (route == null || route.isDisabled() == true) {
            throw new FlightRouteNotFoundException("Flight Route does not exist in system!");
        }
        FlightRouteEntity other =  searchForFlightRouteByOriginAndDestination(route.getDestination().getIATACode(), route.getOrigin().getIATACode());
        route.setSourceRoute(other);
        route.setComplementaryRoute(other);
        other.setComplementaryRoute(route);
        other.setSourceRoute(route);
        return other.getFlightRouteID();
    }

    @Override
    public List<FlightRouteEntity> retrieveAllFlightRouteInOrder() {
        Query query = em.createQuery("SELECT DISTINCT f FROM FlightRouteEntity f WHERE f.disabled=false ORDER BY f.origin.airportName ASC"); //TODO : order complementary routes together
        return query.getResultList();
    }
    
    @Override
    public void removeFlightRoute(long flightRouteID) throws FlightRouteNotFoundException {
        FlightRouteEntity route = em.find(FlightRouteEntity.class, flightRouteID);
        if (route == null || route.isDisabled() == true) {
            throw new FlightRouteNotFoundException("Flight Route does not exist in system!");
        }
        if (route.getFlights().isEmpty()) {
            
            // Disassociation of FK before removal
            if (route.getSourceRoute() != null) {
                route.getSourceRoute().setComplementaryRoute(null);
                route.getSourceRoute().setSourceRoute(null);
            }
            route.setComplementaryRoute(null);
            route.setSourceRoute(null);
            
            em.remove(route);
        } else {
            route.setDisabled(true);
        } 
    }
    
    @Override
    public FlightRouteEntity enableFlightRoute(long originAirportID, long destinationAirportID) throws FlightRouteNotFoundException {
        Query query = em.createQuery("SELECT f FROM FlightRouteEntity f WHERE f.origin.airportID = :codeOne AND f.destination.airportID = :codeTwo AND f.disabled=true");
        query.setParameter("codeOne", originAirportID);
        query.setParameter("codeTwo", destinationAirportID);
        try{
            FlightRouteEntity flight = (FlightRouteEntity) query.getSingleResult();
            flight.setDisabled(false);
            em.flush();
            return flight;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new FlightRouteNotFoundException("Disabled Flight Route does not exist in system!");
        }
    }
    
}
