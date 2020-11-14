/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.FlightScheduleEntity;
import entity.SeatInventoryEntity;
import exceptions.InputDataValidationException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UpdateSeatsException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface SeatsInventorySessionBeanLocal {
    
    public void deleteSeatInventory(List<SeatInventoryEntity> seats);
    
    public SeatInventoryEntity createSeatInventory(SeatInventoryEntity seatInventory, FlightScheduleEntity flightSchedule, CabinClassEntity cabinClass) throws InputDataValidationException;
    
    public SeatInventoryEntity retrieveSeatsById(Long seatInventoryID) throws SeatInventoryNotFoundException;
    
    public void bookSeat(long seatInventoryId, String seatNumber) throws SeatInventoryNotFoundException, UpdateSeatsException;

    public boolean checkIfBooked(SeatInventoryEntity seatInventory, String seatNumber);
    
}
