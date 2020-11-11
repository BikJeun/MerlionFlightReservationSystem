/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightScheduleEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CabinClassNotFoundException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UpdateFlightScheduleException;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface FlightScheduleSessionBeanRemote {

    public FlightScheduleEntity retrieveFlightScheduleById(Long flightScheduleID) throws FlightScheduleNotFoundException;
    
    public List<FlightScheduleEntity> getFlightSchedules(String departure, String destination, Date date, CabinClassTypeEnum cabin) throws FlightNotFoundException;

    public List<Pair<FlightScheduleEntity, FlightScheduleEntity>> getIndirectFlightSchedules(String departure, String destination, Date date, CabinClassTypeEnum cabin) throws FlightNotFoundException;

    public FareEntity getSmallestFare(FlightScheduleEntity flightSchedule, CabinClassTypeEnum cabinClassType) throws FlightScheduleNotFoundException, CabinClassNotFoundException;

    public SeatInventoryEntity getCorrectSeatInventory(FlightScheduleEntity flightSchedule, CabinClassTypeEnum cabinClassType) throws FlightScheduleNotFoundException, SeatInventoryNotFoundException;

    public FlightScheduleEntity updateFlightSchedule(long flightScheduleId, Date newDepartureDateTime, double newFlightDuration) throws FlightScheduleNotFoundException, UpdateFlightScheduleException;

    public void deleteFlightSchedule(long flightScheduleId) throws FlightScheduleNotFoundException, UpdateFlightScheduleException;

}
