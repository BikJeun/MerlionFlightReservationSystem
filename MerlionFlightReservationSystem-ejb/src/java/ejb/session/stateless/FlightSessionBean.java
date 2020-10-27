/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.AircraftConfigNotFoundException;
import exceptions.DeleteFlightException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFlightException;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
    public FlightEntity createNewFlight(FlightEntity flight, Long chosenRoute, Long chosenConfig) throws FlightExistException, UnknownPersistenceException {
        if(flight != null) {
            try {
                em.persist(flight);
                
                FlightRouteEntity flightRoute = flightRouteSessionBean.retreiveFlightRouteById(chosenRoute);
                AircraftConfigurationEntity aircraftConfig = aircraftConfigurationSessionBean.retriveAircraftConfigByID(chosenConfig);
                
                flight.setAircraftConfig(aircraftConfig);
                flight.setFlightRoute(flightRoute);
                
                em.flush();
                em.refresh(flight);
                
            } catch (FlightRouteNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (AircraftConfigNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getClass()
                        .getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new FlightExistException("Flight with " + flight.getFlightNum() + " already exist!");
                }
            }
            
        }
        return flight;
        
    }
    
    @Override
    public FlightEntity retreiveFlightById(Long id) throws FlightNotFoundException {
        FlightEntity flight = em.find(FlightEntity.class, id);
        
        if(flight != null) {
            return flight;
        } else {
            throw new FlightNotFoundException("Flight " + id + " not found!");
        }
    }

    @Override
    public void associateExistingTwoWayFlights(Long flightID, Long returnFlightID) {
        try {
            FlightEntity flight = retreiveFlightById(flightID);
            FlightEntity returnFlight = retreiveFlightById(returnFlightID);
            
            flight.setReturningFlight(returnFlight);
            flight.setSourceFlight(flight);
            
            returnFlight.setSourceFlight(returnFlight);
            returnFlight.setReturningFlight(flight);
        } catch (FlightNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    @Override
    public List<FlightEntity> retrieveAllFlight() {
        Query query = em.createQuery("SELECT f FROM FlightEntity f ORDER BY CAST(SUBSTRING(f.flightNum, 3) INTEGER) ASC");
        List<FlightEntity> result =  query.getResultList();
        int x = result.size()-1;
      while (x >= 0) {    
        FlightEntity flight = result.get(x);
        boolean replaced = false;
        for (int y = x - 2; y >= 0; y--) {
          FlightEntity otherFlight = result.get(y);
           if (flight.getReturningFlight()!= null && flight.getReturningFlight().getFlightID() == otherFlight.getFlightID()) {
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
    public void updateFlight(FlightEntity oldFlight) throws FlightNotFoundException, UpdateFlightException, InputDataValidationException {
        if(oldFlight != null && oldFlight.getFlightID()!= null) {
            Set<ConstraintViolation<FlightEntity>>constraintViolations = validator.validate(oldFlight);
            
            if(constraintViolations.isEmpty()) {
                FlightEntity flightEntityToUpdate = retreiveFlightById(oldFlight.getFlightID());
                
                if(flightEntityToUpdate.getFlightID().equals(oldFlight.getFlightID())) {
                    flightEntityToUpdate.setFlightNum(oldFlight.getFlightNum());
                    flightEntityToUpdate.setFlightRoute(oldFlight.getFlightRoute());
                    flightEntityToUpdate.setAircraftConfig(oldFlight.getAircraftConfig());
                    flightEntityToUpdate.setSourceFlight(oldFlight.getSourceFlight());
                    flightEntityToUpdate.setReturningFlight(oldFlight.getReturningFlight());
                }
                else {
                    throw new UpdateFlightException("ID of flight record to be updated does not match the existing record");
                }
            }
            else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else {
            throw new FlightNotFoundException("Flight ID not provided for flight to be updated");
        }
    }
    
    @Override
    public void deleteFlight(Long flightID) throws FlightNotFoundException, DeleteFlightException {
        FlightEntity flight = retreiveFlightById(flightID);
        
        flight.getFlightRoute().getFlights().remove(flight);
        
        for(FlightSchedulePlanEntity plan: flight.getFlightSchedulePlan()) {
            plan.setFlight(null); //probably is call the local session bean n delete the scheuleplan??...
        }
        
        em.remove(flight);
        
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightEntity>>constraintViolations) {
        
        String msg = "Input data validation error!:";
        
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    
}

    
    
    
    
    
