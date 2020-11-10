/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.CustomerEntity;
import entity.FareEntity;
import entity.FlightScheduleEntity;
import entity.PassengerEntity;
import entity.ReservationEntity;
import entity.SeatInventoryEntity;
import entity.UserEntity;
import exceptions.CabinClassNotFoundException;
import exceptions.FareNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.ReservationExistException;
import exceptions.ReservationNotFoundException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateSeatsException;
import exceptions.UserNotFoundException;
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
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

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
    
    

    public ReservationSessionBean() {
    }

    @Override
    public long createNewReservation(ReservationEntity reservation, List<PassengerEntity> passengers, long flightScheduleId, long userId, long fareId, long cabinClassId) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, UserNotFoundException, FareNotFoundException, CabinClassNotFoundException, SeatInventoryNotFoundException, UpdateSeatsException {
        try {
            FlightScheduleEntity flightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(flightScheduleId);
            UserEntity user = userSessionBean.retrieveUserById(userId);
            FareEntity fare = fareSessionBean.retrieveFareById(fareId);
            CabinClassEntity cabinClass = cabinClassSessionBean.retrieveCabinByID(cabinClassId);
            
            SeatInventoryEntity seat = null;
            for (SeatInventoryEntity seats: flightSchedule.getSeatInventory()) {
                if (seats.getCabin().getCabinClassType() == cabinClass.getCabinClassType()) {
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
            
            reservation.setCabinClass(cabinClass);
            
            reservation.setFare(fare);
            
            reservation.setUser(user);
            user.getReservations().add(reservation);              
          
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
        
    } 

    @Override
    public List<ReservationEntity> retrieveReservationsByCustomerId(Long userID) {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.user.UserID = :id");
        query.setParameter("id", userID);
        
        return query.getResultList();
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
}

    
