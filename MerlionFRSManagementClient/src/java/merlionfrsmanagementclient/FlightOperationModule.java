/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsmanagementclient;

import ejb.session.stateless.AircraftConfigurationSessionBeanRemote;
import ejb.session.stateless.FareSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import entity.AircraftConfigurationEntity;
import entity.CabinClassEntity;
import entity.EmployeeEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import enumeration.EmployeeAccessRightEnum;
import enumeration.ScheduleTypeEnum;
import exceptions.AircraftConfigNotFoundException;
import exceptions.CabinClassNotFoundException;
import exceptions.FareExistException;
import exceptions.FareNotFoundException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateFlightException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private FareSessionBeanRemote fareSessionBean;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public FlightOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public FlightOperationModule(EmployeeEntity currentEmployee, FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBean, AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean, FareSessionBeanRemote fareSessionBean) {
        this();
        this.currentEmployee = currentEmployee;
        this.flightSessionBean = flightSessionBean;
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.aircraftConfigurationSessionBean = aircraftConfigurationSessionBean;
        this.fareSessionBean = fareSessionBean;
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
                
                if (response == 1) {
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
        
        while (true) {
            System.out.println("=== Flight Operation Module :: Flight ===\n");
            System.out.println("1: Create Flight");
            System.out.println("2: View All Flight");
            System.out.println("3: View Flight Details");
            System.out.println("4: Exit\n");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    doCreateFlight();
                } else if (response == 2) {
                    doViewAllFlight();
                } else if (response == 3) {
                    doViewFlightDetails();
                } else if (response == 4) {
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
        System.out.print("Enter Flight Number> ");
        flightNum = "ML" + sc.nextLine().trim(); // not sure if must enforce MLxxx
        List<FlightRouteEntity> routes;
        try {
            routes = flightRouteSessionBean.retrieveAllFlightRouteInOrder();
        } catch (FlightRouteNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease create flight route first!\n");
            return;
        }
        System.out.printf("%20s%40s%20s%40s%25s\n", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA");
        for(FlightRouteEntity rte : routes) {
            System.out.printf("%20s%40s%20s%40s%25s\n", rte.getFlightRouteID().toString(), rte.getOrigin().getAirportName() ,rte.getOrigin().getIATACode(), rte.getDestination().getAirportName() ,rte.getDestination().getIATACode());
        }
        System.out.print("Enter Flight Route (BY ID)>  ");
        chosenRoute = sc.nextLong();
        sc.nextLine();

        List<AircraftConfigurationEntity> aircraftConfig;
        try {
            aircraftConfig = aircraftConfigurationSessionBean.retrieveAllConfiguration();
        } catch (AircraftConfigNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease create a configuration first!");
            return;
        }
        
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
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
            return;
        } catch (FlightExistException ex) {
            try {
                flight = flightSessionBean.enableFlight(flightNum, chosenRoute, chosenConfig);
                System.out.println("Previous disabled flight found!\nRe-enabling flight...");
            } catch (FlightNotFoundException ex1) {
                System.out.println("Error: Flight " + flightNum + " already exists\nPlease try again!\n");
                return;
            }
        }
        System.out.println("Flight created successfully!\n");

        FlightRouteEntity flightRoute;
        try {
            flightRoute = flightRouteSessionBean.retreiveFlightRouteById(chosenRoute);
        } catch (FlightRouteNotFoundException ex) {
            return; // will never hit this
        }
        if (flightRoute.getComplementaryRoute() != null) {
            System.out.print("Complementary route found!\nWould you like to create a complementary return flight? (Y/N)> ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                while (true) {
                    String returnFlightNum;
                    System.out.print("Enter return flight number > ");
                    returnFlightNum = "ML" + sc.nextLine().trim();
                    FlightRouteEntity returnFlightRoute = flightRoute.getComplementaryRoute();
                    try {
                        FlightEntity returnFlight = new FlightEntity(returnFlightNum);
                        returnFlight = flightSessionBean.createNewFlight(returnFlight, returnFlightRoute.getFlightRouteID(), chosenConfig);
                        flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), returnFlight.getFlightID());
                        System.out.println("Return flight created!\n");
                        break;
                    } catch (UnknownPersistenceException | FlightNotFoundException | FlightRouteNotFoundException | AircraftConfigNotFoundException ex) {
                        System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n"); // will never hit this
                    } catch (FlightExistException ex) {
                        try {
                            FlightEntity returnFlight = flightSessionBean.enableFlight(returnFlightNum, returnFlightRoute.getFlightRouteID(), chosenConfig);
                            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), returnFlight.getFlightID());
                            System.out.println("Previous disabled return flight found!\nRe-enabling flight...\n");
                            break;
                        } catch (FlightNotFoundException ex1) {
                            System.out.println("Error: Flight " + returnFlightNum + " already exists\nPlease try again!\n");
                        }
                    }
                }
            }
        }
        
        
    }
    
    private void doViewAllFlight() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View All Flights ***");
        
        List<FlightEntity> list;
        try {
            list = flightSessionBean.retrieveAllFlight();
        } catch (FlightNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
            return;
        }
        System.out.printf("%10s%20s%20s%25s\n", "Flight ID", "Flight Number", "Flight Route", "Aircraft Configuration");
        for (FlightEntity flight : list) {
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

            System.out.printf("%10s%20s%20s%35s%20s%35s%25s%30s%20s%25s%20s%20s%30s\n", "Flight ID", "Flight Number", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA", "Aircraft Configuration ID", "Name", "Cabin Class ID", "Max Seats Capacity", "Aircraft Type", "Returning Flight Number");
            for(int i = 0; i< config.getNumberOfCabinClasses(); i++) {
            System.out.printf("%10s%20s%20s%35s%20s%35s%25s%30s%20s%25s%20s%20s%30s\n", flight.getFlightID(), flight.getFlightNum(), route.getFlightRouteID().toString(), route.getOrigin().getAirportName() ,route.getOrigin().getIATACode(), route.getDestination().getAirportName() ,route.getDestination().getIATACode(), config.getAircraftConfigID().toString(), config.getName(), config.getCabin().get(i).getCabinClassID(), config.getCabin().get(i).getMaxSeatCapacity(), config.getAircraftType().getTypeName(), flight.getReturningFlight() != null ? flight.getReturningFlight().getFlightNum(): "None");
            }
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
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        }
    }
    
    private void doUpdateFlight(FlightEntity flight) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Update Flight ***");
        
        /*
        System.out.print("Enter Flight Number (blank if no change)> ");
        String flightNum = sc.nextLine().trim();
        if (flightNum.length() > 0) {
            flight.setFlightNum("ML" + flightNum);
        }*/
        
        List<FlightRouteEntity> routes;
        try {
            routes = flightRouteSessionBean.retrieveAllFlightRouteInOrder();
        } catch (FlightRouteNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n"); 
            return;
        }
        
        System.out.printf("%20s%40s%20s%40s%25s\n", "Flight Route ID", "Origin Airport Name", "Origin Airport IATA", "Destination Airport Name", "Destination Airport IATA");
        for (FlightRouteEntity rte : routes) {
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
                    
                    if (flight.getReturningFlight() != null) {
                        flight.getReturningFlight().setSourceFlight(null);
                    }
                    flight.setReturningFlight(null);
                   
                    if (flight.getSourceFlight() != null) {
                        flight.getSourceFlight().setReturningFlight(null);
                    }
                    flight.setSourceFlight(null);
                    
                }
            } catch (FlightRouteNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
                return;
            }
        }
        
        List<AircraftConfigurationEntity> aircraftConfig;
        try {
            aircraftConfig = aircraftConfigurationSessionBean.retrieveAllConfiguration();
        } catch (AircraftConfigNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
            return;
        }
        
        System.out.printf("%30s%20s%25s%20s\n", "Aircraft Configuration ID", "Name", "Number of Cabin Class", "Aircraft Type");
        for (AircraftConfigurationEntity config : aircraftConfig) {
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
                System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
                return;
            }
        }
        
        List<FlightEntity> list;
        try {
            list = flightSessionBean.retrieveAllFlightByFlightRoute(flight.getFlightRoute().getDestination().getIATACode(), flight.getFlightRoute().getOrigin().getIATACode());             
            boolean display = false;
            for (FlightEntity returnFlight: list) {
                if (returnFlight.getSourceFlight() == null && returnFlight.getFlightID() != flight.getFlightID()) {
                    display = true;
                    break;
                }   
            }
            if (display) {
                System.out.printf("%10s%20s%20s%25s\n", "Flight ID", "Flight Number", "Flight Route", "Aircraft Configuration");
                for (FlightEntity returnFlight : list) {
                    if (returnFlight.getSourceFlight() == null && returnFlight.getFlightID() != flight.getFlightID()) {
                        System.out.printf("%10s%20s%20s%25s\n", returnFlight.getFlightID() ,returnFlight.getFlightNum(), returnFlight.getFlightRoute().getOrigin().getIATACode() + " -> " + returnFlight.getFlightRoute().getDestination().getIATACode() , returnFlight.getAircraftConfig().getName());
                    }
                }
                System.out.print("Enter return flight to associate (BY ID)(negative number if no change or none)>  ");
                Long chosenReturnFlight = sc.nextLong();
                sc.nextLine();
                if (chosenReturnFlight > 0) {
                    try {
                        FlightEntity fe = flightSessionBean.retreiveFlightById(chosenReturnFlight);
                        flight.setReturningFlight(fe);
                    } catch (FlightNotFoundException ex) {
                        System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
                        return;
                    }
                }
            }
        } catch (FlightNotFoundException ex) {
        }
        
        Set<ConstraintViolation<FlightEntity>>constraintViolations = validator.validate(flight);
        if(constraintViolations.isEmpty()) {
            try {
                flightSessionBean.updateFlight(flight);
                System.out.println("Flight Updated Successfully");
            } catch (FlightNotFoundException | UpdateFlightException | InputDataValidationException | UnknownPersistenceException | FlightRouteNotFoundException | AircraftConfigNotFoundException ex) {
                 System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
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
                
                if (response == 1) {
                    doCreateFlightSchedulePlan();
                } else if (response == 2) {
                    doViewAllFlightSchedulePlan();
                } else if (response == 3) {
                    doViewFlightSchedulePlanDetails();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 4) {
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
                      
            List<FlightEntity> list = flightSessionBean.retrieveAllFlight();
            System.out.printf("%10s%20s%20s%25s\n", "Flight ID", "Flight Number", "Flight Route", "Aircraft Configuration");
            for (FlightEntity flight : list) {
                System.out.printf("%10s%20s%20s%25s\n", flight.getFlightID() ,flight.getFlightNum(), flight.getFlightRoute().getOrigin().getIATACode() + " -> " + flight.getFlightRoute().getDestination().getIATACode() , flight.getAircraftConfig().getName());
            }
            System.out.print("Enter flight to create a schedule plan for (BY ID)> ");
            FlightEntity flight = flightSessionBean.retreiveFlightById(sc.nextLong());
            sc.nextLine();
            plan.setFlight(flight);
            plan.setFlightNum(flight.getFlightNum());

            System.out.print("Enter Schedule Type (1: Single 2: Multiple 3:Recurrent 4:Recurrent weekly)>  ");
            int typeInput = sc.nextInt();
            sc.nextLine();
            System.out.println("Create new flight schedule for flight " + flight.getFlightNum());
            switch (typeInput) {
                case 1:
                    plan.setTypeExistingInPlan(ScheduleTypeEnum.SINGLE);
                    pair = getFlightScheduleInfo();
                    break;
                case 2:
                    plan.setTypeExistingInPlan(ScheduleTypeEnum.MULTIPLE);
                    System.out.print("Enter number of schedule to be created> ");
                    int num = sc.nextInt();
                    sc.nextLine();
                    for(int i = 0; i < num; i++) {
                        Pair pair1 = getFlightScheduleInfo();
                        info.add(pair1);
                    }
                    break;
                case 3:
                    plan.setTypeExistingInPlan(ScheduleTypeEnum.RECURRENTDAY);
                    pair = getFlightScheduleInfo();
                    System.out.print("Enter recurrent end date (dd/mm/yyyy)> ");
                    String date = sc.nextLine().trim();
                    Date dailyEnd = recurrentInputFormat.parse(date);
                    plan.setRecurrentEndDate(dailyEnd);

                    break;
                case 4:
                    plan.setTypeExistingInPlan(ScheduleTypeEnum.RECURRENTWEEK);
                    pair = getFlightScheduleInfo();
                    System.out.print("Enter recurrent end date (dd/mm/yyyy)> ");
                    String date1 = sc.nextLine().trim();
                    Date weekEnd = recurrentInputFormat.parse(date1);
                    plan.setRecurrentEndDate(weekEnd);
                    break;
            }

            Set<ConstraintViolation<FlightSchedulePlanEntity>>constraintViolations = validator.validate(plan);
            if (constraintViolations.isEmpty()) {
                if (plan.getTypeExistingInPlan().equals(ScheduleTypeEnum.MULTIPLE)) {
                    plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(plan, flight.getFlightID(), info);
                } else if (plan.getTypeExistingInPlan().equals(ScheduleTypeEnum.RECURRENTDAY)) {
                    System.out.print("Enter interval of recurrence (1-6)> ");
                    int days = sc.nextInt();
                    sc.nextLine();
                    plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(plan, flight.getFlightID(), pair, days);
                } else if (plan.getTypeExistingInPlan().equals(ScheduleTypeEnum.RECURRENTWEEK)) {
                    plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(plan, flight.getFlightID(), pair, 7);
                } else {
                    plan = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(plan, flight.getFlightID(), pair, 0);
                }
                System.out.println("New Flight Schedule Plan for Flight " + plan.getFlightNum() + " created successfully!\n");

            } else {
                showInputDataValidationErrorsForSchedulePlanEntity(constraintViolations);
                return;
            }

            List<CabinClassEntity> cabinClass = flight.getAircraftConfig().getCabin();
            System.out.println("Aircraft Configuration for flight " + flight.getFlightNum() + " contains " + cabinClass.size() + " cabins");
            System.out.println("Please enter fares for each cabin class!\n");

            for (CabinClassEntity cc: cabinClass) {
                String type = "";
                if (null != cc.getCabinClassType()) switch (cc.getCabinClassType()) {
                    case F:
                        type = "First Class";
                        break;
                    case J:
                        type = "Business Class";
                        break;
                    case W:
                        type = "Premium Economy Class";
                        break;
                    case Y:
                        type = "Economy Class";
                        break;
                    default:
                        break;
                }
                System.out.println("** Creating fare for " + type + " **");
                while (true) {
                    FareEntity fare = createFareEntity(cc);
                    try {
                        fareSessionBean.createFareEntity(fare, plan.getFlightSchedulePlanID(), cc.getCabinClassID());
                        System.out.println("Fare created successfully!");
                        System.out.print("Would you like to add more fares to this cabin class? (Y/N)> ");
                        String reply = sc.nextLine().trim();
                        if (!reply.equals("Y") && !reply.equals("y")) {
                            break;
                        }
                    } catch (FlightSchedulePlanNotFoundException | CabinClassNotFoundException | FareExistException ex) {
                        System.out.println("Error: " + ex.getMessage() + "\nPlease try again!");
                    }
                }
            }
            System.out.println("Fares successfully created!\n");

            if (flight.getReturningFlight() != null) {
                FlightEntity returnFlight = flight.getReturningFlight();
                System.out.println("Complementary return flight has been found for flight " + flight.getFlightNum() + "!");
                System.out.print("Would you like to create a complementary return flight schedule plan? (Y/N)> ");
                String reply = sc.nextLine().trim();
                if (reply.equals("Y") || reply.equals("y")) {
                    System.out.print("Enter layover time (HRS)> ");
                    int layover;
                    while (true) {
                        try {
                            layover = Integer.parseInt(sc.nextLine().trim());
                            break;
                        } catch (NumberFormatException ex) {
                            System.out.print("Please enter a valid value!\n> ");
                        }
                    }
                    FlightSchedulePlanEntity returnPlan = new FlightSchedulePlanEntity();
                    returnPlan.setFlightNum(returnFlight.getFlightNum());
                    returnPlan.setTypeExistingInPlan(plan.getTypeExistingInPlan());
                    if (plan.getRecurrentEndDate() != null) {
                        // this technically would be the recurrent end date of the original plan
                        // which may not be entirely accurate for this returnPlan (due to the layover time)
                        // but it doesnt matter because we calling the createMultiplePlan method below
                        returnPlan.setRecurrentEndDate(plan.getRecurrentEndDate());
                    }

                    List<Pair<Date,Integer>> info2 = new ArrayList<>();
                    for (FlightScheduleEntity fs: plan.getFlightSchedule()) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(fs.getDepartureDateTime());
                        c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + layover);
                        Date newDeparture = c.getTime();
                        info2.add(new Pair<>(newDeparture, fs.getDuration()));
                    }
                    returnPlan = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnPlan, returnFlight.getFlightID(), info2);
                    System.out.println("New Flight Schedule Plan for Return Flight " + plan.getFlightNum() + " created successfully!\n");

                    flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(plan.getFlightSchedulePlanID(), returnPlan.getFlightSchedulePlanID()); 

                    List<CabinClassEntity> returnCabinClass = returnFlight.getAircraftConfig().getCabin();
                    System.out.println("Aircraft Configuration for flight " + returnFlight.getFlightNum() + " contains " + returnCabinClass.size() + " cabins");
                    System.out.println("Please enter fares for each cabin class!\n");

                    for (CabinClassEntity cc: returnCabinClass) {
                        String type = "";
                        if (null != cc.getCabinClassType()) switch (cc.getCabinClassType()) {
                            case F:
                                type = "First Class";
                                break;
                            case J:
                                type = "Business Class";
                                break;
                            case W:
                                type = "Premium Economy Class";
                                break;
                            case Y:
                                type = "Economy Class";
                                break;
                            default:
                                break;
                        }
                        System.out.println("** Creating fare for " + type + " **");
                        while (true) {
                            FareEntity fare = createFareEntity(cc);
                            try {
                                fareSessionBean.createFareEntity(fare, returnPlan.getFlightSchedulePlanID(), cc.getCabinClassID());
                                System.out.println("Fare created successfully!");
                                System.out.print("Would you like to add more fares to this cabin class? (Y/N)> ");
                                String reply2 = sc.nextLine().trim();
                                if(!reply2.equals("Y") && !reply2.equals("y")) {
                                    break;
                                }
                            } catch (FlightSchedulePlanNotFoundException | CabinClassNotFoundException | FareExistException ex) {
                                System.out.println("Error: " + ex.getMessage() + "\nPlease try again!");
                            }
                        }
                    }
                    System.out.println("Fares successfully created!\n");
                } else {
                    System.out.println();
                }
            }           
            
            
        } catch (FlightNotFoundException | ParseException | UnknownPersistenceException | InputDataValidationException | FlightSchedulePlanExistException  ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        } catch (FlightSchedulePlanNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private FareEntity createFareEntity(CabinClassEntity cabinclass) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter fare basis code (2 to 6 characters)> ");
        String code = cabinclass.getCabinClassType() + sc.next().trim();
        System.out.print("Enter fare amount> ");
        BigDecimal cost = sc.nextBigDecimal();
        FareEntity fare = new FareEntity(code, cost);
        return fare;
    }
    
    
    private Pair<Date, Integer> getFlightScheduleInfo() throws ParseException {
        Date departure = null;
        int duration = 0;
        
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss a");
        SimpleDateFormat outputFormatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss a");
        
        System.out.print("Enter departure Date and Time (dd/mm/yyyy hh:mm:ss AM/PM)> ");
        String input = sc.nextLine().trim();
        departure = formatter.parse(input);
        System.out.print("Enter estimated flight duration (HRS)> ");
        duration = sc.nextInt();
        return new Pair<>(departure, duration);
    }
    
    private void doViewAllFlightSchedulePlan() {

        Scanner sc = new Scanner(System.in);
        System.out.println("*** View All Flight Schedule Plans ***");
  
        List<FlightSchedulePlanEntity> list;
        try {
            list = flightSchedulePlanSessionBean.retrieveAllFlightSchedulePlan();
        } catch (FlightSchedulePlanNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\n");
            return;
        }
        System.out.printf("%10s%15s%20s%40s%30s\n", "Plan ID", "Flight Number", "Type Plan", "Recurrent End Date", "Number of Flight Schedule");
        for (FlightSchedulePlanEntity plan : list) {
            System.out.printf("%10s%15s%20s%40s%30s\n", plan.getFlightSchedulePlanID(), plan.getFlightNum(), plan.getTypeExistingInPlan(), plan.getRecurrentEndDate(), plan.getFlightSchedule().size());
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();     
    }
    
    private void doViewFlightSchedulePlanDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            int response = 0;
            System.out.println("*** View Flight Schedule Plan details ***");
            System.out.print("Enter Flight Schedule Plan ID> ");
            Long id = sc.nextLong();
            
            FlightSchedulePlanEntity plan = flightSchedulePlanSessionBean.retrieveFlightSchedulePlanEntityById(id);
            FlightEntity flight = plan.getFlight();
            FlightRouteEntity route = flight.getFlightRoute();
            List<FlightScheduleEntity> schedule = plan.getFlightSchedule();
            List<FareEntity> fare = plan.getFares();
            
            //List<FlightSchedulePlanEntity> list = flightSchedulePlanSessionBean.retrieveNecessaryDetails();
            //List<FlightScheduleEntity> listSchedule = flightSchedulePlanSessionBean.retrieveNecessaryDetailsFromSchedule();
            //List<FlightRouteEntity> listRoute = flightRouteSessionBean.retrieveNecessaryDetails();
            //List<FareEntity> listFare = fareSessionBean.retrieveNecessaryDetails();
            
            System.out.printf("%10s%15s%20s%25s%30s%25s%40s%40s%20s%30s\n", "Plan ID", "Flight Number", "Type Plan", "Flight Schedule ID", "Departure Date", "Duration", "Origin", "Destination", "Cabin Class ID", "Fare");
            
            for (FlightScheduleEntity list : schedule) { 
                for (FareEntity fares : fare) {
                    System.out.printf("%10s%15s%20s%25s%30s%25s%40s%40s%20s%30s\n", plan.getFlightSchedulePlanID(), plan.getFlightNum(), plan.getTypeExistingInPlan(), list.getFlightScheduleID(), list.getDepartureDateTime(), list.getDuration(), route.getOrigin().getAirportName(), route.getDestination().getAirportName(), fares.getCabinClass().getCabinClassID(), fares.getFareAmount());
                }
            }
            System.out.println("--------------------------");
            System.out.println("1: Update Flight Schedule Plan");
            System.out.println("2: Delete Flight Schedule Plan");
            System.out.println("3: Back\n");
            
            System.out.print("> ");
            response = sc.nextInt();
            
            if(response == 1) {
                //doUpdateFlightSchedulePlan(plan, schedule, fare);
            }
            else if(response == 2) {
                doDeleteFlightSchedulePlan(plan);
            }
    }   catch (FlightSchedulePlanNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    //WHAT EXACTLY DO U WANT TO UPDATE???
    /*private void doUpdateFlightSchedulePlan(FlightSchedulePlanEntity plan, List<FlightScheduleEntity> schedule, List<FareEntity> fare) {
    Scanner sc = new Scanner(System.in);
    System.out.println("*** Update Flight Schedule Plan ***");
    
    System.out.print("Enter new duration (negative if no change)> ");
    int duration = sc.nextInt();
    if(duration > 0) {
    schedule.forEach(sched -> sched.setDuration(duration));
    }
    
    }*/
        
    private void doDeleteFlightSchedulePlan(FlightSchedulePlanEntity plan) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Delete Flight Schedule Plan ***");
        System.out.print("Are you sure you want to delete (Y/N)> ");
        String response = sc.nextLine().trim();
        
        if(response.equalsIgnoreCase("Y")) {
            try {
                flightSchedulePlanSessionBean.deleteFlightSchedulePlan(plan.getFlightSchedulePlanID());
            } catch (FlightSchedulePlanNotFoundException | FlightScheduleNotFoundException | FareNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage() + "\n");
            }
            System.out.println("Deletion successful!");
        }
    }

    private void showInputDataValidationErrorsForSchedulePlanEntity(Set<ConstraintViolation<FlightSchedulePlanEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }
        System.out.println("\nPlease try again......\n");
    }

    
    

    
    
}