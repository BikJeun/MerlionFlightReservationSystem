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
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightSessionBeanLocal {
    
    public FlightEntity createNewFlight(FlightEntity flight, Long chosenRoute, Long chosenConfig) throws FlightExistException, UnknownPersistenceException, FlightRouteNotFoundException, AircraftConfigNotFoundException, InputDataValidationException;

    public FlightEntity retreiveFlightById(Long id) throws FlightNotFoundException;
    
    public List<FlightEntity> retrieveAllFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException;
    
    public List<FlightEntity[]> retrieveAllIndirectFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException;
    
    public void associateExistingFlightWithReturnFlight(Long sourceFlightID, Long returnFlightID) throws FlightNotFoundException;
    
    public FlightEntity retrieveFlightByFlightNumber(String flightNum) throws FlightNotFoundException;
}
