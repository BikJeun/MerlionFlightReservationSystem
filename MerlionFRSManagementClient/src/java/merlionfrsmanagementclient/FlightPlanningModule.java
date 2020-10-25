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
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.CabinClassEntity;
import entity.EmployeeEntity;
import entity.FlightRouteEntity;
import enumeration.CabinClassTypeEnum;
import enumeration.EmployeeAccessRightEnum;
import exceptions.AircraftConfigExistException;
import exceptions.AircraftConfigNotFoundException;
import exceptions.AircraftTypeNotFoundException;
import exceptions.AirportNotFoundException;
import exceptions.CabinClassExistException;
import exceptions.CabinClassTypeEnumNotFoundException;
import exceptions.CreateNewAircraftConfigException;
import exceptions.FlightRouteExistException;
import exceptions.FlightRouteNotFoundException;
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
    private AirportSessionBeanRemote airportSessionBean;
    private AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBean;
    private CabinClassSessionBeanRemote cabinClassSessionBean;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBean;

    public FlightPlanningModule(EmployeeEntity employee, AirportSessionBeanRemote airportSessionBean, AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean, CabinClassSessionBeanRemote cabinClassSessionBean, AircraftTypeSessionBeanRemote aircraftTypeSessionBean) {
        this.employee = employee;
        this.airportSessionBean = airportSessionBean;
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
                        flightRouteMenu();
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
                    doViewAllAircraftConfig();
                } else if(response == 3) {
                    doViewAircraftConfigDetails();
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
                       
            List<CabinClassEntity> cabinClasses = new ArrayList<>();
            for(int i = 0; i<cabinNum; i++) {
                cabinClasses.add(doCreateCabinClass(aircraftConfig));
            }
            aircraftConfig = aircraftConfigurationSessionBean.createNewAircraftConfig(aircraftConfig, cabinClasses);
            System.out.println("Aircraft Configuration created for a " + aircraftConfig.getAircraftType().getTypeName() + " Type plane\n");
        } catch (AircraftTypeNotFoundException | CreateNewAircraftConfigException | UnknownPersistenceException| AircraftConfigExistException ex) {
            System.out.println("Error occured in creating Aircraft Configuration: " + ex.getMessage());   
            System.out.println("Please try again!"); 
        } 
    }
    

    private CabinClassEntity doCreateCabinClass(AircraftConfigurationEntity aircraftConfig) {
        CabinClassEntity cabin = null;
       
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Create a new cabin class ***");
        CabinClassTypeEnum type = null;
        int aisles, rows, seatsAbreast = 0;
        String config = null;
        while (true) {
            try {
            System.out.print("Enter class - First Class(F)/Buisiness Class(J)/Premium Economy Class(W)/ Economy Class(Y)> ");
            type = cabinClassSessionBean.findEnumType(sc.nextLine().trim());
            break;
            } catch (CabinClassTypeEnumNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        while (true) {
            try {
                System.out.print("Enter number of aisles (0 to 2 only)> "); // enforce between 0 and 2? 
                aisles = Integer.parseInt(sc.nextLine().trim());
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number!");
            }
        }
        while (true) {
            try {
                System.out.print("Enter number of rows> ");
                rows = Integer.parseInt(sc.nextLine().trim());
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number!");
            }
        }
        while (true) {
            try {
                System.out.print("Enter number of seats abreast> ");
                seatsAbreast = Integer.parseInt(sc.nextLine().trim());
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number!");
            }
        }
        while (true) {
            if (aisles == 0) {
                config = String.valueOf(seatsAbreast);
                break;
            }
            switch (aisles) {
                case 1: System.out.print("Enter seating configuration per column (x-y format)> ");
                    break;
                case 2:  System.out.print("Enter seating configuration per column (x-y-z format)> ");
                    break;
            }
            config = sc.nextLine().trim();
            if (aisles == 1) {
                if (config.length() == 3 && Character.isDigit(config.charAt(0)) && Character.isDigit(config.charAt(2)) && config.charAt(1) == '-') {
                    if (Character.getNumericValue(config.charAt(0)) + Character.getNumericValue(config.charAt(2)) == seatsAbreast) { 
                        break;
                    }
                    System.out.println("Please ensure total seats in seating configuration is equals to the seats abreast declared!");
                    continue;
                }
                System.out.println("Please enter in the correct format!");
            }
            else if (aisles == 2) {
                if (config.length() == 5 && Character.isDigit(config.charAt(0)) && Character.isDigit(config.charAt(2)) && Character.isDigit(config.charAt(4)) && config.charAt(1) == '-' && config.charAt(3) == '-') {
                    if (Character.getNumericValue(config.charAt(0)) + Character.getNumericValue(config.charAt(2)) + Character.getNumericValue(config.charAt(4)) == seatsAbreast) { 
                        break;
                    }
                    System.out.println("Please ensure total seats in seating configuration is equals to the seats abreast declared!");
                    continue;
                }
                System.out.println("Please enter in the correct format!");
            }
            else {
                break; //should not reach here unless inputted aisles is out of range (due to no enforcement above)
            }
        }

        int maxCapacity = cabinClassSessionBean.computeMaxSeatCapacity(rows, seatsAbreast);
        cabin = new CabinClassEntity(aircraftConfig, type, aisles, rows, seatsAbreast, config, maxCapacity);
        return cabin;
    }

    private void doViewAllAircraftConfig() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View all aircraft configuration ***");
        List<AircraftConfigurationEntity> list = aircraftConfigurationSessionBean.retrieveAllConfiguration();
        System.out.printf("%30s%20s%25s%20s\n", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type");
        
        for(AircraftConfigurationEntity config : list) {
            System.out.printf("%30s%20s%25s%20s\n", config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName());
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

    private void doViewAircraftConfigDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** View aircraft configuration details ***");
            System.out.println("Enter configuration ID> ");
            Long id = sc.nextLong();
            
            AircraftConfigurationEntity config = aircraftConfigurationSessionBean.retriveAircraftConfigByID(id);
            System.out.printf("%30s%20s%25s%20s\n", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type");
            System.out.printf("%30s%20s%25s%20s\n", config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName());
        } catch (AircraftConfigNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void flightRouteMenu() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Flight Planning Module :: Flight Route ===\n");
            System.out.println("1: Create Flight Route");
            System.out.println("2: View All Flight Routes");
            System.out.println("3: Delete Flight Route");
            System.out.println("4: Exit\n");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    doCreateFlightRoute();
                } else if(response == 2) {
                    doViewAllFlightRoute();
                } else if(response == 3) {
                   // doViewAircraftConfigDetails();
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

    private void doCreateFlightRoute() {
        AirportEntity originAirport, destinationAirport;
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Create a new Flight Route ***");
        while (true) {
            try {
                System.out.print("Enter IATA code of origin airport> ");
                String origin = sc.nextLine().trim();
                originAirport = airportSessionBean.retrieveAirportByIATA(origin);
                break;
            } catch (AirportNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage() +"\nPlease input a valid IATA Code!");
            }
        }
        while (true) {
            try {
                System.out.print("Enter IATA code of destination airport> ");
                String destination = sc.nextLine().trim();
                destinationAirport = airportSessionBean.retrieveAirportByIATA(destination);
                break;
            } catch (AirportNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage() +"\nPlease input a valid IATA Code!");
            }
        }
        FlightRouteEntity flightRoute = new FlightRouteEntity(); 
        try {
            flightRoute = flightRouteSessionBean.createNewFlightRoute(flightRoute, originAirport.getAirportID(), destinationAirport.getAirportID());
        } catch (FlightRouteExistException | UnknownPersistenceException | AirportNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n\n");
            return;
        }
        System.out.print("Flight Route successfully created!\nWould you like to create its complementary return route? (Y or N)> ");
        String reply = sc.nextLine().trim();
        
        if((reply.equals("Y") || reply.equals("y"))) {
            try {          
               FlightRouteEntity returnFlightRoute = flightRouteSessionBean.createNewFlightRoute(new FlightRouteEntity(), destinationAirport.getAirportID(),originAirport.getAirportID());    
               flightRouteSessionBean.setComplementaryFlightRoute(returnFlightRoute.getFlightRouteID());
               System.out.println("Complementary return route created!\n\n");
            } catch (FlightRouteExistException ex) {
               System.out.println("Complementary return route already exists!\n\n"); 
                try {
                    flightRouteSessionBean.setComplementaryFlightRoute(flightRoute.getFlightRouteID());
                } catch (FlightRouteNotFoundException ex1) {
                    System.out.println("Error:" + ex1.getMessage() + "\n\n"); //will never hit this 
                }
            } catch (UnknownPersistenceException | AirportNotFoundException | FlightRouteNotFoundException ex) {
               System.out.println("Error:" + ex.getMessage() + "\n\n");
            } 
                     
        } 
    }

    private void doViewAllFlightRoute() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View all flight routes ***");
        List<FlightRouteEntity> list = flightRouteSessionBean.retrieveAllFlightRouteInOrder();
        System.out.printf("%20s%35s%20s%35s%25s\n", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA");
        
        for(FlightRouteEntity route : list) {
            System.out.printf("%20s%35s%20s%35s%25s\n", route.getFlightRouteID().toString(), route.getOrigin().getAirportName() ,route.getOrigin().getIATACode(), route.getDestination().getAirportName() ,route.getDestination().getIATACode());
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }
    
    
    
   
    
    
    
    
}
