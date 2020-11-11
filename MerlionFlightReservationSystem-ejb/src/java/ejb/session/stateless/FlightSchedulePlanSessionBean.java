/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatInventoryEntity;
import exceptions.FareExistException;
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
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal {

    @EJB
    private SeatsInventorySessionBeanLocal seatsInventorySessionBean;
    @EJB
    private FareSessionBeanLocal fareSessionBean;
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
    public FlightSchedulePlanEntity createNewFlightSchedulePlan(FlightSchedulePlanEntity plan, List<FareEntity> fares, long flightID, Pair<Date, Double> pair, int recurrent) throws InputDataValidationException, FareExistException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException {
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
                //System.out.print("test" + plan.getFlightSchedule().size());
                 
                for (FlightScheduleEntity fse: plan.getFlightSchedule()) {               
                    for (CabinClassEntity cc: plan.getFlight().getAircraftConfig().getCabin()) {                    
                        SeatInventoryEntity seats = new SeatInventoryEntity(cc.getMaxSeatCapacity(), 0 , cc.getMaxSeatCapacity());                       
                        seatsInventorySessionBean.createSeatInventory(seats, fse, cc);
                    }
                }
                
                for (FareEntity fare: fares) {
                    fareSessionBean.createFareEntity(fare, plan);
                }
                
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
            } catch (FareExistException ex) {
                eJBContext.setRollbackOnly();
                throw new FareExistException(ex.getMessage());
            } 
        } else {
            eJBContext.setRollbackOnly();
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public FlightSchedulePlanEntity createNewFlightSchedulePlanMultiple(FlightSchedulePlanEntity plan, List<FareEntity> fares, long flightID, List<Pair<Date, Double>> info) throws InputDataValidationException, FareExistException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException {
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
                
                for (FlightScheduleEntity fse: plan.getFlightSchedule()) {               
                    for (CabinClassEntity cc: plan.getFlight().getAircraftConfig().getCabin()) {                    
                        SeatInventoryEntity seats = new SeatInventoryEntity(cc.getMaxSeatCapacity(), 0 , cc.getMaxSeatCapacity());                       
                        seatsInventorySessionBean.createSeatInventory(seats, fse, cc);
                    }
                }
                
                for (FareEntity fare: fares) {
                    fareSessionBean.createFareEntity(fare, plan);
                }
                            
                em.flush();
                return plan;
            } catch (PersistenceException ex) {
                eJBContext.setRollbackOnly();
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
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
            } catch (FareExistException ex) {
                eJBContext.setRollbackOnly();
                throw new FareExistException(ex.getMessage());
            } 
        } else {
            eJBContext.setRollbackOnly();
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public FlightSchedulePlanEntity retrieveFlightSchedulePlanEntityById(Long flightSchedulePlanID) throws FlightSchedulePlanNotFoundException {
        FlightSchedulePlanEntity plan = em.find(FlightSchedulePlanEntity.class, flightSchedulePlanID);
        
        if (plan != null && plan.isDisabled() == false) {
            return plan;
        } else {
            throw new FlightSchedulePlanNotFoundException("Flight Schedule Plan ID " + flightSchedulePlanID.toString() + " does not exist!" );
        }
    }
    
    // flightscheduleplan passed in is managed
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void associateFlightToPlan(Long flightID, FlightSchedulePlanEntity flightSchedulePlan) throws FlightNotFoundException, FlightSchedulePlanExistException {

        FlightEntity flight = flightSessionBean.retreiveFlightById(flightID);  
        
        // Check no overlaps with already existing flight plans associated with the flight
        for (FlightSchedulePlanEntity fsp: flight.getFlightSchedulePlan()) {
            for (FlightScheduleEntity fs: fsp.getFlightSchedule()) {
                Date start1 = fs.getDepartureDateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start1);
                double duration = fs.getDuration();
                int hour = (int) duration;
                int min = (int) (duration % 1 * 60);
                calendar.add(Calendar.HOUR_OF_DAY, hour);
                calendar.add(Calendar.MINUTE, min);
                Date end1 = calendar.getTime();
                for (FlightScheduleEntity fs2: flightSchedulePlan.getFlightSchedule()) {
                    Date start2 = fs2.getDepartureDateTime();
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(start2);
                    double duration2 = fs2.getDuration();
                    int hour2 = (int) duration2;
                    int min2 = (int) (duration2 % 1 * 60);
                    calendar2.add(Calendar.HOUR_OF_DAY, hour2);
                    calendar2.add(Calendar.MINUTE, min2);
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
            double duration = fs.get(i).getDuration();
            int hour = (int) duration;
            int min = (int) (duration % 1 * 60);
            calendar.add(Calendar.HOUR_OF_DAY, hour);
            calendar.add(Calendar.MINUTE, min);
            Date end1 = calendar.getTime();
            for (int j = i+1; j < fs.size(); j++) {
                Date start2 = fs.get(j).getDepartureDateTime();
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(start2);
                double duration2 = fs.get(j).getDuration();
                int hour2 = (int) duration2;
                int min2 = (int) (duration2 % 1 * 60);
                calendar2.add(Calendar.HOUR_OF_DAY, hour2);
                calendar2.add(Calendar.MINUTE, min2);
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
            
        for (ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    @Override
    public List<FlightSchedulePlanEntity> retrieveAllFlightSchedulePlan() throws FlightSchedulePlanNotFoundException {
        Query query = em.createQuery("SELECT DISTINCT p FROM FlightScheduleEntity f, FlightSchedulePlanEntity p WHERE f.flightSchedulePlan.flightSchedulePlanID = p.flightSchedulePlanID AND p.disabled = false ORDER BY p.flightNum ASC, f.departureDateTime DESC");  
        List<FlightSchedulePlanEntity> result = query.getResultList();
        if (result.isEmpty()) {
            throw new FlightSchedulePlanNotFoundException("No flight schedule plans in system");
        }
        int x = result.size()-1;
        
        //sort complementary
        while (x >= 0) {
            FlightSchedulePlanEntity plan = result.get(x);
            boolean replaced = false;
            for (int y = x - 2; y >= 0; y--) {
                FlightSchedulePlanEntity otherplan = result.get(y);
                if (otherplan.getComplementary()!= null && otherplan.getComplementary().getFlightSchedulePlanID() == plan.getFlightSchedulePlanID()) {
                    result.remove(x);
                    result.add(y + 1, plan);
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
    public void associateExistingPlanToComplementaryPlan(Long sourcFlightSchedulePlanID, Long returnFlightSchedulePlanID) throws FlightSchedulePlanNotFoundException {
        FlightSchedulePlanEntity source = retrieveFlightSchedulePlanEntityById(sourcFlightSchedulePlanID);
        FlightSchedulePlanEntity complementary = retrieveFlightSchedulePlanEntityById(returnFlightSchedulePlanID);
        
        // Bidirectional association
        source.setComplementary(complementary);
        complementary.setSource(source);
        
    }
    
    @Override
    public void deleteFlightSchedulePlan(Long flightSchedulePlanID) throws FlightSchedulePlanNotFoundException {
        FlightSchedulePlanEntity plan = retrieveFlightSchedulePlanEntityById(flightSchedulePlanID);
                    
        if(plan.getFlightSchedule().stream().allMatch(sched -> sched.getReservations().isEmpty())) {
            
            flightScheduleSessionBean.deleteSchedule(plan.getFlightSchedule());
            
            plan.getFlight().getFlightSchedulePlan().remove(plan);
            
            fareSessionBean.deleteFares(plan.getFares());
            
            if (plan.getSource() != null) {
                plan.getSource().setComplementary(null);
                plan.setSource(null);
            }
            if (plan.getComplementary() != null) {
                plan.getComplementary().setSource(null);
                plan.setComplementary(null);
            }
                       
            em.remove(plan);
            
        } else {
            plan.setDisabled(true);
        }
    }

    /*@Override
    public List<FlightSchedulePlanEntity> retrieveFlightSchedulePlanByFlight(FlightEntity fli) throws FlightSchedulePlanNotFoundException, FlightNotFoundException{
    FlightEntity flight = flightSessionBean.retreiveFlightById(fli.getFlightID());
    Query query = em.createQuery("SELECT f FROM FlightSchedulePlanEntity f WHERE f.flight.FlightID = :flight AND f.disabled = false");
    query.setParameter("flight", flight.getFlightID());
    
    try {
    return query.getResultList();
    } catch (NoResultException | NonUniqueResultException ex) {
    throw new FlightSchedulePlanNotFoundException("Flight Schedule Plan does not exist!");
    }
    }*/
    
}


    

    
    

    


    
    

    

