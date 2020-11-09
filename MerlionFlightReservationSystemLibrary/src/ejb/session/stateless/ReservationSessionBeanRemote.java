/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.FlightScheduleEntity;
import entity.PassengerEntity;
import entity.ReservationEntity;
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
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface ReservationSessionBeanRemote {

    public long createNewReservation(ReservationEntity reservation, List<PassengerEntity> passengers, long flightScheduleId, long userId, long fareId, long cabinClassId) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, UserNotFoundException, FareNotFoundException, CabinClassNotFoundException, SeatInventoryNotFoundException, UpdateSeatsException ;

    public List<ReservationEntity> retrieveReservationsByCustomerId(Long userID);

    public ReservationEntity retrieveReservationById(long id) throws ReservationNotFoundException;
    
}
