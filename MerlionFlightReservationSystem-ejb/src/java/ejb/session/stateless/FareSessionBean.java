/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import entity.FlightSchedulePlanEntity;
import exceptions.FareExistException;
import exceptions.FareNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFareException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class FareSessionBean implements FareSessionBeanRemote, FareSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;  
    
    public FareSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Only exposed in local interface
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public FareEntity createFareEntity(FareEntity fare, FlightSchedulePlanEntity flightSchedulePlan) throws FareExistException, UnknownPersistenceException, InputDataValidationException {     
        Set<ConstraintViolation<FareEntity>> constraintViolations = validator.validate(fare);
        
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(fare);

                flightSchedulePlan.getFares().add(fare);
                fare.setFlightSchedulePlan(flightSchedulePlan);

               
                return fare;
            } catch (PersistenceException ex) { 
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new FareExistException("Overlap in fare basis codes");
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
    public FareEntity retrieveFareById(Long fareID) throws FareNotFoundException {
        FareEntity fare = em.find(FareEntity.class, fareID);
        if (fare != null) {
            return fare;
        } else {
            throw new FareNotFoundException("Fare " + fareID + " not found!");
        }
    }
    
    @Override
    public FareEntity updateFare(long fareID, BigDecimal newCost) throws FareNotFoundException, UpdateFareException {
        try {
            FareEntity fare = retrieveFareById(fareID);
            fare.setFareAmount(newCost);
            em.flush();
            return fare;
        } catch (PersistenceException ex) {
            throw new UpdateFareException("Invalid new cost");
        }
    }
    
    // only exposed in local interface, managed instances passed in
    @Override
    public void deleteFares(List<FareEntity> fares) {
        for(FareEntity fare : fares) {
            em.remove(fare);
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FareEntity>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for (ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}


