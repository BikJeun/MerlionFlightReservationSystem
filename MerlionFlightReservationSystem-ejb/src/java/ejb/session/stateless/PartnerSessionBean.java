/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.PartnerEntity;
import exceptions.PartnerNotFoundException;
import exceptions.PartnerUsernameExistException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

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
}