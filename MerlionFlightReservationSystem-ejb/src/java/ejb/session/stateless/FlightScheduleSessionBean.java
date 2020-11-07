/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CustomerNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InputDataValidationException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Stateless
public class FlightScheduleSessionBean implements FlightScheduleSessionBeanRemote, FlightScheduleSessionBeanLocal {

    @EJB
    private SeatsInventorySessionBeanLocal seatsInventorySessionBean;

    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBean;
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public FlightScheduleSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // only exposed in local interface => FlightSchedulePlan passed in is managed
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public FlightScheduleEntity createNewSchedule(FlightSchedulePlanEntity flightSchedulePlan, FlightScheduleEntity schedule) throws InputDataValidationException {
         Set<ConstraintViolation<FlightScheduleEntity>> constraintViolations = validator.validate(schedule);
         if(constraintViolations.isEmpty()) {
                em.persist(schedule);
                
                schedule.setFlightSchedulePlan(flightSchedulePlan);
                if (!flightSchedulePlan.getFlightSchedule().contains(schedule)) {
                    flightSchedulePlan.getFlightSchedule().add(schedule);
                }

                return schedule;
                
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightScheduleEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    } 
    
    @Override
    public FlightScheduleEntity retrieveFlightScheduleById(Long flightScheduleID) throws FlightScheduleNotFoundException {
        FlightScheduleEntity schedule = em.find(FlightScheduleEntity.class, flightScheduleID);
        
        if(schedule != null) {
            return schedule;
        } else {
            throw new FlightScheduleNotFoundException("Flight Schedule " + flightScheduleID + " not found!");
        
        }
    }

    /* wat is this for ah
    @Override
    public FlightScheduleEntity retrieveEarliestDepartureSchedule(List<FlightScheduleEntity> list) throws FlightScheduleNotFoundException {
        FlightScheduleEntity result = null;
        for(int i = 0; i<list.size(); i++) {
            try {
                FlightScheduleEntity test = retrieveFlightScheduleById(list.get(i).getFlightScheduleID());
                if(result == null || result.getDepartureDateTime().compareTo(test.getDepartureDateTime()) > 0) {
                    result = test;
                }
            } catch (FlightScheduleNotFoundException ex) {
                throw new FlightScheduleNotFoundException(ex.getMessage());
            }
        }
        return result;
    } */

    // only exposed in local (managed instances passed in)
    @Override
    public void deleteSchedule(List<FlightScheduleEntity> flightSchedule) {
       
        for(FlightScheduleEntity sched : flightSchedule) {           
           seatsInventorySessionBean.deleteSeatInventory(sched.getSeatInventory()); 
           em.remove(sched);
       }
    }

    @Override
    public List<FlightScheduleEntity> retrieveFlightScheduleByDate(Long flightSchedule, Date departureDate) throws FlightScheduleNotFoundException {
        Query query = em.createQuery("SELECT s FROM FlightScheduleEntity s WHERE S.flightSchedulePlan.flightSchedulePlanID = :schedID AND s.departureDateTime = :date");
        query.setParameter("schedID", flightSchedule);
        query.setParameter("date", departureDate);
        
        try {
            return query.getResultList();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new FlightScheduleNotFoundException("Flight Schedule does not exist!");
        }
        
        
    }

    @Override
    public boolean checkIfHaveCabin(Long schedID, CabinClassTypeEnum cabin) {
        try {
            FlightScheduleEntity sched = retrieveFlightScheduleById(schedID);
            List<SeatInventoryEntity> seats = sched.getSeatInventory();
            for(SeatInventoryEntity seat : seats) {
                if(seat.getCabin().getCabinClassType().equals(cabin)) {
                    return true;  
                }
            }
            
        } catch (FlightScheduleNotFoundException ex) {
            System.out.println("No Flight Schedule Found!");
        }
        return false;
    }

   
}
    
    

