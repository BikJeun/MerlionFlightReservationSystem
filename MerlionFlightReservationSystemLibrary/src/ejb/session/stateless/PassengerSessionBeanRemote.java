/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PassengerEntity;
import exceptions.PassengerAlreadyExistException;
import exceptions.PassengerNotFoundException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Remote;

/**
 *
 * @author Mitsuki
 */
@Remote
public interface PassengerSessionBeanRemote {

    public PassengerEntity createNewPassenger(PassengerEntity passenger) throws PassengerAlreadyExistException, UnknownPersistenceException;

    public PassengerEntity retrievePassengerByPassport(String passport);
    
}
