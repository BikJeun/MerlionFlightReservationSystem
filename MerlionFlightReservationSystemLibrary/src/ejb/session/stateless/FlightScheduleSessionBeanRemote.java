/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightScheduleEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.FlightScheduleNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface FlightScheduleSessionBeanRemote {

public List<FlightScheduleEntity> retrieveFlightScheduleByDate(Long flightSchedule, Date departureDate) throws FlightScheduleNotFoundException;    

    public boolean checkIfHaveCabin(Long schedID, CabinClassTypeEnum cabin);
}
