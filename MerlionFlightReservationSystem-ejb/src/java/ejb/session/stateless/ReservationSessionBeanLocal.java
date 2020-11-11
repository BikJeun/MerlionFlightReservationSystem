/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PassengerEntity;
import entity.ReservationEntity;
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
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface ReservationSessionBeanLocal {

    public long createNewReservation(ReservationEntity reservation, List<PassengerEntity> passengers, long flightScheduleId, long itineraryId) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, SeatInventoryNotFoundException, UpdateSeatsException, ItineraryNotFoundException, InputDataValidationException;

    public ReservationEntity retrieveReservationById(long id) throws ReservationNotFoundException;
        
}
