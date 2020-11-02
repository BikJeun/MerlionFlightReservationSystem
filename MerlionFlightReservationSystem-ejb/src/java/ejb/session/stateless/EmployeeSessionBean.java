/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.stateless;

import entity.EmployeeEntity;
import exceptions.EmployeeNotFoundException;
import exceptions.EmployeeUsernameExistException;
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
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public EmployeeSessionBean() {
    }
    
    @Override
    public EmployeeEntity createNewEmployee(EmployeeEntity employee) throws EmployeeUsernameExistException, UnknownPersistenceException {
        try {
            em.persist(employee);
            em.flush();
            return employee;
        } catch  (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new EmployeeUsernameExistException("An employee with the same username exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public EmployeeEntity retrieveEmployeeById(Long id) throws EmployeeNotFoundException {
        EmployeeEntity employee = em.find(EmployeeEntity.class, id);
        
        if(employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee id " + id.toString() + " does not exist!");
        }
    }
    
    @Override
    public EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException{
        Query query = em.createQuery("SELECT e FROM EmployeeEntity e WHERE e.userName = :user");
        query.setParameter("user", username);
        
        try{
            return (EmployeeEntity)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee does not exist!");
        }
    }
    
    @Override
    public EmployeeEntity doLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            EmployeeEntity employee = retrieveEmployeeByUsername(username);
            if(employee.getPassword().equals(password)) {
                return employee;
            } else {
                throw new InvalidLoginCredentialException("Wrong password input!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist!");
        }
        
    }
}
