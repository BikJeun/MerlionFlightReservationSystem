/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import exceptions.CustomerExistException;
import exceptions.CustomerNotFoundException;
import exceptions.InvalidLoginCredentialException;
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
 * @author Ooi Jun Hao
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    public CustomerSessionBean() {
    }

    @Override
    public CustomerEntity createNewCustomerEntity(CustomerEntity customer) throws UnknownPersistenceException, CustomerExistException {
        try {
            em.persist(customer);
            em.flush();
            return customer;
        } catch (PersistenceException ex) {
              if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new CustomerExistException("ID Number/Contact Number/Username already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotFoundException{
        Query query = em.createQuery("SELECT e FROM CustomerEntity e WHERE e.username = :user");
        query.setParameter("user", username);
        
        try{
            return (CustomerEntity)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer does not exist!");
        }
    }
    
    @Override
    public CustomerEntity doLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            CustomerEntity customer = retrieveCustomerByUsername(username);
            if(customer.getPassword().equals(password)) {
                return customer;
            } else {
                throw new InvalidLoginCredentialException("Wrong password input!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist!");
        }
        
    }

    
}
