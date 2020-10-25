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
import exceptions.CabinClassExistException;
import exceptions.CabinClassNotFoundException;
import exceptions.CabinClassTypeEnumNotFoundException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Remote;

/**
 *
 * @author Mitsuki
 */
@Remote
public interface CabinClassSessionBeanRemote {

    public CabinClassTypeEnum findEnumType(String trim) throws CabinClassTypeEnumNotFoundException;

    public int computeMaxSeatCapacity(int rows, int seatsAbreast);

    public CabinClassEntity createNewCabinClass(CabinClassEntity cabin, AircraftConfigurationEntity aircraft);

    public CabinClassEntity retrieveCabinByID(Long cabinClassID) throws CabinClassNotFoundException;
    
}
