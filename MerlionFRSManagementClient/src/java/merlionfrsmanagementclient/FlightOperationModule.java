/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsmanagementclient;

import ejb.session.stateless.AircraftConfigurationSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.EmployeeEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import enumeration.EmployeeAccessRightEnum;
import exceptions.AircraftConfigNotFoundException;
import exceptions.DeleteFlightException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFlightException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Ong Bik Jeun
 */
public class FlightOperationModule {
    
    private EmployeeEntity currentEmployee;
    private FlightSessionBeanRemote flightSessionBean;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBean;
    private AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public FlightOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public FlightOperationModule(EmployeeEntity currentEmployee, FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean, AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean) {
        this();
        this.currentEmployee = currentEmployee;
        this.flightSessionBean = flightSessionBean;
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.aircraftConfigurationSessionBean = aircraftConfigurationSessionBean;
    }
    
    public void mainMenu() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Flight Operating Module ===\n");
            System.out.println("Which subsystem do you want to access?");
            System.out.println("1: Flight Subsystem");
            System.out.println("2: Flight Schedule Plan Subsystem");
            System.out.println("3: Exit\n");
            
            response = 0;
            while(response < 1 || response > 3) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.SCHEDULEMANAGER)) {
                        flightMenu();
                    } else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                } else if (response == 2) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.SCHEDULEMANAGER)) {
                        //flightSchedulePlanMenu();
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
    
    private void flightMenu() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Flight Operation Module :: Flight ===\n");
            System.out.println("1: Create Flight");
            System.out.println("2: View All Flight");
            System.out.println("3: View Flight Details");
            System.out.println("4: Exit\n");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    doCreateFlight();
                } else if(response == 2) {
                    doViewAllFlight();
                } else if(response == 3) {
                    doViewFlightDetails();
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
    
    private void doCreateFlight() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Create a new flight ***");
            
            System.out.print("Enter Flight Number> ");
            String flightNum = "ML" + sc.nextLine().trim();
            
            List<FlightRouteEntity> routes = flightRouteSessionBean.retrieveAllFlightRouteInOrder();
            System.out.printf("%20s%40s%20s%40s%25s\n", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA");
            for(FlightRouteEntity rte : routes) {
                System.out.printf("%20s%40s%20s%40s%25s\n", rte.getFlightRouteID().toString(), rte.getOrigin().getAirportName() ,rte.getOrigin().getIATACode(), rte.getDestination().getAirportName() ,rte.getDestination().getIATACode());
            }
            System.out.print("Enter Flight Route (BY ID)>  ");
            Long chosenRoute = sc.nextLong();
            FlightRouteEntity flightRoute = flightRouteSessionBean.retreiveFlightRouteById(chosenRoute);
            sc.nextLine();
            
            List<AircraftConfigurationEntity> aircraftConfig = aircraftConfigurationSessionBean.retrieveAllConfiguration();
            System.out.printf("%30s%20s%25s%20s\n", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type");
            
            for(AircraftConfigurationEntity config : aircraftConfig) {
                System.out.printf("%30s%20s%25s%20s\n", config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName());
            }
            System.out.print("Enter Aircraft Configuration (BY ID)>  ");
            Long chosenConfig = sc.nextLong();
            sc.nextLine();
            
            FlightEntity flight = new FlightEntity(flightNum);
            flight = flightSessionBean.createNewFlight(flight, chosenRoute, chosenConfig);
            
            if(flightRoute.getComplementaryRoute() != null) {
                System.out.print("Would yout like to create a complementary flight as well? (Y/N)> ");
                if(sc.nextLine().trim().equalsIgnoreCase("Y")) {
                    System.out.print("Enter return flight number > ");
                    String returnFlightNum = "ML" + sc.nextLine().trim();
                    
                    FlightRouteEntity returnFlightRoute = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination(flightRoute.getDestination().getIATACode(), flightRoute.getOrigin().getIATACode());
                    FlightEntity returnFlight = new FlightEntity(returnFlightNum);
                    returnFlight = flightSessionBean.createNewFlight(returnFlight, returnFlightRoute.getFlightRouteID(), chosenConfig);
                    
                    flightSessionBean.associateExistingTwoWayFlights(flight.getFlightID(), returnFlight.getFlightID());
                }
            }
        } catch (FlightRouteNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (FlightExistException ex) {
            System.out.println(ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doViewAllFlight() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View All Flights ***");
        System.out.printf("%20s%20s%25s\n", "Flight Number", "Flight Route", "Aircraft Configuration");
        
        List<FlightEntity> list = flightSessionBean.retrieveAllFlight();
        for(FlightEntity flight : list) {
            System.out.printf("%20s%20s%25s\n", flight.getFlightNum(), flight.getFlightRoute().getFlightRouteID().toString(), flight.getAircraftConfig().getName());
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }
    
    private void doViewFlightDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            int response = 0;
            System.out.println("*** View Flight details ***");
            System.out.print("Enter Flight ID> ");
            Long id = sc.nextLong();
            
            FlightEntity flight = flightSessionBean.retreiveFlightById(id);
            FlightRouteEntity route = flight.getFlightRoute();
            AircraftConfigurationEntity config = flight.getAircraftConfig();
            
            System.out.printf("%20s%20s%35s%20s%35s%25s%30s%20s%25s%20s%30s\n", "Flight Number", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type", "Returning Flight Number");
            System.out.printf("%20s%20s%35s%20s%35s%25s%30s%20s%25s%20s%30s\n", flight.getFlightNum(), route.getFlightRouteID().toString(), route.getOrigin().getAirportName() ,route.getOrigin().getIATACode(), route.getDestination().getAirportName() ,route.getDestination().getIATACode(), config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName(), flight.getReturningFlight().getFlightNum());
            System.out.println("--------------------------");
            System.out.println("1: Update Flight");
            System.out.println("2: Delete Flight");
            System.out.println("3: Back\n");
            
            System.out.print("> ");
            response = sc.nextInt();
            
            if(response == 1) {
                doUpdateFlight(flight);
            }
            else if(response == 2) {
                doDeleteFlight(flight);
            }
        } catch (FlightNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doUpdateFlight(FlightEntity flight) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Update Flight ***");
        
        System.out.print("Enter Flight Number (blank if no change)> ");
        String flightNum = sc.nextLine().trim();
        if(flightNum.length() > 0) {
            flight.setFlightNum(flightNum);
        }
        
        List<FlightRouteEntity> routes = flightRouteSessionBean.retrieveAllFlightRouteInOrder();
        System.out.printf("%20s%40s%20s%40s%25s\n", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA");
        for(FlightRouteEntity rte : routes) {
            System.out.printf("%20s%40s%20s%40s%25s\n", rte.getFlightRouteID().toString(), rte.getOrigin().getAirportName() ,rte.getOrigin().getIATACode(), rte.getDestination().getAirportName() ,rte.getDestination().getIATACode());
        }
        System.out.print("Enter Flight Route (BY ID)(negative number if no change)>  ");
        Long chosenRoute = sc.nextLong();
        sc.nextLine();
        if(chosenRoute > 0) {
            try {
                FlightRouteEntity flightRoute = flightRouteSessionBean.retreiveFlightRouteById(chosenRoute);
                flight.setFlightRoute(flightRoute);
            } catch (FlightRouteNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        List<AircraftConfigurationEntity> aircraftConfig = aircraftConfigurationSessionBean.retrieveAllConfiguration();
        System.out.printf("%30s%20s%25s%20s\n", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type");
        for(AircraftConfigurationEntity config : aircraftConfig) {
            System.out.printf("%30s%20s%25s%20s\n", config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName());
        }
        System.out.print("Enter Aircraft Configuration (BY ID)(negative number if no change)>  ");
        Long chosenConfig = sc.nextLong();
        sc.nextLine();
        if(chosenConfig > 0) {
            try {
                AircraftConfigurationEntity config = aircraftConfigurationSessionBean.retriveAircraftConfigByID(chosenConfig);
                flight.setAircraftConfig(config);
            } catch (AircraftConfigNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        Set<ConstraintViolation<FlightEntity>>constraintViolations = validator.validate(flight);
        if(constraintViolations.isEmpty()) {
            try {
                flightSessionBean.updateFlight(flight);
                System.out.println("Flight Updated Successfully");
            } catch (FlightNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (UpdateFlightException ex) {
                System.out.println(ex.getMessage());
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            showInputDataValidationErrorsForFlightEntity(constraintViolations);
        }
        
    }
    
    //JUN HAO SOS PLS
    private void doDeleteFlight(FlightEntity flight) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Delete Flight ***");
        System.out.printf("Confirm delete flight %s (Flight ID: %d) (Enter Y/N)> ", flight.getFlightNum(), flight.getFlightID());
        String reply = sc.nextLine().trim();
        
        if(reply.equalsIgnoreCase("Y")) {
            try {
                flightSessionBean.deleteFlight(flight.getFlightID());
                System.out.println("Flight Deleted Succesfully!\n");
            } catch (FlightNotFoundException ex) {
                Logger.getLogger(FlightOperationModule.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DeleteFlightException ex) {
                Logger.getLogger(FlightOperationModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void showInputDataValidationErrorsForFlightEntity(Set<ConstraintViolation<FlightEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
        
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }
        
        System.out.println("\nPlease try again......\n");
    }

    
}
