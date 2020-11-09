/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightEntity;
import exceptions.AircraftConfigNotFoundException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFlightException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface FlightSessionBeanRemote {

    public FlightEntity createNewFlight(FlightEntity flight, Long chosenRoute, Long chosenConfig) throws FlightExistException, UnknownPersistenceException, FlightRouteNotFoundException, AircraftConfigNotFoundException;

    public void associateExistingFlightWithReturnFlight(Long flightID, Long returnFlightID) throws FlightNotFoundException;
    
    public FlightEntity retreiveFlightById(Long id) throws FlightNotFoundException;

    public List<FlightEntity> retrieveAllFlight() throws FlightNotFoundException;

    public void updateFlight(FlightEntity oldFlight) throws FlightNotFoundException, UpdateFlightException, InputDataValidationException, FlightRouteNotFoundException, UnknownPersistenceException, AircraftConfigNotFoundException;

    public void deleteFlight(Long flightID) throws FlightNotFoundException;

    public FlightEntity enableFlight(String flightNumber, long routeID, long configID) throws FlightNotFoundException;

    public FlightEntity retrieveFlightByFlightNumber(String flightNum) throws FlightNotFoundException;

    public List<FlightEntity> retrieveAllFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException;
   
    
}
