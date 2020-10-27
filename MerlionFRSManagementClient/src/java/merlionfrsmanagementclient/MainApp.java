/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsmanagementclient;

import ejb.session.stateless.AircraftConfigurationSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.SeatsInventorySessionBeanRemote;
import entity.EmployeeEntity;
import enumeration.EmployeeAccessRightEnum;
import exceptions.InvalidLoginCredentialException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Ong Bik Jeun
 */
public class MainApp {
  
    private AirportSessionBeanRemote airportSessionBean;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBean;
    private CabinClassSessionBeanRemote cabinClassSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private SeatsInventorySessionBeanRemote seatsInventorySessionBean;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;
    private FlightSessionBeanRemote flightSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBean;
    private AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    
    
    private boolean login = false;
    private EmployeeEntity currentEmployee;
    
    private FlightOperationModule flightOperationModule;
    private FlightPlanningModule flightPlanningModule;
    private SalesManagementModule salesManagementModule;
    
    public MainApp(AirportSessionBeanRemote airportSessionBean, AircraftTypeSessionBeanRemote aircraftTypeSessionBean, CabinClassSessionBeanRemote cabinClassSessionBean, ReservationSessionBeanRemote reservationSessionBean, SeatsInventorySessionBeanRemote seatsInventorySessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightSessionBeanRemote flightSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean, AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean, EmployeeSessionBeanRemote employeeSessionBean) {
        this.airportSessionBean = airportSessionBean;
        this.aircraftTypeSessionBean = aircraftTypeSessionBean;
        this.cabinClassSessionBean = cabinClassSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.seatsInventorySessionBean = seatsInventorySessionBean;
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightSessionBean = flightSessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.aircraftConfigurationSessionBean = aircraftConfigurationSessionBean;
        this.employeeSessionBean = employeeSessionBean;
    }
    
    public void runApp() {
        while (true) {
            if(!login) {
                Scanner sc = new Scanner(System.in);
                Integer response = 0;
                
                System.out.println("=== Welcome to Merlion Flight Reservation System ===\n");
                System.out.println("1: Login");
                System.out.println("2: Exit\n");
                
                response = 0;
                while(response < 1 || response > 2) {
                    System.out.print("> ");
                    response = sc.nextInt();
                    if(response == 1) {
                        try {
                            doLogin();
                            System.out.println("Login Successful!\n");
                            login = true;
                            flightOperationModule = new FlightOperationModule(currentEmployee, flightSessionBean, flightSchedulePlanSessionBean, flightRouteSessionBean, aircraftConfigurationSessionBean);
                            flightPlanningModule = new FlightPlanningModule(currentEmployee, airportSessionBean, aircraftConfigurationSessionBean, flightRouteSessionBean, cabinClassSessionBean, aircraftTypeSessionBean);
                            salesManagementModule = new SalesManagementModule(currentEmployee, seatsInventorySessionBean, reservationSessionBean);
                            mainMenu();
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println();
                        }
                    } else if (response == 2) {
                        break;
                    } else {
                        System.out.println("Invalid input, please try again!\n");
                    }
                }
                if(response == 2) {
                    break;
                }
            } else {
                mainMenu();
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in) ;
        System.out.println("*** Merlion Flight Reservation System :: LOGIN ***\n");
        System.out.print("Enter username> ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        String password = sc.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0) {
            currentEmployee = employeeSessionBean.doLogin(username, password);
            //login = true;
        } else {
            throw new InvalidLoginCredentialException("Missing Login Credentials");
        }
    }
    
    private void mainMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(login) {
            System.out.println("*** Merlion Flight Reservation System ***\n");
            System.out.println("You are currently logged in as " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + " with " + currentEmployee.getAccessRight().toString() + " rights!\n");
            System.out.println("*** Select Module To Access ***");
            System.out.println("1: Flight Operation Module");
            System.out.println("2: Flight Planning Module");
            System.out.println("3: Sales Management Module");
            System.out.println("4: Log Out\n");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.SCHEDULEMANAGER)) {
                        flightOperationModule.mainMenu();
                    } else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                } else if(response == 2) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.FLEETMANAGER) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ROUTEPLANNER)) {
                        flightPlanningModule.mainMenu();
                    }else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                    
                } else if (response == 3) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.SALESMANAGER)) {
                        salesManagementModule.mainMenu();
                    }else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                } else if (response == 4) {
                    doLogOut();
                    System.out.println("Log out successful.\n");
                    break;
                } else {
                    System.out.println("Invalid Option, please try again!");
                }
            }
            
            if(response == 4) {
                break;
            }
        }
    }
    
    private void doLogOut() {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Are you sure you want to log out? (Y or N)> ");
        String reply = sc.nextLine().trim();
        
        if((reply.equals("Y") || reply.equals("y")) && login) {
            currentEmployee = null;
            login = false;
        }
    }
}
