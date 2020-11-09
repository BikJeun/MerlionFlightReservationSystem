/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.UserEntity;
import exceptions.UserNotFoundException;
import javax.ejb.Local;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface UserSessionBeanLocal {

    public UserEntity retrieveUserById(Long userID) throws UserNotFoundException;
    
}
