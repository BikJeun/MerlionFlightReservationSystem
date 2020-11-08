/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.FlightScheduleEntity;
import entity.ReservationEntity;
import exceptions.FlightScheduleNotFoundException;
import exceptions.ReservationExistException;
import exceptions.ReservationNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UserNotFoundException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface ReservationSessionBeanRemote {

    public ReservationEntity createNewReservation(FlightScheduleEntity flightSchedule, CustomerEntity currentCustomer, ReservationEntity reservation) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, UserNotFoundException;

    public List<ReservationEntity> retrieveReservationsByCustomerId(Long userID);

    public ReservationEntity retrieveReservationById(long id) throws ReservationNotFoundException;
    
}
