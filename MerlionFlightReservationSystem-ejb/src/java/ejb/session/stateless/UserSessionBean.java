/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.UserEntity;
import exceptions.UserNotFoundException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mitsuki
 */
@Stateless
public class UserSessionBean implements UserSessionBeanRemote, UserSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;

    public UserSessionBean() {
    }

    @Override
    public UserEntity retrieveUserById(Long userID) throws UserNotFoundException {
        UserEntity user = em.find(UserEntity.class, userID);
        
        if(user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with " + userID + " does not exist!");
        }
    }



    
}
