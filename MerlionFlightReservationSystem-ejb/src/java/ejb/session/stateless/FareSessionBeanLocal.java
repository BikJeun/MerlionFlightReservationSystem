/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.FareExistException;
import exceptions.FareNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FareSessionBeanLocal {
    
    public FareEntity createFareEntity(FareEntity fare, FlightSchedulePlanEntity flightSchedulePlan) throws FareExistException, UnknownPersistenceException, InputDataValidationException;

    public void deleteFares(List<FareEntity> fares);
    
    public FareEntity retrieveFareById(Long fareID) throws FareNotFoundException;
    
}
