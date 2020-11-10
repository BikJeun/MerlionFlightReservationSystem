/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.SeatsInventorySessionBeanLocal;
import entity.FareEntity;
import entity.FlightScheduleEntity;
import entity.PartnerEntity;
import entity.PassengerEntity;
import entity.ReservationEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CabinClassNotFoundException;
import exceptions.FareNotFoundException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InvalidLoginCredentialException;
import exceptions.ReservationExistException;
import exceptions.ReservationNotFoundException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateSeatsException;
import exceptions.UserNotFoundException;
import helper.MyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author Mitsuki
 */
@WebService(serviceName = "FlightReservationWebService")
@Stateless()
public class FlightReservationWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private SeatsInventorySessionBeanLocal seatsInventorySessionBean;
 
    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    @WebMethod(operationName = "doLogin")
    public PartnerEntity doLogin(@WebParam String username, @WebParam String password) throws InvalidLoginCredentialException {
        return partnerSessionBean.doLogin(username, password);
    }
    
    @WebMethod(operationName = "getFlightSchedules")
    public List<FlightScheduleEntity> getFlightSchedules(@WebParam String departure, @WebParam String destination, @WebParam Date date, @WebParam CabinClassTypeEnum cabin) throws FlightNotFoundException {
        return flightScheduleSessionBean.getFlightSchedules(departure, destination, date, cabin);
    }
    
    @WebMethod(operationName = "getBiggestFare")
    public FareEntity getBiggestFare(@WebParam FlightScheduleEntity flightScheduleEntity, @WebParam CabinClassTypeEnum type) throws FlightScheduleNotFoundException, CabinClassNotFoundException {
        return flightScheduleSessionBean.getBiggestFare(flightScheduleEntity, type);
    }
    
    @WebMethod(operationName = "getIndirectFlightSchedules")
    public List<MyPair> getIndirectFlightSchedules(@WebParam String departure, @WebParam String destination, @WebParam Date date, @WebParam CabinClassTypeEnum cabin) throws FlightNotFoundException {
        List<Pair<FlightScheduleEntity, FlightScheduleEntity>> list = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, date, cabin);
        List<MyPair> newList = new ArrayList<>();
        for(Pair<FlightScheduleEntity, FlightScheduleEntity> pairs : list) {
            MyPair newPair = new MyPair(pairs.getKey(), pairs.getValue());
            newList.add(newPair);
        }
        return newList;
    }
    
    @WebMethod(operationName = "retrieveFlightScheduleById")
    public FlightScheduleEntity retrieveFlightScheduleById(@WebParam Long outbound1) throws InvalidLoginCredentialException, FlightScheduleNotFoundException {
        return flightScheduleSessionBean.retrieveFlightScheduleById(outbound1);
    }
    
    @WebMethod(operationName = "getCorrectSeatInventory")
    public SeatInventoryEntity getCorrectSeatInventory(@WebParam FlightScheduleEntity flightSchedule, @WebParam CabinClassTypeEnum cabinClassType) throws FlightScheduleNotFoundException, SeatInventoryNotFoundException {
        return flightScheduleSessionBean.getCorrectSeatInventory(flightSchedule, cabinClassType);
    }
    
    @WebMethod(operationName = "checkIfBooked")
    public boolean checkIfBooked(@WebParam SeatInventoryEntity seatInventory,@WebParam String seatNumber) {
        return seatsInventorySessionBean.checkIfBooked(seatInventory, seatNumber);
    }
    
    @WebMethod(operationName = "createNewReservation")
    public long createNewReservation(@WebParam ReservationEntity reservation,@WebParam List<PassengerEntity> passengers,@WebParam long flightScheduleId,@WebParam long userId,@WebParam long fareId,@WebParam long cabinClassId) throws ReservationExistException, UnknownPersistenceException, FlightScheduleNotFoundException, UserNotFoundException, FareNotFoundException, CabinClassNotFoundException, SeatInventoryNotFoundException, UpdateSeatsException {
        return reservationSessionBean.createNewReservation(reservation, 
                         passengers, flightScheduleId, userId, 
                         fareId,cabinClassId);      
    }
    
    @WebMethod(operationName = "retrieveReservationsByCustomerId")
    public List<ReservationEntity> retrieveReservationsByCustomerId(@WebParam Long userID) {
        return reservationSessionBean.retrieveReservationsByCustomerId(userID);
    }
    
    @WebMethod(operationName = "retrieveReservationById")
    public ReservationEntity retrieveReservationById(@WebParam long id) throws ReservationNotFoundException {
        return reservationSessionBean.retrieveReservationById(id);
    }
}
    
    

