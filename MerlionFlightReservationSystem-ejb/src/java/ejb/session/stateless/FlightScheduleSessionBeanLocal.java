/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InputDataValidationException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface FlightScheduleSessionBeanLocal {

    public FlightScheduleEntity createNewSchedule(FlightSchedulePlanEntity flightSchedulePlan, FlightScheduleEntity schedule) throws InputDataValidationException;

    public FlightScheduleEntity retrieveEarliestDepartureSchedule(List<FlightScheduleEntity> list) throws FlightScheduleNotFoundException;
    
    public FlightScheduleEntity retrieveFlightScheduleById(Long flightScheduleID) throws FlightScheduleNotFoundException;

    public void deleteSchedule(List<FlightScheduleEntity> flightSchedule) throws FlightScheduleNotFoundException;
    
}
