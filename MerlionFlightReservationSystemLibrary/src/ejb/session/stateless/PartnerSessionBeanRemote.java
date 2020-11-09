/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEntity;
import exceptions.PartnerNotFoundException;
import javax.ejb.Remote;

/**
 *
 * @author Ooi Jun Hao
 */
@Remote
public interface PartnerSessionBeanRemote {

    public PartnerEntity retrievePartnerById(Long id) throws PartnerNotFoundException;
    
    public PartnerEntity retrievePartnerByUsername(String username) throws PartnerNotFoundException;
    
}
