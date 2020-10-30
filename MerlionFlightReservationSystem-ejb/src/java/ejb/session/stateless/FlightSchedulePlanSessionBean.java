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
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javafx.util.Pair;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
    @Resource
    private EJBContext eJBContext;
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FlightSchedulePlanSessionBean() {
         validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public FlightSchedulePlanEntity createNewFlightSchedulePlan(FlightSchedulePlanEntity plan, long flightID, Pair<Date, Integer> pair, int recurrent) throws InputDataValidationException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException {
        Set<ConstraintViolation<FlightSchedulePlanEntity>>constraintViolations = validator.validate(plan);
        
        if (constraintViolations.isEmpty()) {
            try{
                em.persist(plan);
                
                //Create Flight Schedule
                if(recurrent == 0) {
                    FlightScheduleEntity schedule = new FlightScheduleEntity(pair.getKey(), pair.getValue());
                    flightScheduleSessionBean.createNewSchedule(plan, schedule);
                    //plan.getFlightSchedule().add(schedule); //association done on schedule bean
                } else {
                    Date presentDate = pair.getKey();
                    Date endDate = plan.getRecurrentEndDate();
                    
                    while(endDate.compareTo(presentDate) > 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(presentDate);
                        FlightScheduleEntity schedule = new FlightScheduleEntity(c.getTime(), pair.getValue());
                        flightScheduleSessionBean.createNewSchedule(plan, schedule);
                        c.add(Calendar.DAY_OF_MONTH, recurrent);         
                        presentDate = c.getTime();                 
                        //plan.getFlightSchedule().add(schedule); //association done on schedule bean
                    }
                }
                
                associateFlightToPlan(flightID, plan);
                em.flush();
                return plan;
            } catch(PersistenceException ex) {
                eJBContext.setRollbackOnly();
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new FlightSchedulePlanExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (InputDataValidationException ex) {
                eJBContext.setRollbackOnly();
                throw new InputDataValidationException(ex.getMessage());
            } catch (FlightNotFoundException ex) {
                eJBContext.setRollbackOnly();
                throw new FlightNotFoundException(ex.getMessage());
            } catch (FlightSchedulePlanExistException ex) {
                eJBContext.setRollbackOnly();
                throw new FlightSchedulePlanExistException(ex.getMessage());
            } 
        } else {
            eJBContext.setRollbackOnly();
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public FlightSchedulePlanEntity createNewFlightSchedulePlanMultiple(FlightSchedulePlanEntity plan, long flightID, List<Pair<Date, Integer>> info) throws InputDataValidationException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException {
        Set<ConstraintViolation<FlightSchedulePlanEntity>>constraintViolations = validator.validate(plan);
        
        if(constraintViolations.isEmpty()) {
            try {
                em.persist(plan);
                
                int size = info.size();
                for(int i = 0; i < size; i++) {
                    FlightScheduleEntity schedule = new FlightScheduleEntity(info.get(i).getKey(), info.get(i).getValue());
                    flightScheduleSessionBean.createNewSchedule(plan, schedule);
                    //plan.getFlightSchedule().add(schedule); //association done on schedule session bean
                }
                
                associateFlightToPlan(flightID, plan);
                em.flush();
                return plan;
            } catch(PersistenceException ex) {
                eJBContext.setRollbackOnly();
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new FlightSchedulePlanExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (InputDataValidationException ex) {
                eJBContext.setRollbackOnly();
                throw new InputDataValidationException(ex.getMessage());
            } catch (FlightNotFoundException ex) {
                eJBContext.setRollbackOnly();
                throw new FlightNotFoundException(ex.getMessage());
            } catch (FlightSchedulePlanExistException ex) {
                eJBContext.setRollbackOnly();
                throw new FlightSchedulePlanExistException(ex.getMessage());
            } 
        } else {
            eJBContext.setRollbackOnly();
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
    
    // Not in use anymore
    @Override
    public void associateFlightToPlan(Long flightID, Long flightSchedulePlanID) throws FlightNotFoundException, FlightSchedulePlanNotFoundException, FlightSchedulePlanExistException {

        FlightEntity flight = flightSessionBean.retreiveFlightById(flightID);
        FlightSchedulePlanEntity plan = retrieveFlightSchedulePlanEntityById(flightSchedulePlanID);
        
        

        for (FlightSchedulePlanEntity fsp: flight.getFlightSchedulePlan()) {
            for (FlightScheduleEntity fs: fsp.getFlightSchedule()) {
                Date start1 = fs.getDepartureDateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start1);
                calendar.add(Calendar.HOUR_OF_DAY, fs.getDuration());
                Date end1 = calendar.getTime();
                for (FlightScheduleEntity fs2: plan.getFlightSchedule()) {
                    Date start2 = fs2.getDepartureDateTime();
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(start2);
                    calendar2.add(Calendar.HOUR_OF_DAY, fs2.getDuration());
                    Date end2 = calendar2.getTime();
                    
                    if (isOverlapping(start1, end1, start2, end2)) {
                        throw new FlightSchedulePlanExistException("Flight schedule overlaps with existing flight schedules");
                    }
                }
            }
        }
        flight.getFlightSchedulePlan().add(plan);
        plan.setFlight(flight);

    }
    
    // flightscheduleplan passed in should be managed
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void associateFlightToPlan(Long flightID, FlightSchedulePlanEntity flightSchedulePlan) throws FlightNotFoundException, FlightSchedulePlanExistException {

        FlightEntity flight = flightSessionBean.retreiveFlightById(flightID);  
        
        // Check no overlaps with already existing flight plans associated with the flight
        for (FlightSchedulePlanEntity fsp: flight.getFlightSchedulePlan()) {
            for (FlightScheduleEntity fs: fsp.getFlightSchedule()) {
                Date start1 = fs.getDepartureDateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start1);
                calendar.add(Calendar.HOUR_OF_DAY, fs.getDuration());
                Date end1 = calendar.getTime();
                for (FlightScheduleEntity fs2: flightSchedulePlan.getFlightSchedule()) {
                    Date start2 = fs2.getDepartureDateTime();
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(start2);
                    calendar2.add(Calendar.HOUR_OF_DAY, fs2.getDuration());
                    Date end2 = calendar2.getTime();
                    
                    if (isOverlapping(start1, end1, start2, end2)) {
                        System.out.println("calling one");
                        throw new FlightSchedulePlanExistException("Flight schedule overlaps with existing flight schedules");
                    }
                }
            }
        }
        
        // Check the flight schedule plan does not have overlaps amongst itself also
        List<FlightScheduleEntity> fs = flightSchedulePlan.getFlightSchedule();
        System.out.println("check: " + fs.size());
        for (int i = 0; i < fs.size(); i++) {
            Date start1 = fs.get(i).getDepartureDateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start1);
            calendar.add(Calendar.HOUR_OF_DAY, fs.get(i).getDuration());
            Date end1 = calendar.getTime();
            for (int j = i+1; j < fs.size(); j++) {
                Date start2 = fs.get(j).getDepartureDateTime();
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(start2);
                calendar2.add(Calendar.HOUR_OF_DAY, fs.get(j).getDuration());
                Date end2 = calendar2.getTime();
                 
                if (isOverlapping(start1, end1, start2, end2)) {
                    System.out.println("calling two");
                    throw new FlightSchedulePlanExistException("Flight schedule overlaps");
                 }
            }
        }
        
        flight.getFlightSchedulePlan().add(flightSchedulePlan);
        flightSchedulePlan.setFlight(flight);

    }
    
    private boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && start2.before(end1);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightSchedulePlanEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    

    

    
}
    
    

    


    
    

    

