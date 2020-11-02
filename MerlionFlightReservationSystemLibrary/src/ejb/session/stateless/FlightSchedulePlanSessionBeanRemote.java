/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.FlightSchedulePlanEntity;
import exceptions.FareNotFoundException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface FlightSchedulePlanSessionBeanRemote {
    
    public FlightSchedulePlanEntity createNewFlightSchedulePlan(FlightSchedulePlanEntity plan, long flightID, Pair<Date, Integer> pair, int recurrent) throws InputDataValidationException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException;
    
    public FlightSchedulePlanEntity createNewFlightSchedulePlanMultiple(FlightSchedulePlanEntity plan, long flightID, List<Pair<Date, Integer>> info) throws InputDataValidationException, UnknownPersistenceException, FlightNotFoundException, FlightSchedulePlanExistException;
    
    public List<FlightSchedulePlanEntity> retrieveAllFlightSchedulePlan() throws FlightSchedulePlanNotFoundException;
    
    public void associateExistingPlanToComplementaryPlan(Long sourcFlightSchedulePlanID, Long returnFlightSchedulePlanID) throws FlightSchedulePlanNotFoundException;
    
    public FlightSchedulePlanEntity retrieveFlightSchedulePlanEntityById(Long flightSchedulePlanID) throws FlightSchedulePlanNotFoundException;
    
    public void deleteFlightSchedulePlan(Long flightSchedulePlanID) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException, FareNotFoundException;
        
        
    }
