/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CabinClassNotFoundException;
import exceptions.CustomerNotFoundException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InputDataValidationException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
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
    private FlightSessionBeanLocal flightSessionBean;

    @EJB
    private SeatsInventorySessionBeanLocal seatsInventorySessionBean;
 
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
    
    // only exposed in local (managed instances passed in)
    @Override
    public void deleteSchedule(List<FlightScheduleEntity> flightSchedule) {
       
        for(FlightScheduleEntity sched : flightSchedule) {           
           seatsInventorySessionBean.deleteSeatInventory(sched.getSeatInventory()); 
           em.remove(sched);
       }
    }

    @Override
    public List<FlightScheduleEntity> getFlightSchedules(String departure, String destination, Date date, CabinClassTypeEnum cabin) throws FlightNotFoundException {
        List<FlightScheduleEntity> schedule = new ArrayList<>();
        List<FlightEntity> flight = flightSessionBean.retrieveAllFlightByFlightRoute(departure, destination);

        for (FlightEntity flightEntity: flight) {
            for (FlightSchedulePlanEntity flightSchedulePlanEntity: flightEntity.getFlightSchedulePlan()) {
                for (FlightScheduleEntity flightScheduleEntity: flightSchedulePlanEntity.getFlightSchedule()) {
                    boolean toAdd = false;
                    if (cabin == null) {
                        toAdd = true;
                    } else {
                        for (SeatInventoryEntity seatInventoryEntity: flightScheduleEntity.getSeatInventory()) {
                            if (seatInventoryEntity.getCabin().getCabinClassType().equals(cabin)) {
                                toAdd = true;
                            }
                        }
                    }
                    
                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c1.setTime(flightScheduleEntity.getDepartureDateTime());
                    c2.setTime(date);
                    boolean sameDay = c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) &&
                                      c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);       
                    if (!sameDay){
                        toAdd = false;
                    }
                    if (toAdd) {
                        schedule.add(flightScheduleEntity);
                    }
                }
            } 
        }       
        Collections.sort(schedule, new FlightScheduleEntity.FlightScheduleComparator());
        return schedule;
    }

    @Override
    public List<Pair<FlightScheduleEntity, FlightScheduleEntity>> getIndirectFlightSchedules(String departure, String destination, Date date, CabinClassTypeEnum cabin) throws FlightNotFoundException {
        List<Pair<FlightScheduleEntity, FlightScheduleEntity>> schedule = new ArrayList<>();
        List<FlightEntity[]> flight = flightSessionBean.retrieveAllIndirectFlightByFlightRoute(departure, destination);
        
        for (Object[] pair: flight) {
            FlightEntity firstFlight = (FlightEntity) pair[0];
            FlightEntity secondFlight = (FlightEntity) pair[1];
            for (FlightSchedulePlanEntity flightSchedulePlan: firstFlight.getFlightSchedulePlan()) {
                for (FlightScheduleEntity flightSchedule: flightSchedulePlan.getFlightSchedule()) {
                    for (FlightSchedulePlanEntity flightSchedulePlan2: secondFlight.getFlightSchedulePlan()) {
                        for (FlightScheduleEntity flightSchedule2: flightSchedulePlan2.getFlightSchedule()) {
                            boolean toAdd = false;
                            if (cabin == null) {
                                toAdd = true;
                            } else {
                                for (SeatInventoryEntity seatInventory: flightSchedule.getSeatInventory()) {
                                    for (SeatInventoryEntity seatInventory2: flightSchedule2.getSeatInventory()) {
                                        if (seatInventory.getCabin().getCabinClassType().equals(cabin) && seatInventory2.getCabin().getCabinClassType().equals(cabin)) {
                                        toAdd = true;
                                        }
                                    }                           
                                }
                            }
                            
                            Calendar ca = Calendar.getInstance();
                            Calendar cc = Calendar.getInstance();
                            ca.setTime(flightSchedule.getDepartureDateTime());
                            cc.setTime(date);
                            boolean sameDay = ca.get(Calendar.DAY_OF_YEAR) == cc.get(Calendar.DAY_OF_YEAR) &&
                                              ca.get(Calendar.YEAR) == cc.get(Calendar.YEAR);       
                            if (!sameDay){
                                toAdd = false;
                            }
                            
                            Calendar c = Calendar.getInstance();
                            c.setTime(flightSchedule.getDepartureDateTime());
                            c.add(Calendar.HOUR_OF_DAY, flightSchedule.getDuration());
                            int diff1 = flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                            c.add(Calendar.HOUR_OF_DAY, diff1);
                            
                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(flightSchedule2.getDepartureDateTime());
                            long gap = Duration.between(c.toInstant(), c2.toInstant()).toHours();
                            if (gap < 2l || gap > 12l) { // To check: how far is too far
                                toAdd = false;
                            }
                            
                            if (toAdd) {
                                schedule.add(new Pair(flightSchedule, flightSchedule2));
                            }
                            
                        }
                    }
                }
            }
        }
        Collections.sort(schedule, new FlightScheduleEntity.IndirectFlightScheduleComparator());
        return schedule;
    }

    @Override
    public FareEntity getSmallestFare(FlightScheduleEntity flightSchedule, CabinClassTypeEnum cabinClassType) throws FlightScheduleNotFoundException, CabinClassNotFoundException {
        FlightScheduleEntity flightScheduleEntity = retrieveFlightScheduleById(flightSchedule.getFlightScheduleID());
        List<FareEntity> fares = flightScheduleEntity.getFlightSchedulePlan().getFares();
        List<FareEntity> ccfares = new ArrayList<>();
        for (FareEntity fare: fares) {
            if (fare.getCabinClass().getCabinClassType() == cabinClassType) {
                ccfares.add(fare);
            }
        }
        if (ccfares.isEmpty()) {
            throw new CabinClassNotFoundException("Cabin class not found");
        }
        FareEntity smallest = ccfares.get(0);
        for (FareEntity fare : ccfares) {
            if(fare.getFareAmount().compareTo(smallest.getFareAmount()) < 0) {
                smallest = fare;
            }
        }
        return smallest;
    }

    @Override
    public FareEntity getBiggestFare(FlightScheduleEntity flightScheduleEntity, CabinClassTypeEnum type) throws FlightScheduleNotFoundException, CabinClassNotFoundException {
        FlightScheduleEntity flightSchedule = retrieveFlightScheduleById(flightScheduleEntity.getFlightScheduleID());
        List<FareEntity> fares = flightSchedule.getFlightSchedulePlan().getFares();
        List<FareEntity> ccfares = new ArrayList<>();
        for(FareEntity fare : fares) {
            if(fare.getCabinClass().getCabinClassType() == type) {
                ccfares.add(fare);
            }
        }
        if(ccfares.isEmpty()) {
            throw new CabinClassNotFoundException("Cabin Class not found");
        }
        
        FareEntity biggest = ccfares.get(0);
        for(FareEntity fare : ccfares) {
            if(fare.getFareAmount().compareTo(biggest.getFareAmount()) > 0) {
                biggest = fare;
            }
        }
        return biggest;
    }
   
}
    
    

