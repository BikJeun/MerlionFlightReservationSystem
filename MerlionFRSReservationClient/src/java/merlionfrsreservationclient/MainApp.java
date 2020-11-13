/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package merlionfrsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FareSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.ItinerarySessionBeanRemote;
import ejb.session.stateless.PassengerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.SeatsInventorySessionBeanRemote;
import entity.CustomerEntity;
import entity.FareEntity;
import entity.FlightScheduleEntity;
import entity.ItineraryEntity;
import entity.PassengerEntity;
import entity.ReservationEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CabinClassNotFoundException;
import exceptions.CustomerExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.InvalidLoginCredentialException;
import exceptions.ItineraryExistException;
import exceptions.ItineraryNotFoundException;
import exceptions.ReservationExistException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateSeatsException;
import exceptions.UserNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javafx.util.Pair;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author Ong Bik Jeun
 */
public class MainApp {
    
    private ItinerarySessionBeanRemote itinerarySessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private PassengerSessionBeanRemote passengerSessionBean;
    private FareSessionBeanRemote fareSessionBean;
    private SeatsInventorySessionBeanRemote seatsInventorySessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBean;
    private FlightSessionBeanRemote flightSessionBean;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBean;
    private AirportSessionBeanRemote airportSessionBean;
    private CustomerSessionBeanRemote customerSessionBean;
    private boolean login;
    private CustomerEntity currentCustomer;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public MainApp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();    
    }
    
    public MainApp(ItinerarySessionBeanRemote itinerarySessionBean, ReservationSessionBeanRemote reservationSessionBean, PassengerSessionBeanRemote passengerSessionBean, FareSessionBeanRemote fareSessionBean, SeatsInventorySessionBeanRemote seatsInventorySessionBean,FlightRouteSessionBeanRemote flightRouteSessionBean, FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightScheduleSessionBeanRemote flightScheduleSessionBean, AirportSessionBeanRemote airportSessionBean, CustomerSessionBeanRemote customerSessionBean) {
        this();
        this.itinerarySessionBean = itinerarySessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.passengerSessionBean = passengerSessionBean;
        this.seatsInventorySessionBean = seatsInventorySessionBean;
        this.flightRouteSessionBean = flightRouteSessionBean;
        this.flightSessionBean = flightSessionBean;
        this.flightSchedulePlanSessionBean = flightSchedulePlanSessionBean;
        this.flightScheduleSessionBean = flightScheduleSessionBean;
        this.airportSessionBean = airportSessionBean;
        this.customerSessionBean = customerSessionBean;
    }
    
    public void runApp() {
        
        while (true) {
            
            if(!login) {
                Scanner sc = new Scanner(System.in);
                Integer response = 0;
                
                System.out.println("=== Welcome to Merlion Airlines ===\n");
                System.out.println("1: Customer Login");
                System.out.println("2: Register for new Customer Account");
                System.out.println("3: Search Flight");
                System.out.println("4: Exit\n");
                
                response = 0;
                while(response < 1 || response > 4) {
                    System.out.print("> ");
                    response = sc.nextInt();
                    if (response == 1) {
                        try {
                            doLogin();
                            System.out.println("Login Successful!\n");
                            login = true;
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println();
                        }
                    } else if (response == 2) {
                        try {
                            doRegisterCustomer();
                        } catch (InvalidLoginCredentialException | CustomerExistException | UnknownPersistenceException ex) {
                            System.out.println(ex.getMessage());
                            System.out.println();
                        }
                    } else if (response == 3) {
                        searchFlight();
                    } else if (response == 4) {
                        break;
                    } else {
                        System.out.println("Invalid input, please try again!\n");
                    }
                }
                if(response == 4) {
                    break;
                }
            } else {
                mainMenu();
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner sc = new Scanner(System.in) ;
        System.out.println("*** LOGIN ***\n");
        System.out.print("Enter username> ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        String password = sc.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0) {
            currentCustomer = customerSessionBean.doLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing Login Credentials");
        }
    }
    
    private void searchFlight() {
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/M/yyyy");
        System.out.println("*** Search Flight ***\n");
        
        int type;
        while (true) {
            System.out.print("Enter Trip Type (1. One-Way 2. Round-Trip/Return)> ");
            type = sc.nextInt();
            sc.nextLine();   
            if (type != 1 && type != 2) {
                System.out.println("Error: Invalid option\nPlease try again!");
            } else {
                break;
            }
        }
        
        System.out.print("Enter departure airport (By IATA code)> ");
        String departure = sc.nextLine().trim();
        
        System.out.print("Enter destination airport (By IATA code)> ");
        String destination = sc.nextLine().trim();
        
        Date departureDate;
        while (true) {
            try { 
                System.out.print("Enter departure date (dd/mm/yyyy)> ");
                String date = sc.nextLine().trim();
                departureDate = inputFormat.parse(date);
                break;
            } catch (ParseException ex) {
                System.out.println("Error: Invalid date\nPlease try again!");
            }
        }
        
        System.out.print("Enter number of passengers> ");
        int passengers = sc.nextInt();
        
        int flightPref;
        while (true) {
            System.out.print("State you preference (0. No Preference 1. Direct Flight 2.Connecting Flight)> ");
            flightPref = sc.nextInt();
            if (flightPref != 1 && flightPref != 2 && flightPref != 0) {
                System.out.println("Error: Invalid option\nPlease try again!");
            } else {
                break;
            }
        }
         
        CabinClassTypeEnum cabin;
        while (true) {
            System.out.print("Enter preference for cabin class (0.No Preference 1. First Class 2. Business Class 3. Premium Economy Class 4.Economy Class)> ");
            int cabinPref = sc.nextInt();
            sc.nextLine();
            if (cabinPref == 1) {
                cabin = CabinClassTypeEnum.F;
                break;
            } else if (cabinPref == 2) {
                cabin = CabinClassTypeEnum.J;
                break;
            } else if (cabinPref == 3) {
                cabin = CabinClassTypeEnum.W;
                break;
            } else if (cabinPref == 4) {
                cabin = CabinClassTypeEnum.Y;
                break;
            } else if (cabinPref == 0) {
                cabin = null;
                break;
            } else  {
                System.out.println("Error: Invalid option\nPlease try again!");
            }
        }
        
        if (flightPref == 0) {
            boolean exit = false;
            boolean exit2 = false;
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);


                System.out.println("                      ============= Available Direct Outbound Flights ============= ");
                
                System.out.println("                             ============ On Desired Date =========== ");
                printSingleFlightSchedule(dateActualFlightScheduleOutBound, cabin, passengers);               

                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusOneFlightScheduleOutBound, cabin, passengers);                    
                
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusTwoFlightScheduleOutBound, cabin, passengers);  
              
                
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusThreeFlightScheduleOutBound, cabin, passengers);  
                                     
                
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printSingleFlightSchedule(dateAddOneFlightScheduleOutBound, cabin, passengers); 
                              
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddTwoFlightScheduleOutBound, cabin, passengers); 
             
                
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddThreeFlightScheduleOutBound, cabin, passengers);   
                
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
                exit = true;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
            
            try {
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateActualFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();
                
                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddOneFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddTwoFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddThreeFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusOneFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusTwoFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusThreeFlightScheduleOutBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                System.out.println("\n\n                      ============= Available Connecting Outbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateActualFlightScheduleOutBound1, cabin, passengers);
                
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusOneFlightScheduleOutBound1, cabin, passengers);
            
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusTwoFlightScheduleOutBound1, cabin, passengers);
                                  
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusThreeFlightScheduleOutBound1, cabin, passengers);
              
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddOneFlightScheduleOutBound1, cabin, passengers);
                         
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddTwoFlightScheduleOutBound1, cabin, passengers);
                               
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddThreeFlightScheduleOutBound1, cabin, passengers);
                
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
                exit2 = true;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
            if (exit && exit2) {
                return;
            }
        }
        
        if (flightPref == 1) {
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleOutBound = flightScheduleSessionBean.getFlightSchedules(departure, destination, c.getTime(), cabin);


                System.out.println("                      ============= Available Outbound Flights ============= ");
                
                System.out.println("                             ============ On Desired Date =========== ");
                printSingleFlightSchedule(dateActualFlightScheduleOutBound, cabin, passengers);               

                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusOneFlightScheduleOutBound, cabin, passengers);                    
                
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusTwoFlightScheduleOutBound, cabin, passengers);  
              
                
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusThreeFlightScheduleOutBound, cabin, passengers);  
                                     
                
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printSingleFlightSchedule(dateAddOneFlightScheduleOutBound, cabin, passengers); 
                              
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddTwoFlightScheduleOutBound, cabin, passengers); 
             
                
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddThreeFlightScheduleOutBound, cabin, passengers);                         
                
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
        if (flightPref == 2) {
            try {
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateActualFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddOneFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddTwoFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddThreeFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusOneFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusTwoFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusThreeFlightScheduleOutBound = flightScheduleSessionBean.getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                System.out.println("                      ============= Available Outbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateActualFlightScheduleOutBound, cabin, passengers);
                
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusOneFlightScheduleOutBound, cabin, passengers);
            
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusTwoFlightScheduleOutBound, cabin, passengers);
                                  
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusThreeFlightScheduleOutBound, cabin, passengers);
              
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddOneFlightScheduleOutBound, cabin, passengers);
                         
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddTwoFlightScheduleOutBound, cabin, passengers);
                               
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddThreeFlightScheduleOutBound, cabin, passengers);
                              
            } catch (FlightNotFoundException ex) {
                System.out.println("Sorry there are not indirect flights for your specified route");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
        
        System.out.println("\n");
        
        if (type == 2 && flightPref == 0) {
            Date returnDate;
            while (true) {
                try { 
                    System.out.print("Enter return date (dd/mm/yyyy)> ");
                    String date = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }      
            }
            boolean exit = false;
            boolean exit2 = false;
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, returnDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);


                System.out.println("                      ============= Available Direct Inbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printSingleFlightSchedule(dateActualFlightScheduleInBound, cabin, passengers);
              
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusOneFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusTwoFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusThreeFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printSingleFlightSchedule(dateAddOneFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddTwoFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddThreeFlightScheduleInBound, cabin, passengers);
                
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
                exit = true;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
               
            try {
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateActualFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, returnDate, cabin);
                
                Calendar c = Calendar.getInstance();
                
                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddOneFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddTwoFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddThreeFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusOneFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusTwoFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusThreeFlightScheduleInBound1 = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                
                System.out.println("\n\n                      ============= Available Connecting Inbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateActualFlightScheduleInBound1, cabin, passengers);
                              
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusOneFlightScheduleInBound1, cabin, passengers);
                                
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusTwoFlightScheduleInBound1, cabin, passengers);
                               
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusThreeFlightScheduleInBound1, cabin, passengers);
                              
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddOneFlightScheduleInBound1, cabin, passengers);
                                                      
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddTwoFlightScheduleInBound1, cabin, passengers);
                                          
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddThreeFlightScheduleInBound1, cabin, passengers);
                
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry there are no return flights for this flight route within this period");
                exit2 = true;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            } 
            if (exit && exit2) {
                return;
            }
        }

        if (type == 2 && flightPref == 1) {
            Date returnDate;
            while (true) {
                try { 
                    System.out.print("Enter return date (dd/mm/yyyy)> ");
                    String date = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }      
            }
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, returnDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleInBound = flightScheduleSessionBean.getFlightSchedules(destination, departure, c.getTime(), cabin);


                System.out.println("                      ============= Available Inbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printSingleFlightSchedule(dateActualFlightScheduleInBound, cabin, passengers);
              
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusOneFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusTwoFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printSingleFlightSchedule(dateMinusThreeFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printSingleFlightSchedule(dateAddOneFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddTwoFlightScheduleInBound, cabin, passengers);
               
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                printSingleFlightSchedule(dateAddThreeFlightScheduleInBound, cabin, passengers);
               
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry there are no return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            } 
        }        
        
        if (type == 2 && flightPref == 2) {
            Date returnDate;
            while (true) {
                try { 
                    System.out.print("Enter return date (dd/mm/yyyy)> ");
                    String date = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }      
            }
            
            try {
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateActualFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, returnDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddOneFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddTwoFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddThreeFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusOneFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusTwoFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusThreeFlightScheduleInBound = flightScheduleSessionBean.getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                
                System.out.println("                      ============= Available Inbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                printFlightScheduleWithConnecting(dateActualFlightScheduleInBound, cabin, passengers);
                              
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusOneFlightScheduleInBound, cabin, passengers);
                                
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusTwoFlightScheduleInBound, cabin, passengers);
                               
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateMinusThreeFlightScheduleInBound, cabin, passengers);
                              
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddOneFlightScheduleInBound, cabin, passengers);
                                                      
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddTwoFlightScheduleInBound, cabin, passengers);
                                          
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                printFlightScheduleWithConnecting(dateAddThreeFlightScheduleInBound, cabin, passengers);
             
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry there are no indirect return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
                
        System.out.println("\n");
        
        System.out.print("Would you like to reserve a flight? (Y/N)> ");
        String ans = sc.nextLine().trim();
        
        if (ans.equalsIgnoreCase("n")) {
            return;
        } else if (ans.equalsIgnoreCase("y") && !login) {
            try {
                doLogin();
                login = true;
                System.out.println("*** Welcome to Merlion Airlines ***\n");
                System.out.println("You are currently logged in as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "!\n");
            } catch (InvalidLoginCredentialException ex) {
                System.out.println(ex.getMessage());
                return;
            }
        }
               
        Long outbound1, outbound2, inbound1, inbound2;
        if (type == 1  && flightPref == 1) {
            outbound2 = null;
            inbound2 = null;
            inbound1 = null;
            System.out.print("Enter flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            sc.nextLine();
        } else if (type == 1 && flightPref == 2) {           
            inbound1 = null;
            inbound2 = null;
            System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
            outbound2 = sc.nextLong();  
            sc.nextLine();
        } else if (type == 2 && flightPref == 1) {
            outbound2 = null;
            inbound2 = null;
            System.out.print("Enter the outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the inbound flight you would like to reserve (Flight ID)> ");
            inbound1 = sc.nextLong();
            sc.nextLine();    
        } else if (type == 2 && flightPref == 2) {
            System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
            outbound2 = sc.nextLong();
            System.out.print("Enter the first inbound flight you would like to reserve (Flight ID)> ");
            inbound1 = sc.nextLong();
            System.out.print("Enter the connecting inbound flight you would like to reserve (Flight ID)> ");
            inbound2 = sc.nextLong();
        } else if (flightPref == 0) {
            System.out.print("Select type of flight you would like to reserve (1. Direct Flight 2.Connecting Flight)> ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (type == 1 && choice == 1) {
                outbound2 = null;
                inbound2 = null;
                inbound1 = null;
                System.out.print("Enter flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                sc.nextLine();
            } else if (type == 1 && choice == 2) {
                inbound1 = null;
                inbound2 = null;
                System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
                outbound2 = sc.nextLong(); 
            } else if (type == 2 && choice == 1) {
               outbound2 = null;
                inbound2 = null;
                System.out.print("Enter the outbound flight you would like to reserve (Flight ID)> ");
                outbound1 = sc.nextLong();
                System.out.print("Enter the inbound flight you would like to reserve (Flight ID)> ");
                inbound1 = sc.nextLong();
                sc.nextLine();   
                
            } else if (type == 2 && choice == 2) {
                System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
                 outbound1 = sc.nextLong();
                 System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
                 outbound2 = sc.nextLong();
                 System.out.print("Enter the first inbound flight you would like to reserve (Flight ID)> ");
                 inbound1 = sc.nextLong();
                 System.out.print("Enter the connecting inbound flight you would like to reserve (Flight ID)> ");
                 inbound2 = sc.nextLong();       
            } else {
                System.out.println("Error: Invalid option\nPlease try again!\n");
                return;
            }
        } else {
            return;
        }
        reserveFlight(outbound1, outbound2, inbound1, inbound2, cabin, passengers);   
    }
    
    private void doRegisterCustomer() throws InvalidLoginCredentialException, CustomerExistException, UnknownPersistenceException {
        Scanner sc = new Scanner(System.in) ;
        System.out.println("*** Register for New Customer Account ***\n");
        System.out.print("Enter first name> ");
        String firstname = sc.nextLine().trim();
        System.out.print("Enter last name> ");
        String lastname = sc.nextLine().trim();
        System.out.print("Enter identification number> ");
        String idNumber = sc.nextLine().trim();
        System.out.print("Enter contact number> ");
        String contactnumber = sc.nextLine().trim();
        System.out.print("Enter address> ");
        String address = sc.nextLine().trim();
        System.out.print("Enter postal code> ");
        String postalcode = sc.nextLine().trim();
        System.out.print("Enter desired username> ");
        String username = sc.nextLine().trim();
        System.out.print("Enter desired password> ");
        String password = sc.nextLine().trim();
        
        if(username.length() > 0 &&
                password.length() > 0 &&
                firstname.length() > 0 &&
                lastname.length() > 0 &&
                idNumber.length() > 0 &&
                contactnumber.length() > 0 &&
                address.length() > 0 &&
                postalcode.length() > 0) {
            CustomerEntity customer = new CustomerEntity(firstname, lastname, idNumber, contactnumber, address, postalcode, username, password);
            currentCustomer = customerSessionBean.createNewCustomerEntity(customer);
            login = true;
            System.out.println("Account successfully created for ID " + currentCustomer.getIdentificationNumber()+"\n");
            mainMenu();
        } else {
            throw new InvalidLoginCredentialException("Missing Account Credentials");
        }
        
    }
    
    private void mainMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        while(login) {
            System.out.println("*** Welcome to Merlion Airlines ***\n");
            System.out.println("You are currently logged in as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "!\n");
            System.out.println();
            System.out.println("*** What would you like to do ***");
            System.out.println("1: Reserve Flight");
            System.out.println("2: View My Flight Reservations");
            System.out.println("3: View My Flight Reservation Details");
            System.out.println("4: Log Out");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    searchFlight();
                } else if(response == 2) {
                    viewFlightReservation();
                } else if (response == 3) {
                    viewFlightReservationDetails();
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
            currentCustomer = null;
            login = false;
        }
    }
    
    private void reserveFlight(Long outbound1, Long outbound2, Long inbound1, Long inbound2, CabinClassTypeEnum cabinClassType, int noOfPassengers) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Reserve Flight ***\n");
            
            FlightScheduleEntity outbound1FlightSchedule;
            List<String> outbound1SeatSelection;   
            FareEntity outbound1Fare;
            SeatInventoryEntity outbound1Seats;
            ReservationEntity outbound1Reservation;
            
            FlightScheduleEntity outbound2FlightSchedule;
            List<String> outbound2SeatSelection;
            FareEntity outbound2Fare;
            SeatInventoryEntity outbound2Seats;
            ReservationEntity outbound2Reservation;
            
            FlightScheduleEntity inbound1FlightSchedule;
            List<String> inbound1SeatSelection;
            FareEntity inbound1Fare; 
            SeatInventoryEntity inbound1Seats;
            ReservationEntity inbound1Reservation;
            
            FlightScheduleEntity inbound2FlightSchedule;
            List<String> inbound2SeatSelection;
            FareEntity inbound2Fare;
            SeatInventoryEntity inbound2Seats;  
            ReservationEntity inbound2Reservation;
            
            ItineraryEntity itinerary = new ItineraryEntity();
            
            BigDecimal pricePerPax;

            if (outbound2 == null && inbound1 == null && inbound2 == null) {
                outbound1FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = flightScheduleSessionBean.getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = flightScheduleSessionBean.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                            
                outbound1Reservation = new ReservationEntity(outbound1Fare.getFareBasisCode(), outbound1Fare.getFareAmount(), outbound1Seats.getCabin().getCabinClassType());
                
                pricePerPax = outbound1Fare.getFareAmount();
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                itinerary.setCreditCardNumber(creditCardNum);
                itinerary.setCvv(cvv); 
             
                Set<ConstraintViolation<ItineraryEntity>> constraintViolations = validator.validate(itinerary);
                if (constraintViolations.isEmpty()) {
                    itinerary = itinerarySessionBean.createNewItinerary(itinerary, currentCustomer.getUserID());
                } else {
                    showInputDataValidationErrorsForItineraryEntity(constraintViolations);
                    return;
                }
                
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                reservationSessionBean.createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());                  
                
                System.out.println("Reservation Itinerary (Booking ID: " + itinerary.getItineraryId() + ") created successfully for User " + currentCustomer.getUserID() + "!\n");
            } else if (outbound2 == null && inbound2 == null) {
                outbound1FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = flightScheduleSessionBean.getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = flightScheduleSessionBean.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                outbound1Reservation = new ReservationEntity(outbound1Fare.getFareBasisCode(), outbound1Fare.getFareAmount(), outbound1Seats.getCabin().getCabinClassType());
                
                inbound1FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(inbound1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound1Seats = getDesiredSeatInventory(inbound1FlightSchedule);   
                } else {
                    inbound1Seats = flightScheduleSessionBean.getCorrectSeatInventory(inbound1FlightSchedule, cabinClassType);
                }
                inbound1Fare = flightScheduleSessionBean.getSmallestFare(inbound1FlightSchedule, inbound1Seats.getCabin().getCabinClassType());
                inbound1SeatSelection = getSeatBookings(inbound1Seats, noOfPassengers); 
                inbound1Reservation = new ReservationEntity(inbound1Fare.getFareBasisCode(), inbound1Fare.getFareAmount(), inbound1Seats.getCabin().getCabinClassType());
                
                pricePerPax = outbound1Fare.getFareAmount().add(inbound1Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                itinerary.setCreditCardNumber(creditCardNum);
                itinerary.setCvv(cvv); 
             
                Set<ConstraintViolation<ItineraryEntity>> constraintViolations = validator.validate(itinerary);
                if (constraintViolations.isEmpty()) {
                    itinerary = itinerarySessionBean.createNewItinerary(itinerary, currentCustomer.getUserID());
                } else {
                    showInputDataValidationErrorsForItineraryEntity(constraintViolations);
                    return;
                }
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                reservationSessionBean.createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());                  
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound1SeatSelection.get(i));
                }  
                reservationSessionBean.createNewReservation(inbound1Reservation, passengers, inbound1FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());  
                
                System.out.println("Reservation Itinerary (Booking ID: " + itinerary.getItineraryId() + ") created successfully for User " + currentCustomer.getUserID() + "!\n");
            } else if (inbound1 == null && inbound2 == null) {
                outbound1FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = flightScheduleSessionBean.getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = flightScheduleSessionBean.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                outbound1Reservation = new ReservationEntity(outbound1Fare.getFareBasisCode(), outbound1Fare.getFareAmount(), outbound1Seats.getCabin().getCabinClassType());
                
                outbound2FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(outbound2);
                System.out.println("Seat Selection for outbound connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound2Seats = getDesiredSeatInventory(outbound2FlightSchedule);  
                } else {
                   outbound2Seats = flightScheduleSessionBean.getCorrectSeatInventory(outbound2FlightSchedule, cabinClassType);
                }    
                outbound2Fare = flightScheduleSessionBean.getSmallestFare(outbound2FlightSchedule, outbound2Seats.getCabin().getCabinClassType());
                outbound2SeatSelection = getSeatBookings(outbound2Seats, noOfPassengers);
                outbound2Reservation = new ReservationEntity(outbound2Fare.getFareBasisCode(), outbound2Fare.getFareAmount(), outbound2Seats.getCabin().getCabinClassType());
                
                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                itinerary.setCreditCardNumber(creditCardNum);
                itinerary.setCvv(cvv); 
             
                Set<ConstraintViolation<ItineraryEntity>> constraintViolations = validator.validate(itinerary);
                if (constraintViolations.isEmpty()) {
                    itinerary = itinerarySessionBean.createNewItinerary(itinerary, currentCustomer.getUserID());
                } else {
                    showInputDataValidationErrorsForItineraryEntity(constraintViolations);
                    return;
                }
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                reservationSessionBean.createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());
            
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound2SeatSelection.get(i));
                }
                reservationSessionBean.createNewReservation(outbound2Reservation, passengers, outbound2FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());
                
                System.out.println("Reservation Itinerary (Booking ID: " + itinerary.getItineraryId() + ") created successfully for User " + currentCustomer.getUserID() + "!\n");
            } else {
                outbound1FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = flightScheduleSessionBean.getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = flightScheduleSessionBean.getSmallestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                outbound1Reservation = new ReservationEntity(outbound1Fare.getFareBasisCode(), outbound1Fare.getFareAmount(), outbound1Seats.getCabin().getCabinClassType());
                
                outbound2FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(outbound2);
                System.out.println("Seat Selection for outbound connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound2Seats = getDesiredSeatInventory(outbound2FlightSchedule);  
                } else {
                   outbound2Seats = flightScheduleSessionBean.getCorrectSeatInventory(outbound2FlightSchedule, cabinClassType);
                }
                outbound2Fare = flightScheduleSessionBean.getSmallestFare(outbound2FlightSchedule, outbound2Seats.getCabin().getCabinClassType());
                outbound2SeatSelection = getSeatBookings(outbound2Seats, noOfPassengers);
                outbound2Reservation = new ReservationEntity(outbound2Fare.getFareBasisCode(), outbound2Fare.getFareAmount(), outbound2Seats.getCabin().getCabinClassType());
                
                inbound1FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(inbound1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound1Seats = getDesiredSeatInventory(inbound1FlightSchedule);   
                } else {
                    inbound1Seats = flightScheduleSessionBean.getCorrectSeatInventory(inbound1FlightSchedule, cabinClassType);
                }
                inbound1Fare = flightScheduleSessionBean.getSmallestFare(inbound1FlightSchedule, inbound1Seats.getCabin().getCabinClassType());
                inbound1SeatSelection = getSeatBookings(inbound1Seats, noOfPassengers);   
                inbound1Reservation = new ReservationEntity(inbound1Fare.getFareBasisCode(), inbound1Fare.getFareAmount(), inbound1Seats.getCabin().getCabinClassType());
                
                inbound2FlightSchedule = flightScheduleSessionBean.retrieveFlightScheduleById(inbound2);
                System.out.println("Seat Selection for inbound connecting flight " + inbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound2Seats = getDesiredSeatInventory(inbound2FlightSchedule);
                } else {
                    inbound2Seats = flightScheduleSessionBean.getCorrectSeatInventory(inbound2FlightSchedule, cabinClassType);
                }
                inbound2Fare = flightScheduleSessionBean.getSmallestFare(inbound2FlightSchedule, inbound2Seats.getCabin().getCabinClassType());
                inbound2SeatSelection = getSeatBookings(inbound2Seats, noOfPassengers);
                inbound2Reservation = new ReservationEntity(inbound2Fare.getFareBasisCode(), inbound2Fare.getFareAmount(), inbound2Seats.getCabin().getCabinClassType());
                
                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount()).add(inbound1Fare.getFareAmount()).add(inbound2Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                itinerary.setCreditCardNumber(creditCardNum);
                itinerary.setCvv(cvv); 
             
                Set<ConstraintViolation<ItineraryEntity>> constraintViolations = validator.validate(itinerary);
                if (constraintViolations.isEmpty()) {
                    itinerary = itinerarySessionBean.createNewItinerary(itinerary, currentCustomer.getUserID());
                } else {
                    showInputDataValidationErrorsForItineraryEntity(constraintViolations);
                    return;
                }
                
                 for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                reservationSessionBean.createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());
            
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound2SeatSelection.get(i));
                }
                reservationSessionBean.createNewReservation(outbound2Reservation, passengers, outbound2FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());
            
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound1SeatSelection.get(i));
                }  
                reservationSessionBean.createNewReservation(inbound1Reservation, passengers, inbound1FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound2SeatSelection.get(i));
                }  
                reservationSessionBean.createNewReservation(inbound2Reservation, passengers, inbound2FlightSchedule.getFlightScheduleID(), itinerary.getItineraryId());
                
                System.out.println("Reservation Itinerary (Booking ID: " + itinerary.getItineraryId() + ") created successfully for User " + currentCustomer.getUserID() + "!\n");
            }           
        } catch (FlightScheduleNotFoundException | CabinClassNotFoundException | ReservationExistException | UnknownPersistenceException | UserNotFoundException | SeatInventoryNotFoundException | UpdateSeatsException | InputDataValidationException | ItineraryExistException | ItineraryNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        }
    }
    
    private List<PassengerEntity> obtainPassengerDetails(int noOfPassengers) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Passenger Details ***\n");
        List<PassengerEntity> passengers = new ArrayList<>();
        for (int i = 1; i <= noOfPassengers; i++) {
            System.out.print("Enter passenger " + (i) + " first name> ");
            String firstName = sc.nextLine().trim();
            System.out.print("Enter passenger " + (i) + " last name> ");
            String lastName = sc.nextLine().trim();
            System.out.print("Enter passenger " + (i) + " passport number> ");
            String passport = sc.nextLine().trim();
            passengers.add(new PassengerEntity(firstName, lastName, passport, null));
        }
        return passengers;
    } 
    
    private List<String> getSeatBookings(SeatInventoryEntity seatInventory, int noOfPassengers) {
        Scanner sc = new Scanner(System.in);
        int totalAvailSeats = seatInventory.getAvailable();
        int totalReservedSeats = seatInventory.getReserved();
        int totalBalanceSeats = seatInventory.getBalance();

        char[][] seats = seatInventory.getSeats();
        String cabinClassConfig = seatInventory.getCabin().getSeatingConfigPerColumn();

        //Display Seats
        String type = "";
        if (null !=  seatInventory.getCabin().getCabinClassType())
            switch (seatInventory.getCabin().getCabinClassType()) {
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

        System.out.println(" -- " + type + " -- ");
        System.out.print("Row  ");
        int count = 0;
        int no = 0;
        for (int i = 0; i < cabinClassConfig.length(); i++) {
            if (Character.isDigit(cabinClassConfig.charAt(i))) {
                no += Integer.parseInt(String.valueOf(cabinClassConfig.charAt(i)));
                while (count < no) {
                    System.out.print((char)('A' + count) + "  ");
                    count++;
                }
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        for (int j = 0; j < seats.length; j++) {
            System.out.printf("%-5s", String.valueOf(j+1));
            int count2 = 0;
            int no2 = 0;
            for (int i = 0; i < cabinClassConfig.length(); i++) {
                if (Character.isDigit(cabinClassConfig.charAt(i))) {
                    no2 += Integer.parseInt(String.valueOf(cabinClassConfig.charAt(i)));
                    while (count2 < no2) {
                        System.out.print(seats[j][count2] + "  ");
                        count2++;
                    }
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
        System.out.println(" --- Total --- ");
        System.out.println("Number of available seats: " + totalAvailSeats);
        System.out.println("Number of reserved seats: " + totalReservedSeats);
        System.out.println("Number of balance seats: " + totalBalanceSeats);

        List<String> seatSelection = new ArrayList<>();
        while (true) {        
            for (int i = 0; i < noOfPassengers; i++) {                   
                String seatNumber;
                while (true) {
                    System.out.print("\nEnter seat to reserve for Passenger " +  (i + 1) + "(Eg. A5)> ");
                    seatNumber = sc.nextLine().trim();               
                    boolean booked = seatsInventorySessionBean.checkIfBooked(seatInventory, seatNumber);
                    if (booked) {
                        System.out.println("Seat already taken!\nPlease choose another seat");
                    } else {
                        break;
                    }
                }    
                seatSelection.add(seatNumber);
            }
            boolean distinct = seatSelection.stream().distinct().count() ==  seatSelection.size();
            if (distinct) {
                return seatSelection;
            } else {
                System.out.println("Duplicate seats detected!\nPlease try again");
            }
        }
    }
    
    private SeatInventoryEntity getDesiredSeatInventory(FlightScheduleEntity flightSchedule) {
        Scanner sc = new Scanner(System.in);
        int i = 1;
        System.out.println(" * Available Cabin Classes * ");
        for (SeatInventoryEntity seats: flightSchedule.getSeatInventory()) {
            String cabinClass;
            if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F) {
                cabinClass = "First Class"; 
            } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J) {
                cabinClass = "Business Class";
            } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W) {
                cabinClass = "Premium Economy Class";
            } else {
                cabinClass = "Economy Class";  
            }

            System.out.println(i + ") " + cabinClass);
            i++;
        }
        while (true) {
            System.out.print("Select desired cabin class> ");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice <= flightSchedule.getSeatInventory().size() && choice >= 1) {
                return flightSchedule.getSeatInventory().get(choice - 1);
            } else {
                System.out.println("Error: Please enter a valid input");
            }
        }   
                
    }
 
    private void viewFlightReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View Flight Reservations ***\n");
        List<ItineraryEntity> list = itinerarySessionBean.retrieveItinerariesByCustomerId(currentCustomer.getUserID());
        for (ItineraryEntity itinerary : list) {
            System.out.println("Itinerary Reservation ID: " + itinerary.getItineraryId());
            System.out.println();
            for (ReservationEntity res: itinerary.getReservations()) {
                String journey = res.getFlightSchedule().getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIATACode() + " -> " + res.getFlightSchedule().getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIATACode();
                String departureDateTime = res.getFlightSchedule().getDepartureDateTime().toString().substring(0, 19);
                String duration = String.valueOf(res.getFlightSchedule().getDuration()) + " Hrs";
                String flightNum = res.getFlightSchedule().getFlightSchedulePlan().getFlightNum();
                System.out.println("\t" + flightNum + ", " + journey + ", " + departureDateTime + ", " + duration);
            }
            System.out.println();          
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }
    
   
    private void viewFlightReservationDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** View Flight Reservations Details ***\n");     
            
            System.out.print("Enter ID of reservation to view in detail> ");
            long id = sc.nextLong();
            sc.nextLine();
            System.out.println();
            ItineraryEntity itinerary = itinerarySessionBean.retrieveItineraryByID(id);
            
            BigDecimal totalPaid = new BigDecimal(0);
            for (ReservationEntity res: itinerary.getReservations()) {
                totalPaid = totalPaid.add(res.getFareAmount().multiply(new BigDecimal(res.getPassenger().size())));
                String journey = res.getFlightSchedule().getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIATACode() + " -> " + res.getFlightSchedule().getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIATACode();
                String departureDateTime = res.getFlightSchedule().getDepartureDateTime().toString().substring(0, 19);
                String duration = String.valueOf(res.getFlightSchedule().getDuration()) + " Hrs";
                String flightNum = res.getFlightSchedule().getFlightSchedulePlan().getFlightNum();
                String cabinClass;
                if (res.getCabinClassType() == CabinClassTypeEnum.F) {
                    cabinClass = "First Class";
                } else if (res.getCabinClassType() == CabinClassTypeEnum.J) {
                    cabinClass = "Business Class";
                } else if (res.getCabinClassType() == CabinClassTypeEnum.W) {
                    cabinClass = "Premium Economy Class";
                } else {
                    cabinClass = "Economy Class";
                }
                System.out.println("Flight: " + flightNum + ", " + journey + ", " + departureDateTime + ", " + duration);
                System.out.println();
                for (PassengerEntity passenger: res.getPassenger()) {                  
                    String name = passenger.getFirstName() + " " + passenger.getLastName();           
                    String seatNumber = passenger.getSeatNumber();
                    System.out.println("\t" + name + ", " + cabinClass + ", Seat " + seatNumber);
                }
                System.out.println();
            }
            System.out.println("Total amount paid: $" + totalPaid.toString());
            System.out.print("Press any key to continue...> ");
            sc.nextLine();
                
        } catch (ItineraryNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        }
    }
    
    private void printSingleFlightSchedule(List<FlightScheduleEntity> flightSchedules, CabinClassTypeEnum cabinClassPreference, int passengers) throws CabinClassNotFoundException, FlightScheduleNotFoundException {
        System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrival Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");          
        for (FlightScheduleEntity flightScheduleEntity : flightSchedules) {
            int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                    - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(flightScheduleEntity.getDepartureDateTime());
            double duration = flightScheduleEntity.getDuration();
            int hour = (int) duration;
            int min = (int) (duration % 1 * 60);
            c2.add(Calendar.HOUR_OF_DAY, hour);
            c2.add(Calendar.MINUTE, min);
            c2.add(Calendar.HOUR_OF_DAY, diff);
            Date arrival = c2.getTime();
            for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
                String cabinClassType;
                if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabinClassPreference == CabinClassTypeEnum.F || cabinClassPreference == null)) {
                    cabinClassType = "First Class";
                } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabinClassPreference == CabinClassTypeEnum.J || cabinClassPreference == null)) {
                    cabinClassType = "Business Class";
                } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabinClassPreference == CabinClassTypeEnum.W || cabinClassPreference == null)) {
                    cabinClassType = "Premium Economy Class";
                } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabinClassPreference == CabinClassTypeEnum.Y || cabinClassPreference == null)) {
                    cabinClassType = "Economy Class";
                } else {
                    continue;
                }
              
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", flightScheduleEntity.getFlightScheduleID(),
                        flightScheduleEntity.getFlightSchedulePlan().getFlightNum(),
                        flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(),
                        flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(),
                        flightScheduleEntity.getDepartureDateTime().toString().substring(0, 19),
                        flightScheduleEntity.getDuration(),
                        arrival.toString().substring(0, 19),
                        cabinClassType,
                        seats.getBalance(),
                        flightScheduleSessionBean.getSmallestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                        flightScheduleSessionBean.getSmallestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                );

            }
        }
    }
    
    private void printFlightScheduleWithConnecting(List<Pair<FlightScheduleEntity, FlightScheduleEntity>> flightSchedulePairs, CabinClassTypeEnum cabin, int passengers) throws FlightScheduleNotFoundException, CabinClassNotFoundException {
         System.out.printf("%15s%20s%40s%40s%30s%20s%30s%30s%30s%25s%25s%25s%30s%45s%45s%40s%20s%30s%30s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrival Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price",
                        "Connecting Flight ID", 
                        "Connecting Flight Number", 
                        "Connecting Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrival Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
        for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: flightSchedulePairs) {
            FlightScheduleEntity flight1 = pair.getKey();
            FlightScheduleEntity flight2 = pair.getValue();
            int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                    flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(flight1.getDepartureDateTime());
            double duration = flight1.getDuration();
            int hour = (int) duration;
            int min = (int) (duration % 1 * 60);
            c2.add(Calendar.HOUR_OF_DAY, hour);
            c2.add(Calendar.MINUTE, min);
            c2.add(Calendar.HOUR_OF_DAY, diff1);
            Date arrival1 = c2.getTime();
            int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                    flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
            Calendar c3 = Calendar.getInstance();
            c3.setTime(flight2.getDepartureDateTime());
            double duration2 = flight2.getDuration();
            int hour2 = (int) duration2;
            int min2 = (int) (duration2 % 1 * 60);
            c3.add(Calendar.HOUR_OF_DAY, hour2);
            c3.add(Calendar.MINUTE, min2);
            c3.add(Calendar.HOUR_OF_DAY, diff2);
            Date arrival2 = c3.getTime();
            for (SeatInventoryEntity seats1: flight1.getSeatInventory()) {
                for (SeatInventoryEntity seats2: flight2.getSeatInventory()) {
                    String cabinClassType1, cabinClassType2;
                    if (cabin == null) {
                        if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.F) {
                            cabinClassType1 = "First Class"; 
                        } else if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.J) {
                            cabinClassType1 = "Business Class";
                        } else if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.W) {
                            cabinClassType1 = "Premium Economy Class";
                        } else {
                            cabinClassType1 = "Economy Class";  
                        }
                        if (seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.F) {
                            cabinClassType2 = "First Class"; 
                        } else if (seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.J) {
                            cabinClassType2 = "Business Class";
                        } else if (seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.W) {
                            cabinClassType2 = "Premium Economy Class";
                        } else {
                            cabinClassType2 = "Economy Class";  
                        }
                    } else if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.F && seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.F && cabin == CabinClassTypeEnum.F) {
                        cabinClassType1 = "First Class"; 
                        cabinClassType2 = "First Class"; 
                    } else if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.J && seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.J && cabin == CabinClassTypeEnum.J) {
                        cabinClassType1 = "Business Class";
                        cabinClassType2 = "Business Class";
                    } else if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.W && seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.W && cabin == CabinClassTypeEnum.W) {
                        cabinClassType1 = "Premium Economy Class";
                        cabinClassType2 = "Premium Economy Class";
                    } else if (seats1.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && seats2.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && cabin == CabinClassTypeEnum.Y) {
                        cabinClassType1 = "Economy Class"; 
                        cabinClassType2 = "Economy Class"; 
                    } else {
                        continue;
                    }
                    System.out.printf("%15s%20s%40s%40s%30s%20s%30s%30s%30s%25s%25s%25s%30s%45s%45s%40s%20s%30s%30s%30s%25s%25s\n", flight1.getFlightScheduleID(), 
                        flight1.getFlightSchedulePlan().getFlightNum(), 
                        flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                        flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                        flight1.getDepartureDateTime().toString().substring(0, 19), 
                        flight1.getDuration(), 
                        arrival1.toString().substring(0, 19), 
                        cabinClassType1, 
                        seats1.getBalance(), 
                        flightScheduleSessionBean.getSmallestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                        flightScheduleSessionBean.getSmallestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                        flight2.getFlightScheduleID(), 
                        flight2.getFlightSchedulePlan().getFlightNum(), 
                        flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                        flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                        flight2.getDepartureDateTime().toString().substring(0, 19), 
                        flight2.getDuration(), 
                        arrival2.toString().substring(0, 19), 
                        cabinClassType2, 
                        seats2.getBalance(), 
                        flightScheduleSessionBean.getSmallestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                        flightScheduleSessionBean.getSmallestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                    );
                }
            }
        }
    }
    
    private void showInputDataValidationErrorsForItineraryEntity(Set<ConstraintViolation<ItineraryEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }
        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForReservationEntity(Set<ConstraintViolation<ReservationEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }
        System.out.println("\nPlease try again......\n");
    }
}
