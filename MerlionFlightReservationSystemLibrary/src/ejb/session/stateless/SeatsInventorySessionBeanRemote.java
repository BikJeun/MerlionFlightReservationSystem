/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SeatInventoryEntity;
import exceptions.InputDataValidationException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UpdateSeatsException;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface SeatsInventorySessionBeanRemote {

    public void updateSeatInventory(SeatInventoryEntity sched) throws SeatInventoryNotFoundException, UpdateSeatsException, InputDataValidationException;

    /*    public List<SeatInventoryEntity> retrieveInventoryByCabin(FlightScheduleEntity flight, CabinClassTypeEnum cabin);*/    

    public boolean checkIfBooked(SeatInventoryEntity sched, String reserve);
}
