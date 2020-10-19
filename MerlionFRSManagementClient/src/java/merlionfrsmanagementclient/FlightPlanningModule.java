/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsmanagementclient;

import ejb.session.stateless.AircraftConfigurationSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.EmployeeEntity;
import enumeration.EmployeeAccessRightEnum;
import java.util.Scanner;

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
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Flight Planning Module ===\n");
            System.out.println("Which subsystem do you want to access?");
            System.out.println("1: Aircraft Configuration Subsystem");
            System.out.println("2: Flight Route Subsystem");
            System.out.println("3: Exit\n");
            
            response = 0;
            while(response < 1 || response > 3) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    if(employee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || employee.getAccessRight().equals(EmployeeAccessRightEnum.FLEETMANAGER)) {
                    //aircraftConfigMenu();
                    } else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                } else if (response == 2) {
                    if(employee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || employee.getAccessRight().equals(EmployeeAccessRightEnum.ROUTEPLANNER)) {
                    //flightRouteMenu();
                    } else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                } else if(response == 3) {
                    break;
                } else {
                    System.out.println("Invalid input, please try again!\n");
                }
                
            }
            if(response == 3) {
                break;
            }
        }
    }
    
   
    
    
    
    
}
