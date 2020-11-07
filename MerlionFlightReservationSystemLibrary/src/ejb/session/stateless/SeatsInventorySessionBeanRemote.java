/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.FlightScheduleEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface SeatsInventorySessionBeanRemote {

    /*    public List<SeatInventoryEntity> retrieveInventoryByCabin(FlightScheduleEntity flight, CabinClassTypeEnum cabin);*/    
}
