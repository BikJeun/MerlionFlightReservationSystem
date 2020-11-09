/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AirportEntity;
import exceptions.AirportNotFoundException;
import javax.ejb.Remote;

/**
 *
 * @author Ong Bik Jeun
 */
@Remote
public interface AirportSessionBeanRemote {

    public AirportEntity retrieveAirportById(Long id) throws AirportNotFoundException;

    public AirportEntity retrieveAirportByIATA(String iata) throws AirportNotFoundException;
    
}
