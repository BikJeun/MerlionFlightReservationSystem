/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.PartnerEntity;
import exceptions.InvalidLoginCredentialException;
import exceptions.PartnerNotFoundException;
import exceptions.PartnerUsernameExistException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public PartnerSessionBean() {
    }
    
    @Override
    public PartnerEntity createNewPartner(PartnerEntity partner) throws PartnerUsernameExistException, UnknownPersistenceException {
        try {
            em.persist(partner);
            em.flush();
            return partner;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new PartnerUsernameExistException("Partner with the same username exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public PartnerEntity retrievePartnerById(Long id) throws PartnerNotFoundException {
        PartnerEntity partner = em.find(PartnerEntity.class, id);
        if(partner != null) {
            return partner;
        } else {
            throw new PartnerNotFoundException("Partner id " + id.toString() + " does not exist!");
        }
    }
    
    @Override
    public PartnerEntity retrievePartnerByUsername(String username) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT p FROM PartnerEntity p WHERE p.username = :username");
        query.setParameter("username", username);
        
        try {
        return (PartnerEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerNotFoundException("Partner does not exist!");
        }
    }

    @Override
    public PartnerEntity doLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            PartnerEntity partner = retrievePartnerByUsername(username);
            
            if(partner.getPassword().equals(password)) {
                return partner;
            } else {
                throw new InvalidLoginCredentialException("Login Credentials are invalid. Please try again.\n");
            }
        } catch (PartnerNotFoundException ex) {
                throw new InvalidLoginCredentialException("Login Credentials are invalid. Please try again.\n");
        }
    }

    
    
    
}