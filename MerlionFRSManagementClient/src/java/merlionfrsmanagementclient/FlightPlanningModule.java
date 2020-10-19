/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsmanagementclient;

import ejb.session.stateless.AircraftConfigurationSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.EmployeeEntity;

/**
 *
 * @author Ong Bik Jeun
 */
public class FlightPlanningModule {
    
    private EmployeeEntity employee;
    private AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBean;

    public FlightPlanningModule(EmployeeEntity employee, AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean) {
        this.employee = employee;
        this.aircraftConfigurationSessionBean = aircraftConfigurationSessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
    }

    public void mainMenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
   
    
    
    
    
}
