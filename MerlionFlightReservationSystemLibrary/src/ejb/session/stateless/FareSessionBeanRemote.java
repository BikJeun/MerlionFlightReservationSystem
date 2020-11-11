/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FareEntity;
import exceptions.FareNotFoundException;
import exceptions.UpdateFareException;
import java.math.BigDecimal;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface FareSessionBeanRemote {

    public FareEntity updateFare(long fareID, BigDecimal newCost) throws FareNotFoundException, UpdateFareException;
}
