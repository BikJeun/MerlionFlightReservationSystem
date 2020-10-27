/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightEntity;
import exceptions.FlightNotFoundException;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightSessionBeanLocal {

    public FlightEntity retreiveFlightById(Long id) throws FlightNotFoundException;
    
}
