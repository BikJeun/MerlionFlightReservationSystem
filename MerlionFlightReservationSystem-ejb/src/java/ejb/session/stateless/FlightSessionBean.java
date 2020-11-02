/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import exceptions.AircraftConfigNotFoundException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFlightException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class FlightSessionBean implements FlightSessionBeanRemote, FlightSessionBeanLocal {
    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;
    
    @EJB
    private AircraftConfigurationSessionBeanLocal aircraftConfigurationSessionBean;
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public FlightSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public FlightEntity createNewFlight(FlightEntity flight, Long chosenRoute, Long chosenConfig) throws FlightExistException, UnknownPersistenceException, FlightRouteNotFoundException, AircraftConfigNotFoundException {
        if(flight != null) {
            
            FlightRouteEntity flightRoute = flightRouteSessionBean.retreiveFlightRouteById(chosenRoute);               
            AircraftConfigurationEntity aircraftConfig = aircraftConfigurationSessionBean.retriveAircraftConfigByID(chosenConfig);
    
            try {
                em.persist(flight);
                flight.setAircraftConfig(aircraftConfig);
                flight.setFlightRoute(flightRoute);
                
                em.flush();

            } catch (PersistenceException ex) {              
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                         throw new FlightExistException("Flight with " + flight.getFlightNum() + " already exist");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }                          
            }
            
        }
        return flight;
        
    }
    
    @Override
    public FlightEntity enableFlight(String flightNumber, long routeID, long configID) throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f FROM FlightEntity f WHERE f.flightNum = :num AND f.disabled=true AND f.flightRoute.flightRouteID = :route AND f.aircraftConfig.aircraftConfigID = :config");
        query.setParameter("num", flightNumber);
        query.setParameter("route", routeID);
        query.setParameter("config", configID);
        try {
            FlightEntity flight = (FlightEntity) query.getSingleResult();
            flight.setDisabled(false);
            em.flush();
            return flight;
        } catch (NoResultException |  NonUniqueResultException ex) {
            throw new FlightNotFoundException("Disabled flight deos not exist in system");
        }
    }
    
    @Override
    public FlightEntity retreiveFlightById(Long id) throws FlightNotFoundException {
        FlightEntity flight = em.find(FlightEntity.class, id);
        
        if(flight != null && flight.isDisabled() == false) {
            return flight;
        } else {
            throw new FlightNotFoundException("Flight " + id + " not found!");
        }
    }
    
    @Override
    public FlightEntity retrieveFlightByFlightNumber(String flightNum) throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f FROM FlightEntity f WHERE f.flightNum = :num AND f.disabled=false");
        query.setParameter("num", flightNum);
        try {
            return (FlightEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new FlightNotFoundException("Flight does not exist in system");
        }
    }

    @Override
    public void associateExistingFlightWithReturnFlight(Long sourceFlightID, Long returnFlightID) throws FlightNotFoundException {
        FlightEntity sourceFlight = retreiveFlightById(sourceFlightID);
        FlightEntity returnFlight = retreiveFlightById(returnFlightID);

        // Bidirection association
        sourceFlight.setReturningFlight(returnFlight);
        returnFlight.setSourceFlight(sourceFlight);
    }
    
    @Override
    public List<FlightEntity> retrieveAllFlight() throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f FROM FlightEntity f WHERE f.disabled=false ORDER BY SUBSTRING(f.flightNum, 3) ASC");
        List<FlightEntity> result =  query.getResultList();
        if (result.isEmpty()) {
            throw new FlightNotFoundException("No flights found in system");
        }
        int x = result.size()-1;
        while (x >= 0) {          
            FlightEntity flight = result.get(x);
            boolean replaced = false;
            for (int y = x - 2; y >= 0; y--) {           
                FlightEntity otherFlight = result.get(y);                             
                if (otherFlight.getReturningFlight()!= null && otherFlight.getReturningFlight().getFlightID() == flight.getFlightID()) {
                    result.remove(x);
                    result.add(y + 1, flight);
                    replaced = true;
                    break;                                      
                }
            }
            if (replaced) {continue;}
            x--;        
        }     
        return result;
    }
    
    @Override
     public List<FlightEntity> retrieveAllFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException {
        Query query = em.createQuery("SELECT f FROM FlightEntity f WHERE f.disabled=false AND f.flightRoute.origin.IATACode = :origin AND f.flightRoute.destination.IATACode = :dest ORDER BY SUBSTRING(f.flightNum, 3) ASC");
        query.setParameter("origin", originIATACode);
        query.setParameter("dest", destinationIATACode);
        List<FlightEntity> result =  query.getResultList();
        if (result.isEmpty()) {
            throw new FlightNotFoundException("No flights with flight route from " + originIATACode + " to " +  destinationIATACode + " found in system");
        }
        return result;
    }
    
    
    @Override
    public void updateFlight(FlightEntity oldFlight) throws FlightNotFoundException, UpdateFlightException, InputDataValidationException, FlightRouteNotFoundException, UnknownPersistenceException, AircraftConfigNotFoundException {

        Set<ConstraintViolation<FlightEntity>>constraintViolations = validator.validate(oldFlight);
        if (constraintViolations.isEmpty()) {
            try {
                FlightEntity flightEntityToUpdate = retreiveFlightById(oldFlight.getFlightID());
                
                if (!flightEntityToUpdate.getFlightNum().equals(oldFlight.getFlightNum())) {
                    throw new FlightNotFoundException("Flight not found");
                }
                
                // Flight Number (edit: not allowing update of flightnumber)
                // flightEntityToUpdate.setFlightNum(oldFlight.getFlightNum());
                
                // Flight Route
                if (!Objects.equals(flightEntityToUpdate.getFlightRoute().getFlightRouteID(), oldFlight.getFlightRoute().getFlightRouteID())) {
                    flightRouteSessionBean.retreiveFlightRouteById(flightEntityToUpdate.getFlightRoute().getFlightRouteID()).getFlights().remove(flightEntityToUpdate);
                    flightRouteSessionBean.retreiveFlightRouteById(oldFlight.getFlightRoute().getFlightRouteID()).getFlights().add(flightEntityToUpdate);
                    flightEntityToUpdate.setFlightRoute(flightRouteSessionBean.retreiveFlightRouteById(oldFlight.getFlightRoute().getFlightRouteID()));
                }
                
                // Aircraft Config
                if (!Objects.equals(flightEntityToUpdate.getAircraftConfig().getAircraftConfigID(), oldFlight.getAircraftConfig().getAircraftConfigID())) {
                    flightEntityToUpdate.setAircraftConfig(aircraftConfigurationSessionBean.retriveAircraftConfigByID(oldFlight.getAircraftConfig().getAircraftConfigID()));
                }
                
                // Source Flight
                if (flightEntityToUpdate.getSourceFlight() != null) {
                    if (oldFlight.getSourceFlight() != null && !Objects.equals(flightEntityToUpdate.getSourceFlight().getFlightID(), oldFlight.getSourceFlight().getFlightID())) {
                        // Dissassociation of old
                        flightEntityToUpdate.getSourceFlight().setReturningFlight(null);
                        flightEntityToUpdate.setSourceFlight(null);
                        
                        // Association of new
                        FlightEntity newSource = retreiveFlightById(oldFlight.getSourceFlight().getFlightID());
                        newSource.setReturningFlight(flightEntityToUpdate);
                        flightEntityToUpdate.setSourceFlight(newSource);
                    } else if (oldFlight.getSourceFlight() == null) {
                        // Dissassociation of old
                        flightEntityToUpdate.getSourceFlight().setReturningFlight(null);
                        flightEntityToUpdate.setSourceFlight(null);
                    }                   
                } else {
                    if (oldFlight.getSourceFlight() != null) {
                        // Association of new
                        FlightEntity newSource = retreiveFlightById(oldFlight.getSourceFlight().getFlightID());
                        newSource.setReturningFlight(flightEntityToUpdate);
                        flightEntityToUpdate.setSourceFlight(newSource);
                    }
                }
                
                // Returning flight
                if (flightEntityToUpdate.getReturningFlight() != null) {
                    if (oldFlight.getReturningFlight() != null && !Objects.equals(flightEntityToUpdate.getReturningFlight().getFlightID(), oldFlight.getReturningFlight().getFlightID())) {
                        // Dissassociation of old
                        flightEntityToUpdate.getReturningFlight().setSourceFlight(null);
                        flightEntityToUpdate.setReturningFlight(null);
                        
                        // Association of new
                        FlightEntity newReturning = retreiveFlightById(oldFlight.getReturningFlight().getFlightID());
                        newReturning.setSourceFlight(flightEntityToUpdate);
                        flightEntityToUpdate.setReturningFlight(newReturning);
                    } else if (oldFlight.getReturningFlight() == null) {
                        // Dissassociation of old
                        flightEntityToUpdate.getReturningFlight().setSourceFlight(null);
                        flightEntityToUpdate.setReturningFlight(null);
                    }                   
                } else {
                    if (oldFlight.getReturningFlight() != null) {
                        // Association of new
                        FlightEntity newReturning = retreiveFlightById(oldFlight.getReturningFlight().getFlightID());
                        newReturning.setSourceFlight(flightEntityToUpdate);
                        flightEntityToUpdate.setReturningFlight(newReturning);
                    }
                }
                
                em.flush();
            } catch (PersistenceException ex) {              
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                         throw new UpdateFlightException("Flight with " + oldFlight.getFlightNum() + " already exist");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }                          
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public void deleteFlight(Long flightID) throws FlightNotFoundException {
        FlightEntity flight = retreiveFlightById(flightID);
        if (flight.getFlightSchedulePlan().isEmpty()) {
            
            // Disassociation of FK before removal            
            flight.getFlightRoute().getFlights().remove(flight);
            if (flight.getReturningFlight() != null) {
                flight.getReturningFlight().setSourceFlight(null);
            }
            flight.setReturningFlight(null);

            if (flight.getSourceFlight() != null) {
                flight.getSourceFlight().setReturningFlight(null);
            }
            flight.setSourceFlight(null);

            em.remove(flight);
        } else {
            flight.setDisabled(true);
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightEntity>>constraintViolations) {
        
        String msg = "Input data validation error!:";
        
        for (ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    
}

    
    
    
    
    
