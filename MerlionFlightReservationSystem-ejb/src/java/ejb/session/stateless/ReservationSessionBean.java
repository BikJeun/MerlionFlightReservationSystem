/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightScheduleEntity;
import entity.ItineraryEntity;
import entity.PassengerEntity;
import entity.ReservationEntity;
import entity.SeatInventoryEntity;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.ItineraryNotFoundException;
import exceptions.ReservationExistException;
import exceptions.ReservationNotFoundException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateSeatsException;
import exceptions.UserNotFoundException;
import java.util.List;
import java.util.Set;
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
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private ItinerarySessionBeanLocal itinerarySessionBean;

    @EJB
    private SeatsInventorySessionBeanLocal seatsInventorySessionBean;

    @EJB
    private CabinClassSessionBeanLocal cabinClassSessionBean;

    @EJB
    private FareSessionBeanLocal fareSessionBean;

    @EJB
    private UserSessionBeanLocal userSessionBean; 

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;
           
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;  

    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

   
    @Override
    public long createNewReservation(ReservationEntity reservation, List<PassengerEntity> passengers, long flightScheduleId, long itineraryId) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, SeatInventoryNotFoundException, UpdateSeatsException, ItineraryNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<ReservationEntity>> constraintViolations = validator.validate(reservation);
        
        if (constraintViolations.isEmpty()) {
            try {
                FlightScheduleEntity flightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(flightScheduleId);
                ItineraryEntity itinerary = itinerarySessionBean.retrieveItineraryByID(itineraryId);

                SeatInventoryEntity seat = null;
                for (SeatInventoryEntity seats: flightSchedule.getSeatInventory()) {
                    if (seats.getCabin().getCabinClassType() == reservation.getCabinClassType()) {
                        seat = seats;
                    }
                } 
                if (seat == null) {
                    throw new SeatInventoryNotFoundException("Seat Inventory for specified cabin class not found");
                }
                
                em.persist(reservation);

                for (PassengerEntity passenger: passengers) {
                    em.persist(passenger);
                    reservation.getPassenger().add(passenger);
                    seatsInventorySessionBean.bookSeat(seat.getSeatInventoryID(), passenger.getSeatNumber());
                }

                flightSchedule.getReservations().add(reservation);
                reservation.setFlightSchedule(flightSchedule);

                reservation.setItinerary(itinerary);
                itinerary.getReservations().add(reservation);

                em.flush();

                return reservation.getReservationID();   
            } catch (PersistenceException ex) {              
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                         throw new ReservationExistException("Reservation already exist");
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
    public ReservationEntity retrieveReservationById(long id) throws ReservationNotFoundException {
        ReservationEntity res = em.find(ReservationEntity.class, id);
        
        if(res != null) {
            return res;
        } else {
            throw new ReservationNotFoundException("Reservation does not exist!");
        }
    } 
    
      private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ReservationEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for (ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}

    
