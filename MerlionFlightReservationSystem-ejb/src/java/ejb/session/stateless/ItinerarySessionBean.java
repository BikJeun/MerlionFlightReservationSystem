/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ItineraryEntity;
import entity.UserEntity;
import exceptions.InputDataValidationException;
import exceptions.ItineraryExistException;
import exceptions.ItineraryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UserNotFoundException;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
public class ItinerarySessionBean implements ItinerarySessionBeanRemote, ItinerarySessionBeanLocal {

    @EJB
    private UserSessionBeanLocal userSessionBean;

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;  
    
    public ItinerarySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public ItineraryEntity createNewItinerary(ItineraryEntity itinerary, long userId) throws UnknownPersistenceException, InputDataValidationException, UserNotFoundException, ItineraryExistException {
        Set<ConstraintViolation<ItineraryEntity>> constraintViolations = validator.validate(itinerary);
        UserEntity user = userSessionBean.retrieveUserById(userId);
        
         if (constraintViolations.isEmpty()) {
            try {
                em.persist(itinerary);

                itinerary.setUser(user);
                user.getItineraries().add(itinerary);

                em.flush();
                return itinerary;
            } catch (PersistenceException ex) { 
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ItineraryExistException("Itinerary already exists");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public ItineraryEntity retrieveItineraryByID(long itineraryId) throws ItineraryNotFoundException {
        ItineraryEntity itinerary = em.find(ItineraryEntity.class, itineraryId);
        if (itinerary == null) {
            throw new ItineraryNotFoundException("Itinerary not found");
        } else {
            return itinerary;
        }
    }
    
    @Override
    public List<ItineraryEntity> retrieveItinerariesByCustomerId(Long userID) {
        Query query = em.createQuery("SELECT r FROM ItineraryEntity r WHERE r.user.UserID = :id");
        query.setParameter("id", userID);
        
        return query.getResultList();
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ItineraryEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for (ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
