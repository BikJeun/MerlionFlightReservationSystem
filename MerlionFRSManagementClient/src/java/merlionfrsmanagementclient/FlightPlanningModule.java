/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsmanagementclient;

import ejb.session.stateless.AircraftConfigurationSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.CabinClassEntity;
import entity.EmployeeEntity;
import enumeration.CabinClassTypeEnum;
import enumeration.EmployeeAccessRightEnum;
import exceptions.AircraftConfigExistException;
import exceptions.AircraftTypeNotFoundException;
import exceptions.CabinClassExistException;
import exceptions.CabinClassTypeEnumNotFoundException;
import exceptions.UnknownPersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ong Bik Jeun
 */
public class FlightPlanningModule {
    
    private EmployeeEntity employee;
    private AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBean;
    private CabinClassSessionBeanRemote cabinClassSessionBean;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBean;

    public FlightPlanningModule(EmployeeEntity employee, AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean, CabinClassSessionBeanRemote cabinClassSessionBean, AircraftTypeSessionBeanRemote aircraftTypeSessionBean) {
        this.employee = employee;
        this.aircraftConfigurationSessionBean = aircraftConfigurationSessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.cabinClassSessionBean = cabinClassSessionBean;
        this.aircraftTypeSessionBean = aircraftTypeSessionBean;
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
                    aircraftConfigMenu();
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

    private void aircraftConfigMenu() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Flight Planning Module :: Aircraft Configuration ===\n");
            System.out.println("1: Create Aircraft Configuration");
            System.out.println("2: View All Aircraft Configuration");
            System.out.println("3: View Aircraft Cofiguration Details");
            System.out.println("4: Exit\n");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    doCreateAircraftConfig();
                } else if(response == 2) {
                    //doViewAllAircraftConfig();
                } else if(response == 3) {
                    //doViewAircraftConfigDetails();
                } else if(response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if(response == 4) {
                break;
            }
        }  
    }

    private void doCreateAircraftConfig() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Create a new aircraft configuration ***");
            System.out.print("Enter aircraft type (1: Boeing 737, 2: Boeing 747)> ");
            Long type = sc.nextLong();
            sc.nextLine();
            AircraftTypeEntity boeing = aircraftTypeSessionBean.retrieveAircraftTypeById(type);
            System.out.print("Enter name> ");
            String name = sc.nextLine().trim();
            System.out.print("Enter number of cabin class (1-4 only)> \n");
            int cabinNum = sc.nextInt();
            
            AircraftConfigurationEntity aircraftConfig = new AircraftConfigurationEntity(boeing, name, cabinNum);
            aircraftConfig = aircraftConfigurationSessionBean.createNewAircraftConfig(aircraftConfig);
            
            for(int i = 0; i<cabinNum; i++) {
                doCreateCabinClass(aircraftConfig);
            }
            int maxCapacity = aircraftConfigurationSessionBean.calculateMaxCapacity(aircraftConfig);
            
            //need to check that maxCapcity <= max seat capacity of the aircraft type
            if(type == 1 && maxCapacity <= 189) {
                System.out.println("Aircraft Configuration created for a Boeing 737 Type plane");
                aircraftConfigurationSessionBean.associateTypeWithConfig(Long.valueOf(1), aircraftConfig.getAircraftConfigID());
            } else if(type == 2 && maxCapacity <=416) {
                System.out.println("Aircraft Configuration created for a Boeing 747 Type plane");
                aircraftConfigurationSessionBean.associateTypeWithConfig(Long.valueOf(2), aircraftConfig.getAircraftConfigID());
            } else {
                //set a rollback to delete all the creation made????
                System.out.println("Invalid Configuration");
            }
        } catch (AircraftConfigExistException ex) {
            System.out.println(ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        } catch (AircraftTypeNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
            
            
        }
    

    private void doCreateCabinClass(AircraftConfigurationEntity aircraftConfig) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Create a new cabin class ***");
            System.out.print("Enter class - First Class(F)/Buisiness Class(J)/Premium Economy Class(W)/ Economy Class(Y)> ");
            CabinClassTypeEnum type = cabinClassSessionBean.findEnumType(sc.nextLine().trim());
            System.out.print("Enter number of aisles> ");
            int aisles = sc.nextInt();
            System.out.print("Enter number of rows> ");
            int rows = sc.nextInt();
            System.out.print("Enter number of seats abreast> ");
            int seatsAbreast = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter seating configuration per column (x-y-z format)> ");
            String config = sc.nextLine().trim();
            
            int maxCapacity = cabinClassSessionBean.computeMaxSeatCapacity(rows, seatsAbreast);
            
            CabinClassEntity cabin = new CabinClassEntity(type, aisles, rows, seatsAbreast, config, maxCapacity);
            cabin = cabinClassSessionBean.createNewCabinClass(cabin);
            cabinClassSessionBean.associateAircraftConfigToCabin(aircraftConfig.getAircraftConfigID(), cabin.getCabinClassID());
        } catch (CabinClassTypeEnumNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (CabinClassExistException ex) {
            System.out.println(ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
   
    
    
    
    
}
