/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightRouteEntity;
import exceptions.AirportNotFoundException;
import exceptions.FlightRouteExistException;
import exceptions.FlightRouteNotFoundException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightRouteSessionBeanLocal {

    public FlightRouteEntity createNewFlightRoute(FlightRouteEntity flightRoute, long originAirportID, long destinationAirportID) throws FlightRouteExistException, UnknownPersistenceException, AirportNotFoundException;
    
    public FlightRouteEntity searchForFlightRouteByOriginAndDestination(String originAirportIATA, String destinationAirportIATA) throws FlightRouteNotFoundException;
    
    public long setComplementaryFlightRoute(long routeID) throws FlightRouteNotFoundException;
     
    public List<FlightRouteEntity> retrieveAllFlightRouteInOrder() throws FlightRouteNotFoundException ;
    
    public void removeFlightRoute(long flightRouteID) throws FlightRouteNotFoundException;
    
    public FlightRouteEntity enableFlightRoute(long originAirportID, long destinationAirportID) throws FlightRouteNotFoundException;

    public FlightRouteEntity retreiveFlightRouteById(Long id) throws FlightRouteNotFoundException;
}
