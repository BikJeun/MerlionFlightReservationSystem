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
import exceptions.InputDataValidationException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UpdateSeatsException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class SeatsInventorySessionBean implements SeatsInventorySessionBeanRemote, SeatsInventorySessionBeanLocal {
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public SeatsInventorySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
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
    public SeatInventoryEntity retrieveSeatsById(Long seatInventoryID) throws SeatInventoryNotFoundException{
        SeatInventoryEntity seat = em.find(SeatInventoryEntity.class, seatInventoryID);
        
        if(seat != null) {
            return seat;
        } else {
            throw new SeatInventoryNotFoundException("Seat Inventory does not exist!");
        }
    }
    
    @Override
    public void deleteSeatInventory(List<SeatInventoryEntity> seats) {
        for (SeatInventoryEntity seat: seats) {
            em.remove(seat);
        }
    }
    
    /*@Override
    public List<SeatInventoryEntity> retrieveInventoryByCabin(FlightScheduleEntity flight, CabinClassTypeEnum cabin) {
    Query query = em.createQuery("SELECT s FROM SeatInventoryEntity s WHERE s.flightSchedule = :flight AND s.cabin.cabinClassType = :type");
    query.setParameter("flight", flight);
    query.setParameter("type", cabin);
    
    return query.getResultList();
    }*/
    
   
    @Override
    public void bookSeat(long seatInventoryId, String seatNumber) throws SeatInventoryNotFoundException, UpdateSeatsException {
             
        SeatInventoryEntity seatInventory = retrieveSeatsById(seatInventoryId);
        
        int col = seatNumber.charAt(0) - 'A';
        int row = Integer.parseInt(seatNumber.substring(1)) - 1;
        
        char[][] seats = seatInventory.getSeats();
        if (seats[row][col] == '-' ){
            seats[row][col] = 'X';
            seatInventory.setSeats(seats);
        } else {
            throw new UpdateSeatsException("Seat already booked");
        }
        seatInventory.setReserved(seatInventory.getReserved() + 1);
        seatInventory.setBalance(seatInventory.getBalance() - 1);       
    }
    
    @Override
    public boolean checkIfBooked(SeatInventoryEntity seatInventory, String seatNumber) {
        try {
            SeatInventoryEntity scheds = retrieveSeatsById(seatInventory.getSeatInventoryID());
            char[][] mtx = scheds.getSeats();
            int col = seatNumber.charAt(0) - 'A';
            int row = Integer.parseInt(seatNumber.substring(1)) - 1;
            
            if(mtx[row][col] == 'X') {
                return true;
            } 
        } catch (SeatInventoryNotFoundException ex) {
            System.out.println(ex.getMessage());        
        }
        return false;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<SeatInventoryEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
        
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        return msg;
    }

    
    
    
}
