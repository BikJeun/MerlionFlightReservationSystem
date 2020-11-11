/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.FareExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightSchedulePlanSessionBeanLocal {
    
    public FlightSchedulePlanEntity createNewFlightSchedulePlan(FlightSchedulePlanEntity plan, List<FareEntity> fares, long flightID, Pair<Date, Double> pair, int recurrent) throws InputDataValidationException, FareExistException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException ;
    
    public FlightSchedulePlanEntity createNewFlightSchedulePlanMultiple(FlightSchedulePlanEntity plan, List<FareEntity> fares, long flightID, List<Pair<Date, Double>> info) throws InputDataValidationException, FareExistException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException;
    
    public FlightSchedulePlanEntity retrieveFlightSchedulePlanEntityById(Long flightSchedulePlanID) throws FlightSchedulePlanNotFoundException;
    
    public void associateExistingPlanToComplementaryPlan(Long sourcFlightSchedulePlanID, Long returnFlightSchedulePlanID) throws FlightSchedulePlanNotFoundException;
    
}
