/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfigurationEntity;
import entity.CabinClassEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.AircraftConfigNotFoundException;
import exceptions.CabinClassNotFoundException;
import exceptions.CabinClassTypeEnumNotFoundException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mitsuki
 */
@Stateless
public class CabinClassSessionBean implements CabinClassSessionBeanRemote, CabinClassSessionBeanLocal {

    @EJB
    private AircraftConfigurationSessionBeanLocal aircraftConfigurationSessionBean;

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    // only exposed in local interface => aircraftConfig passed in is managed
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public CabinClassEntity createNewCabinClass(CabinClassEntity cabin, AircraftConfigurationEntity aircraft) {  
        em.persist(cabin);
        
        // Bidirectional association between cabinClass <-> aircraftConfig
        cabin.setAircraftConfig(aircraft); //this instance of aircraft is managed
        if(!aircraft.getCabin().contains(cabin)) {
            aircraft.getCabin().add(cabin);
        }
        em.refresh(cabin);
        return cabin; // will never have persistance error due to constraint violation because of no constraints
    }
    
    @Override
    public CabinClassEntity retrieveCabinByID(Long cabinClassID) throws CabinClassNotFoundException {
        CabinClassEntity cabin = em.find(CabinClassEntity.class, cabinClassID);
        if(cabin != null) {
            return cabin;
        } else {
            throw new CabinClassNotFoundException("Cabin class with " + cabinClassID + " not found!\n");
        }
    }
    
    @Override
    public CabinClassTypeEnum findEnumType(String input) throws CabinClassTypeEnumNotFoundException {
        if(input.equalsIgnoreCase("F")) {
            return CabinClassTypeEnum.F;
        } else if(input.equalsIgnoreCase("J")){
            return CabinClassTypeEnum.J;
        } else if(input.equalsIgnoreCase("W")){
            return CabinClassTypeEnum.W;
        } else if(input.equalsIgnoreCase("Y")){
        return CabinClassTypeEnum.Y;
        } else {
            throw new CabinClassTypeEnumNotFoundException("Invalid input of cabin class type!\n");
        }
    } 

    @Override
    public int computeMaxSeatCapacity(int rows, int seatsAbreast) {
        return rows * seatsAbreast;
    }
}
    