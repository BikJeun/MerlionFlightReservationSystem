/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import helper.MyPair;
import entity.PartnerEntity;
import entity.FlightScheduleEntity;
import entity.FareEntity;
import entity.SeatInventoryEntity;
import entity.ReservationEntity;
import entity.PassengerEntity;
import entity.ItineraryEntity;
import enumeration.CabinClassTypeEnum;
import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import ejb.session.stateless.ItinerarySessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.SeatsInventorySessionBeanLocal;
import exceptions.CabinClassNotFoundException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.InvalidLoginCredentialException;
import exceptions.ItineraryExistException;
import exceptions.ItineraryNotFoundException;
import exceptions.ReservationExistException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateSeatsException;
import exceptions.UserNotFoundException;
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
 * @author Ooi Jun Hao
 */
@WebService(serviceName = "FlightReservationWebService")
@Stateless()
public class FlightReservationWebService {

    @EJB
    private ItinerarySessionBeanLocal itinerarySessionBean;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private SeatsInventorySessionBeanLocal seatsInventorySessionBean;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBean;
   
    
    @WebMethod(operationName = "doLogin")
    public PartnerEntity doLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        return partnerSessionBean.doLogin(username, password);
    }

    @WebMethod(operationName = "getFlightSchedules")
    public List<FlightScheduleEntity> getFlightSchedules(@WebParam(name = "origin") String origin, 
            @WebParam(name = "destination") String destination, 
            @WebParam(name = "date") Date date,
            @WebParam(name = "cabinclasstype") CabinClassTypeEnum cabinclasstype) throws FlightNotFoundException {
        return flightScheduleSessionBean.getFlightSchedules(origin, destination, date, cabinclasstype);
    }
    
    @WebMethod(operationName = "getBiggestFare")
    public FareEntity getBiggestFare(@WebParam(name = "flightscheduleentity") FlightScheduleEntity flightscheduleentity,
            @WebParam(name = "cabinclasstype") CabinClassTypeEnum cabinclastype) throws 
            FlightScheduleNotFoundException, 
            CabinClassNotFoundException {
        return flightScheduleSessionBean.getBiggestFare(flightscheduleentity, cabinclastype);
    }
    
    @WebMethod(operationName = "getIndirectFlightSchedules")
    public List<MyPair> getIndirectFlightSchedules(@WebParam(name = "origin") String origin,
            @WebParam(name = "destination") String destination,
            @WebParam(name = "date") Date date,
            @WebParam(name = "cabinclasstype") CabinClassTypeEnum cabinclasstype) throws 
            FlightNotFoundException {
        List<Pair<FlightScheduleEntity, FlightScheduleEntity>> list = flightScheduleSessionBean.getIndirectFlightSchedules(origin, destination, date, cabinclasstype);
        List<MyPair> newList = new ArrayList<>();
        for (Pair<FlightScheduleEntity, FlightScheduleEntity> pairs : list) {
            MyPair newPair = new MyPair(pairs.getKey(), pairs.getValue());
            newList.add(newPair);
        }
        return newList;
    }
    
    @WebMethod(operationName = "retrieveFlightScheduleById")
    public FlightScheduleEntity retrieveFlightScheduleById(@WebParam(name = "flightscheduleid") long flightscheduleid) throws FlightScheduleNotFoundException {
        return flightScheduleSessionBean.retrieveFlightScheduleById(flightscheduleid);
    }
    
    @WebMethod(operationName = "getCorrectSeatInventory")
    public SeatInventoryEntity getCorrectSeatInventory(@WebParam(name = "flightscheduleentity") FlightScheduleEntity flightscheduleentity,
            @WebParam(name = "cabinclasstype") CabinClassTypeEnum cabinclasstype) throws 
            FlightScheduleNotFoundException, 
            SeatInventoryNotFoundException {
        return flightScheduleSessionBean.getCorrectSeatInventory(flightscheduleentity, cabinclasstype);
    }
    
    @WebMethod(operationName = "checkIfBooked")
    public boolean checkIfBooked(@WebParam(name = "seatinventoryentity") SeatInventoryEntity seatinventoryentity,
            @WebParam(name = "seatnumber") String seatnumber) {
        return seatsInventorySessionBean.checkIfBooked(seatinventoryentity, seatnumber);
    }
    
    @WebMethod(operationName = "createNewReservation")
    public long createNewReservation(@WebParam(name = "reservationentity") ReservationEntity reservationentity,
            @WebParam(name = "passengers") List<PassengerEntity> passengers,
            @WebParam(name = "flightscheduleid") long flightscheduleid,
            @WebParam(name = "itineraryid") long itineraryid) throws 
            InputDataValidationException, 
            ReservationExistException,
            UnknownPersistenceException,
            FlightScheduleNotFoundException,
            SeatInventoryNotFoundException,
            UpdateSeatsException,
            ItineraryNotFoundException {
        return reservationSessionBean.createNewReservation(reservationentity, passengers, flightscheduleid, itineraryid);
    }
    
    @WebMethod(operationName = "createNewItinerary")
    public long createNewItinerary(@WebParam(name = "creditcardnumber") String creditcardnumber,
            @WebParam(name = "cvv") String cvv,
            @WebParam(name = "userid") long userid) throws 
            UserNotFoundException, 
            InputDataValidationException,
            UnknownPersistenceException,
            ItineraryExistException {
        return itinerarySessionBean.createNewItinerary(new ItineraryEntity(creditcardnumber, cvv), userid).getItineraryId();
    }
    
    @WebMethod(operationName = "retrieveItinerariesByUserId")
    public List<ItineraryEntity> retrieveItinerariesByUserId(@WebParam(name = "userid") long userid) {
        return itinerarySessionBean.retrieveItinerariesByCustomerId(userid);
    }
    
    @WebMethod(operationName = "retreiveItineraryById")
    public ItineraryEntity retrieveItineraryById(@WebParam(name = "itineraryid") long itineraryid) throws ItineraryNotFoundException {
        return itinerarySessionBean.retrieveItineraryByID(itineraryid);
    }
}
