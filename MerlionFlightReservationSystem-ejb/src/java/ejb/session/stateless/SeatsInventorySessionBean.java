/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import entity.FlightScheduleEntity;
import entity.SeatInventoryEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class SeatsInventorySessionBean implements SeatsInventorySessionBeanRemote, SeatsInventorySessionBeanLocal {

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    public SeatsInventorySessionBean() {
    }
    
    // only exposed in local interface
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public SeatInventoryEntity createSeatInventory(SeatInventoryEntity seatInventory, FlightScheduleEntity flightSchedule, CabinClassEntity cabinClass) {
        
        em.persist(seatInventory);
        
        int noOfRows = cabinClass.getNumOfRows();
        int noOfSeatsAbreast = cabinClass.getNumOfSeatsAbreast();
        char[][] seats = new char[noOfRows][noOfSeatsAbreast];
        
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < noOfSeatsAbreast; j++) {
                seats[i][j] = '-';
            }
        }
        seatInventory.setSeats(seats);
        
        seatInventory.setCabin(cabinClass);
        seatInventory.setFlightSchedule(flightSchedule);
        flightSchedule.getSeatInventory().add(seatInventory);
        
        return seatInventory;
    }

    @Override
    public void deleteSeatInventory(List<SeatInventoryEntity> seats) {
        for (SeatInventoryEntity seat: seats) {
            em.remove(seat);
        }
    }
    
}
