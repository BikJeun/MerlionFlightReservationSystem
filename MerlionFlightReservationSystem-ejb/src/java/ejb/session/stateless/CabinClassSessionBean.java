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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public CabinClassEntity createNewCabinClass(CabinClassEntity cabin, AircraftConfigurationEntity aircraft) {  
        em.persist(cabin);
        if(!aircraft.getCabin().contains(cabin)) {
            aircraft.getCabin().add(cabin);
        }
        if(!cabin.getAircraftConfig().contains(aircraft)) {
            cabin.getAircraftConfig().add(aircraft);
        }
        return cabin;
        // QN: Does it matter if i flush here or is this considered 'exiting business method'?
        /*  Removed because will never be thrown
        } catch(PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new CabinClassExistException("Cabin class already exist!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } */
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
    public void associateAircraftConfigToCabin(Long aircraftConfigID, Long cabinClassID) {
        try {
            AircraftConfigurationEntity aircraft = aircraftConfigurationSessionBean.retriveAircraftConfigByID(aircraftConfigID);
            CabinClassEntity cabin = retrieveCabinByID(cabinClassID);
            
            if(!aircraft.getCabin().contains(cabin)) {
                aircraft.getCabin().add(cabin);
            }
            if(!cabin.getAircraftConfig().contains(aircraft)) {
                cabin.getAircraftConfig().add(aircraft);
            }
        } catch (CabinClassNotFoundException | AircraftConfigNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        
    }

}
    