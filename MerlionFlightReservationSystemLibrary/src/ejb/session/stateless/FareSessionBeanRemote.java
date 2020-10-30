/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import exceptions.CabinClassNotFoundException;
import exceptions.FareExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface FareSessionBeanRemote {

    public FareEntity createFareEntity(FareEntity fare, long flightSchedulePlanID, long cabinClassID) throws FlightSchedulePlanNotFoundException, CabinClassNotFoundException, FareExistException, UnknownPersistenceException;
    
}
