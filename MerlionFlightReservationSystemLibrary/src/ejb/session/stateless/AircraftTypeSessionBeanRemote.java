/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftTypeEntity;
import exceptions.AircraftTypeNotFoundException;
import javax.ejb.Remote;

/**
 *
 * @author Mitsuki
 */
@Remote
public interface AircraftTypeSessionBeanRemote {

    public AircraftTypeEntity retrieveAircraftTypeById(Long id) throws AircraftTypeNotFoundException;
    
}
