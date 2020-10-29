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
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import enumeration.EmployeeAccessRightEnum;
import enumeration.ScheduleTypeEnum;
import exceptions.AircraftConfigNotFoundException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.FlightScheduleExistException;
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFlightException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
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
                        flightSchedulePlanMenu();
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
        FlightEntity flight;
        long chosenRoute, chosenConfig;
        String flightNum;
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Create a new flight ***");
        while (true) {
            try {
                System.out.print("Enter Flight Number> ");
                flightNum = "ML" + String.valueOf(Integer.parseInt(sc.nextLine().trim())); // not sure if must enforce MLxxx
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number!");
            }
        }
        List<FlightRouteEntity> routes = flightRouteSessionBean.retrieveAllFlightRouteInOrder();
        if(routes.isEmpty()) {
            System.out.println("No flight route existing!\nPlease create flight route first!");
        } else {
            System.out.printf("%20s%40s%20s%40s%25s\n", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA");
            for(FlightRouteEntity rte : routes) {
                System.out.printf("%20s%40s%20s%40s%25s\n", rte.getFlightRouteID().toString(), rte.getOrigin().getAirportName() ,rte.getOrigin().getIATACode(), rte.getDestination().getAirportName() ,rte.getDestination().getIATACode());
            }
            System.out.print("Enter Flight Route (BY ID)>  ");
            chosenRoute = sc.nextLong();
            sc.nextLine();
            
            
            List<AircraftConfigurationEntity> aircraftConfig = aircraftConfigurationSessionBean.retrieveAllConfiguration();
            if(aircraftConfig.isEmpty()) {
                System.out.println("No Aircraft Configuration created!\nPlease create a configuration first!");
            } else {
                System.out.printf("%30s%20s%25s%20s\n", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type");
                
                for (AircraftConfigurationEntity config : aircraftConfig) {
                    System.out.printf("%30s%20s%25s%20s\n", config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName());
                }
                System.out.print("Enter Aircraft Configuration (BY ID)>  ");
                chosenConfig = sc.nextLong();
                sc.nextLine();
                
                try {
                    flight = new FlightEntity(flightNum);
                    flight = flightSessionBean.createNewFlight(flight, chosenRoute, chosenConfig);
                } catch (UnknownPersistenceException | FlightRouteNotFoundException | AircraftConfigNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n\n");
                    return;
                } catch (FlightExistException ex) {
                    try {
                        flight = flightSessionBean.enableFlight(flightNum, chosenRoute, chosenConfig);
                        System.out.println("Previous disabled flight found!\nRe-enabling flight...");
                    } catch (FlightNotFoundException ex1) {
                        System.out.println("Error: Flight " + flightNum + " already exists\nPlease try again!\n\n");
                        return;
                    }
                }
                System.out.println("Flight created successfully!");
                
                FlightRouteEntity flightRoute;
                try {
                    flightRoute = flightRouteSessionBean.retreiveFlightRouteById(chosenRoute);
                } catch (FlightRouteNotFoundException ex) {
                    return; // will never hit this
                }
                if(flightRoute.getComplementaryRoute() != null) {
                    System.out.print("Complementary route found!\nWould you like to create a complementary return flight? (Y/N)> ");
                    if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                        while (true) {
                            String returnFlightNum;
                            while (true) {
                                try {
                                    System.out.print("Enter return flight number > ");
                                    returnFlightNum = "ML" + String.valueOf(Integer.parseInt(sc.nextLine().trim()));
                                    break;
                                } catch (NumberFormatException ex) {
                                    System.out.println("Please enter a valid number!");
                                }
                            }
                            FlightRouteEntity returnFlightRoute = flightRoute.getComplementaryRoute();
                            try {
                                FlightEntity returnFlight = new FlightEntity(returnFlightNum);
                                returnFlight = flightSessionBean.createNewFlight(returnFlight, returnFlightRoute.getFlightRouteID(), chosenConfig);
                                flightSessionBean.associateExistingTwoWayFlights(flight.getFlightID(), returnFlight.getFlightID());
                                System.out.println("Return flight created!\n\n");
                                break;
                            } catch (UnknownPersistenceException | FlightNotFoundException | FlightRouteNotFoundException | AircraftConfigNotFoundException ex) {
                                System.out.println("Error: " + ex.getMessage() + "\nPlease try again!"); // will never hit this
                            } catch (FlightExistException ex) {
                                try {
                                    flight = flightSessionBean.enableFlight(returnFlightNum, returnFlightRoute.getFlightRouteID(), chosenConfig);
                                    System.out.println("Previous disabled return flight found!\nRe-enabling flight...");
                                    break;
                                } catch (FlightNotFoundException ex1) {
                                    System.out.println("Error: Flight " + flightNum + " already exists\nPlease try again!\n\n");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void doViewAllFlight() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View All Flights ***");
        System.out.printf("%10s%20s%20s%25s\n", "Flight ID", "Flight Number", "Flight Route", "Aircraft Configuration");
        
        List<FlightEntity> list = flightSessionBean.retrieveAllFlight();
        for(FlightEntity flight : list) {
            System.out.printf("%10s%20s%20s%25s\n", flight.getFlightID() ,flight.getFlightNum(), flight.getFlightRoute().getOrigin().getIATACode() + " -> " + flight.getFlightRoute().getDestination().getIATACode() , flight.getAircraftConfig().getName());
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
            
            System.out.printf("%10s%20s%20s%35s%20s%35s%25s%30s%20s%25s%20s%30s\n", "Flight ID", "Flight Number", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type", "Returning Flight Number");
            System.out.printf("%10s%20s%20s%35s%20s%35s%25s%30s%20s%25s%20s%30s\n", flight.getFlightID(), flight.getFlightNum(), route.getFlightRouteID().toString(), route.getOrigin().getAirportName() ,route.getOrigin().getIATACode(), route.getDestination().getAirportName() ,route.getDestination().getIATACode(), config.getAircraftConfigID().toString(), config.getName(), config.getNumberOfCabinClasses(), config.getAircraftType().getTypeName(), flight.getReturningFlight() != null ? flight.getReturningFlight().getFlightNum(): "None");
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
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n\n");
        }
    }
    
    private void doUpdateFlight(FlightEntity flight) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Update Flight ***");
        
        System.out.print("Enter Flight Number (blank if no change)> ");
        String flightNum = sc.nextLine().trim();
        if(flightNum.length() > 0) {
            flight.setFlightNum("ML" + flightNum);
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
                if (flightRoute.getOrigin() == flight.getFlightRoute().getOrigin() && flightRoute.getDestination() == flight.getFlightRoute().getDestination()) {
                }
                else {
                    flight.setFlightRoute(flightRoute);
                    flight.setReturningFlight(null);
                }
            } catch (FlightRouteNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n\n");
                return;
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
                System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n\n");
                return;
            }
        }
        
        Set<ConstraintViolation<FlightEntity>>constraintViolations = validator.validate(flight);
        if(constraintViolations.isEmpty()) {
            try {
                flightSessionBean.updateFlight(flight);
                System.out.println("Flight Updated Successfully");
            } catch (FlightNotFoundException | UpdateFlightException | InputDataValidationException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            showInputDataValidationErrorsForFlightEntity(constraintViolations);
        }
        
    }
    
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
                System.out.println(ex.getMessage());
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
    
    private void flightSchedulePlanMenu() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Flight Operation Module :: Flight Schedule Plan ===\n");
            System.out.println("1: Create Flight Schedule Plan");
            System.out.println("2: View All Flight Schedule Plan");
            System.out.println("3: View Flight Schedule Plan Details");
            System.out.println("4: Exit\n");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    doCreateFlightSchedulePlan();
                } else if(response == 2) {
                    //doViewAllFlightSchedulePlan();
                } else if(response == 3) {
                    //doViewFlightSchedulePlanDetails();
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
    
    private void doCreateFlightSchedulePlan() {
        try {
            Scanner sc = new Scanner(System.in);
            FlightSchedulePlanEntity plan = new FlightSchedulePlanEntity();
            SimpleDateFormat recurrentInputFormat = new SimpleDateFormat("dd/M/yyyy");
            List<Pair<Date, Integer>> info = new ArrayList<>();
            Pair<Date, Integer> pair = null;
            
            System.out.println("*** Create a new flight schedule plan ***");
            System.out.printf("%10s%20s%20s%25s\n", "Flight ID", "Flight Number", "Flight Route", "Aircraft Configuration");
            
            List<FlightEntity> list = flightSessionBean.retrieveAllFlight();
            if(list.isEmpty()) {
                System.out.println("No flight existing!\nPlease create flight first!");
            } else {
                for(FlightEntity flight : list) {
                    System.out.printf("%10s%20s%20s%25s\n", flight.getFlightID() ,flight.getFlightNum(), flight.getFlightRoute().getOrigin().getIATACode() + " -> " + flight.getFlightRoute().getDestination().getIATACode() , flight.getAircraftConfig().getName());
                }
                System.out.print("Enter flight to create a schedule plan for (BY ID)> ");
                FlightEntity flight = flightSessionBean.retreiveFlightById(sc.nextLong());
                sc.nextLine();
                plan.setFlight(flight);
                plan.setFlightNum(flight.getFlightNum());
                
                System.out.println("Enter Schedule Type (1: Single 2: Multiple 3:Recurrent Daily 4:Recurrent weekly)>  ");
                int typeInput = sc.nextInt();
                System.out.println("Create new flight schedule for flight " + flight.getFlightNum());
                switch(typeInput) {
                    case 1:
                        plan.setTypeExistingInPlan(ScheduleTypeEnum.SINGLE);
                        pair = getFlightScheduleInfo();
                        break;
                    case 2:
                        plan.setTypeExistingInPlan(ScheduleTypeEnum.MULTIPLE);
                        System.out.print("Enter number of schedule to be created> ");
                        int num = sc.nextInt();
                        for(int i = 0; i < num; i++) {
                            Pair pair1 = getFlightScheduleInfo();
                            info.add(pair1);
                        }
                        break;
                    case 3:
                        plan.setTypeExistingInPlan(ScheduleTypeEnum.RECURRENTDAY);
                        pair = getFlightScheduleInfo();
                        System.out.print("Enter recurrent end date (dd/mm/yyyy)> "); //something wrong here... not getting input then say parseerror
                        String date = sc.nextLine().trim();
                        Date dailyEnd = recurrentInputFormat.parse(date);
                        plan.setRecurrentEndDate(dailyEnd);
                        
                        break;
                    case 4:
                        plan.setTypeExistingInPlan(ScheduleTypeEnum.RECURRENTWEEK);
                        pair = getFlightScheduleInfo();
                        System.out.print("Enter recurrent end date (dd/mm/yyyy)> "); //something wrong here... not getting input then say parseerror
                        String date1 = sc.nextLine().trim();
                        Date weekEnd = recurrentInputFormat.parse(date1);
                        plan.setRecurrentEndDate(weekEnd);
                        break;
                }
                
                Set<ConstraintViolation<FlightSchedulePlanEntity>>constraintViolations = validator.validate(plan);
                if(constraintViolations.isEmpty()) {
                    if(plan.getTypeExistingInPlan().equals(ScheduleTypeEnum.MULTIPLE)) {
                        try {
                            plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(plan, info);
                        } catch (FlightSchedulePlanExistException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if(plan.getTypeExistingInPlan().equals(ScheduleTypeEnum.RECURRENTDAY)) {
                        System.out.println("Enter interval of recurrent (1-6)> ");
                        int days = sc.nextInt();
                        try {
                            plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(plan, pair, days);
                        } catch (FlightSchedulePlanExistException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if(plan.getTypeExistingInPlan().equals(ScheduleTypeEnum.RECURRENTWEEK)) {
                        try {
                            plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(plan, pair, 7);
                        } catch (FlightSchedulePlanExistException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else {
                        try {
                            plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(plan, pair, 0);
                        } catch (FlightSchedulePlanExistException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    System.out.println("New Flight Schedule Plan for Flight " + plan.getFlightNum() + " created successfully!");
                    flightSchedulePlanSessionBean.associateFlightToPlan(plan.getFlight().getFlightID(), plan.getFlightSchedulePlanID());
                } else {
                    showInputDataValidationErrorsForSchedulePlanEntity(constraintViolations);
                }
            }
        } catch (FlightNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        } catch (UnknownPersistenceException ex) {
            System.out.println(ex.getMessage());
        } catch (InputDataValidationException ex) {
            System.out.println(ex.getMessage());
        } catch (FlightScheduleExistException ex) {
            System.out.println(ex.getMessage());
        } catch (FlightSchedulePlanNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    private Pair<Date, Integer> getFlightScheduleInfo() {
        Date departure = null;
        int duration = 0;
        try {
            Scanner sc = new Scanner(System.in);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss a");
            SimpleDateFormat outputFormatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss a");
            
            System.out.print("Enter departure Date and Time (dd/mm/yyy hh:mm:ss AM/PM)> ");
            String input = sc.nextLine().trim();
            departure = formatter.parse(input);
            System.out.print("Enter estimated flight duration> ");
            duration = sc.nextInt();
            //return new Pair<>(departure, duration);
        } catch (ParseException ex) {
            System.out.println("Error with formatting date");
        }
        return new Pair<>(departure, duration);
    }
    
    private void showInputDataValidationErrorsForSchedulePlanEntity(Set<ConstraintViolation<FlightSchedulePlanEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }
        System.out.println("\nPlease try again......\n");
    }
}