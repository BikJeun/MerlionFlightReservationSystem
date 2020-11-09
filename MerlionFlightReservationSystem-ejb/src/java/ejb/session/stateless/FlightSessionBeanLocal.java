/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightEntity;
import exceptions.FlightNotFoundException;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightSessionBeanLocal {

    public FlightEntity retreiveFlightById(Long id) throws FlightNotFoundException;
    
    public List<FlightEntity> retrieveAllFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException;
    
    public List<FlightEntity[]> retrieveAllIndirectFlightByFlightRoute(String originIATACode, String destinationIATACode) throws FlightNotFoundException;
    
}
