/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftTypeEntity;
import exceptions.AircraftTypeNotFoundException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Ong Bik Jeun
 */
@Remote
public interface AircraftTypeSessionBeanRemote {

    public AircraftTypeEntity retrieveAircraftTypeById(Long id) throws AircraftTypeNotFoundException;

    public List<AircraftTypeEntity> retrieveAllAircraftType();
}
