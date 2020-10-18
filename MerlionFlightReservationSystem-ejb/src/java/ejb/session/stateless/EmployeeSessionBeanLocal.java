/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import exceptions.EmployeeNotFoundException;
import exceptions.EmployeeUsernameExistException;
import exceptions.UnknownPersistenceException;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface EmployeeSessionBeanLocal {

    public EmployeeEntity createNewEmployee(EmployeeEntity employee) throws EmployeeUsernameExistException, UnknownPersistenceException;
    
    public EmployeeEntity retrieveEmployeeById(Long id) throws EmployeeNotFoundException; 

    public EmployeeEntity retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;
}
