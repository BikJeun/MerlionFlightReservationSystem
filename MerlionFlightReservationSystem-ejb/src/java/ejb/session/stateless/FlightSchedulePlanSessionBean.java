/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleExistException;
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @EJB
    private FlightSessionBeanLocal flightSessionBean;
    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FlightSchedulePlanSessionBean() {
         validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public FlightSchedulePlanEntity createNewFlightSchedulePlan(FlightSchedulePlanEntity plan, Pair<Date, Integer> pair, int recurrent) throws FlightSchedulePlanExistException, UnknownPersistenceException, InputDataValidationException, FlightScheduleExistException, FlightSchedulePlanNotFoundException {
        Set<ConstraintViolation<FlightSchedulePlanEntity>>constraintViolations = validator.validate(plan);
        
        if(constraintViolations.isEmpty()) {
            try{
                em.persist(plan);
                em.flush();
                
                //Create Flight Schedule
                if(recurrent == 0) {
                    FlightScheduleEntity schedule = new FlightScheduleEntity(pair.getKey(), pair.getValue());
                    schedule = flightScheduleSessionBean.createNewSchedule(plan.getFlightSchedulePlanID(), schedule);
                    plan.getFlightSchedule().add(schedule);
                } else {
                    Date presentDate = pair.getKey();
                    Date endDate = plan.getRecurrentEndDate();
                    
                    while(endDate.compareTo(presentDate) > 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(presentDate);
                        c.add(Calendar.DAY_OF_MONTH, recurrent);
                        
                        presentDate = c.getTime();
                        Pair<Date, Integer> updatedPair = new Pair<>(presentDate, pair.getValue());
                        FlightScheduleEntity schedule = new FlightScheduleEntity(pair.getKey(), pair.getValue());
                        schedule = flightScheduleSessionBean.createNewSchedule(plan.getFlightSchedulePlanID(), schedule);
                        plan.getFlightSchedule().add(schedule);
                    }
                }
                
                em.refresh(plan);
                return plan;
            }catch(PersistenceException ex) {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new FlightSchedulePlanExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            } catch (FlightSchedulePlanNotFoundException ex) {
                throw new FlightSchedulePlanNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public FlightSchedulePlanEntity createNewFlightSchedulePlanMultiple(FlightSchedulePlanEntity plan, List<Pair<Date, Integer>> info) throws FlightSchedulePlanExistException, UnknownPersistenceException, InputDataValidationException, FlightScheduleExistException, FlightSchedulePlanNotFoundException {
        Set<ConstraintViolation<FlightSchedulePlanEntity>>constraintViolations = validator.validate(plan);
        
        if(constraintViolations.isEmpty()) {
            try {
                em.persist(plan);
                em.flush();
                
                int size = info.size();
                for(int i = 0; i<size; i++) {
                    FlightScheduleEntity schedule = new FlightScheduleEntity(info.get(i).getKey(), info.get(i).getValue());
                    schedule = flightScheduleSessionBean.createNewSchedule(plan.getFlightSchedulePlanID(), schedule);
                    plan.getFlightSchedule().add(schedule);
                }
                em.refresh(plan);
                return plan;
            } catch(PersistenceException ex) {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new FlightSchedulePlanExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (FlightScheduleExistException ex) {
                throw new FlightScheduleExistException(ex.getMessage());
            } catch (FlightSchedulePlanNotFoundException ex) {
                throw new FlightSchedulePlanNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public FlightSchedulePlanEntity retrieveFlightSchedulePlanEntityById(Long flightSchedulePlanID) throws FlightSchedulePlanNotFoundException {
        FlightSchedulePlanEntity plan = em.find(FlightSchedulePlanEntity.class, flightSchedulePlanID);
        
        if(plan != null) {
            return plan;
        } else {
            throw new FlightSchedulePlanNotFoundException("Flight Schedule Plan ID " + flightSchedulePlanID.toString() + " does not exist!" );
        }
    }
    
    @Override
    public void associateFlightToPlan(Long flightID, Long flightSchedulePlanID) throws FlightNotFoundException, FlightSchedulePlanNotFoundException{
        try {
            FlightEntity flight = flightSessionBean.retreiveFlightById(flightID);
            FlightSchedulePlanEntity plan = retrieveFlightSchedulePlanEntityById(flightSchedulePlanID);
            
            if(flight != null && plan != null) {
                flight.getFlightSchedulePlan().add(plan);
                plan.setFlight(flight);
            }
        } catch (FlightNotFoundException ex) {
            throw new FlightNotFoundException(ex.getMessage());
        } catch (FlightSchedulePlanNotFoundException ex) {
            throw new FlightSchedulePlanNotFoundException(ex.getMessage());
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightSchedulePlanEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    

    

    
}
    
    

    


    
    

    

