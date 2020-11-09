/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClassEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CabinClassNotFoundException;
import exceptions.CabinClassTypeEnumNotFoundException;
import javax.ejb.Remote;

/**
 *
 * @author Ong Bik Jeun
 */
@Remote
public interface CabinClassSessionBeanRemote {

    public CabinClassTypeEnum findEnumType(String trim) throws CabinClassTypeEnumNotFoundException;
    
    public CabinClassEntity retrieveCabinByID(Long cabinClassID) throws CabinClassNotFoundException;
    
}
