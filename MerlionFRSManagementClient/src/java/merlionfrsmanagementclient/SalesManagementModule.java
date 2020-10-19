/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merlionfrsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.SeatsInventorySessionBeanRemote;
import entity.EmployeeEntity;

/**
 *
 * @author Ong Bik Jeun
 */
public class SalesManagementModule {
    private EmployeeEntity currentEmployee;
    private SeatsInventorySessionBeanRemote seatsInventorySessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;

    public SalesManagementModule(EmployeeEntity currentEmployee, SeatsInventorySessionBeanRemote seatsInventorySessionBean, ReservationSessionBeanRemote reservationSessionBean) {
        this.currentEmployee = currentEmployee;
        this.seatsInventorySessionBean = seatsInventorySessionBean;
        this.reservationSessionBean = reservationSessionBean;
    }

    public void mainMenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
