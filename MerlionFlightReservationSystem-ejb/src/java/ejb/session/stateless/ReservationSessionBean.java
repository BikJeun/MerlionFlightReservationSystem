/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.FlightScheduleEntity;
import entity.ReservationEntity;
import entity.UserEntity;
import exceptions.FlightScheduleNotFoundException;
import exceptions.ReservationExistException;
import exceptions.ReservationNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UserNotFoundException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private UserSessionBeanLocal userSessionBean;

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    

    public ReservationSessionBean() {
    }

    @Override
    public ReservationEntity createNewReservation(FlightScheduleEntity flightSchedule, CustomerEntity currentCustomer, ReservationEntity reservation) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, UserNotFoundException {
        try {
            flightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(flightSchedule.getFlightScheduleID());
            UserEntity user = userSessionBean.retrieveUserById(currentCustomer.getUserID());
            
            em.persist(reservation);
            em.flush();
            
            flightSchedule.getReservations().add(reservation);
            user.getReservations().add(reservation);
            
        } catch (FlightScheduleNotFoundException ex) {
            throw new FlightScheduleNotFoundException(ex.getMessage());
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
        em.refresh(reservation);
        return reservation;     
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
