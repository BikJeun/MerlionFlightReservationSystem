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
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface CustomerSessionBeanLocal {
 
    public CustomerEntity createNewCustomerEntity(CustomerEntity customer) throws UnknownPersistenceException, CustomerExistException;
    
    public CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotFoundException;

    public CustomerEntity doLogin(String username, String password) throws InvalidLoginCredentialException;
}
