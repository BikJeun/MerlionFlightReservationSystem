/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightScheduleEntity;
import exceptions.FlightScheduleExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightScheduleSessionBeanLocal {

    public FlightScheduleEntity createNewSchedule(Long flightSchedulePlanID, FlightScheduleEntity schedule) throws FlightScheduleExistException, UnknownPersistenceException, InputDataValidationException, FlightSchedulePlanNotFoundException;
    
}
