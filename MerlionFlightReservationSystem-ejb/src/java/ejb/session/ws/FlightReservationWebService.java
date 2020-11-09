/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.FareEntity;
import entity.FlightScheduleEntity;
import entity.PartnerEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CabinClassNotFoundException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InvalidLoginCredentialException;
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
    public List<Pair<FlightScheduleEntity, FlightScheduleEntity>> getIndirectFlightSchedules(@WebParam String departure, @WebParam String destination, @WebParam Date date, @WebParam CabinClassTypeEnum cabin) throws FlightNotFoundException {
        return flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, date, cabin);
    }
    
    
}
