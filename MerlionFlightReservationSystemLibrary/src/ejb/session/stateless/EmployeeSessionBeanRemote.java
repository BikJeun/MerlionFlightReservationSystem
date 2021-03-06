/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidLoginCredentialException;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public EmployeeEntity retrieveEmployeeById(Long id) throws EmployeeNotFoundException;

    public EmployeeEntity doLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
