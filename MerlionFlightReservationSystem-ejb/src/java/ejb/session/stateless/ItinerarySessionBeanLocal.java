/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ItineraryEntity;
import exceptions.InputDataValidationException;
import exceptions.ItineraryExistException;
import exceptions.ItineraryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UserNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface ItinerarySessionBeanLocal {

    public ItineraryEntity retrieveItineraryByID(long itineraryId) throws ItineraryNotFoundException;
    
     public List<ItineraryEntity> retrieveItinerariesByCustomerId(Long userID);
    
    public ItineraryEntity createNewItinerary(ItineraryEntity itinerary, long userId) throws UnknownPersistenceException, InputDataValidationException, UserNotFoundException, ItineraryExistException; 
    
}
