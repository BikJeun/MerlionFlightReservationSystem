/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.ws.CabinClassTypeEnum;
import ejb.session.ws.FlightScheduleEntity;
import ejb.session.ws.InvalidLoginCredentialException_Exception;
import ejb.session.ws.PartnerEntity;
import ejb.session.ws.SeatInventoryEntity;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author Mitsuki
 */
public class HolidayReservationSystemClientApp {
     boolean login;
    PartnerEntity currentPartner;

    public HolidayReservationSystemClientApp() {
    }
    
    public void runApp() {
        while(true) {
            if(!login) {
                Scanner sc = new Scanner(System.in);
                int response = 0;
                
                System.out.println(" === Welcome to Merlion Flight Reservation System ===\n");
                System.out.println("*** Login ***");
                System.out.println("Enter username> ");
                String username = sc.nextLine().trim();
                System.out.println("Enter password> ");
                String password = sc.nextLine().trim();
                
                if(username.length() > 0 && password.length() > 0) {
                    try {
                        currentPartner = doLogin(username, password);
                        login = true;
                    } catch (InvalidLoginCredentialException_Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } else {
                mainMenu();
            }
        }
    }
    
private void mainMenu() {
      Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(login) {
            System.out.println("You are currently logged in from " + currentPartner.getName() + "!\n");
            System.out.println("1: Search Flight");
            System.out.println("2: Reserve Flight");
            System.out.println("3: View My Flight Reservation");
            System.out.println("4: View My Flight Reservation Details");
            System.out.println("5: Log Out\n");
            
            response = 0;
            while(response < 1 || response > 5) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    doSearchFlight();
                } else if(response == 2) {
                    //doReserveFlight();
                } else if(response == 3) {
                    //doViewFlightReservation();
                } else if (response == 4) {
                    //doViewFlightReservationDetails();
                } else if (response == 5) {
                    doLogout();
                    System.out.println("Log out successful.\n");
                    break;
                } else {
                    System.out.println("Invalid Option, please try again!");
                }
            }
            if(response == 5) {
                break;
            }
        }
    }

    private static PartnerEntity doLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginCredentialException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.doLogin(arg0, arg1);
    }
    
    private void doLogout() {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Are you sure you want to log out? (Y or N)> ");
        String reply = sc.nextLine().trim();
        
        if((reply.equals("Y") || reply.equals("y")) && login) {
            currentPartner = null;
            login = false;
        }
    }

    private void doSearchFlight() {
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
            System.out.print("State you preference (1. Direct Flight 2.Connecting Flight)> ");
            flightPref = sc.nextInt();
            if (flightPref != 1 && flightPref != 2) {
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
        
        if (flightPref == 1) {
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleOutBound = getFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime(), cabin);
                
                System.out.println("                      ============= Available Outbound Flights ============= ");
                
                System.out.println("                             ============ On Desired Date =========== ");
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
                for (FlightScheduleEntity flightScheduleEntity: dateActualFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //may need to check if its in the format we want
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
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
                for (FlightScheduleEntity flightScheduleEntity: dateMinusOneFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
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
                for (FlightScheduleEntity flightScheduleEntity: dateMinusTwoFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                
                 System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
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
                for (FlightScheduleEntity flightScheduleEntity: dateMinusThreeFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                   //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateAddOneFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateAddTwoFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                
                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateAddThreeFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                   //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                } 
                

    } catch (FlightNotFoundException ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                // wont hit
            }
        }
        if (flightPref == 2) {
            try {
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateActualFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateAddThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<Pair<FlightScheduleEntity, FlightScheduleEntity>> dateMinusThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                System.out.println("                      ============= Available Outbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateActualFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateMinusOneFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateMinusTwoFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateMinusThreeFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                               getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateAddOneFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateAddTwoFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateAddThreeFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }

            } catch (FlightNotFoundException ex) {
                System.out.println("Sorry there are not indirect flights for your specified route");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("\n");

        if (type == 2 && flightPref == 1) {
            System.out.print("Enter return date (dd/mm/yyyy)> ");

            Date returnDate;
            while (true) {
                try { 
                    System.out.print("Enter departure date (dd/mm/yyyy)> ");
                    String date = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }      
            }
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleInBound = getFlightSchedules(destination, departure, returnDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime(), cabin);


                System.out.println("                      ============= Available Inbound Flights ============= ");

                System.out.println("                             ============ On Desired Date =========== ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateActualFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }

                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateMinusOneFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }

                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateMinusTwoFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }
                 System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateMinusThreeFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }

                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateAddOneFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }

                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateAddTwoFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }

                System.out.println("\n                  ============ Departing 3 days after Desired Date ============ ");
                System.out.printf("%15s%20s%30s%30s%40s%20s%20s%20s%30s%25s%25s\n", "Flight ID", 
                        "Flight Number", 
                        "Departure Airport", 
                        "Arrival Airport", 
                        "Departure Date & Time", 
                        "Duration (HRS)", 
                        "Arrial Date & Time", 
                        "Cabin Type", 
                        "Number of Seats Balanced", 
                        "Price per head", 
                        "Total Price");
                for (FlightScheduleEntity flightScheduleEntity: dateAddThreeFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                                flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats: flightScheduleEntity.getSeatInventory()) {
                        String cabinClassType;
                        if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.F && (cabin == CabinClassTypeEnum.F || cabin == null)) {
                            cabinClassType = "First Class";                
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.J && (cabin == CabinClassTypeEnum.J || cabin == null)) {
                            cabinClassType = "Business Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.W && (cabin == CabinClassTypeEnum.W || cabin == null)) {
                            cabinClassType = "Premium Economy Class";                  
                        } else if (seats.getCabin().getCabinClassType() == CabinClassTypeEnum.Y && (cabin == CabinClassTypeEnum.Y || cabin == null)) {
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
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                                getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                        );              
                    }       
                }

            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry there are no return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                // will nvr hit this
            } 
        }
        if (type == 2 && flightPref == 2) {
            System.out.print("Enter return date (dd/mm/yyyy)> ");

            Date returnDate;
            while (true) {
                try { 
                    System.out.print("Enter departure date (dd/mm/yyyy)> ");
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
                System.out.printf("%%15s%20s%40s%40s%30s%20s%30s%30s%30s%25s%25s%25s%30s%45s%45s%40s%20s%30s%30s%30s%25s%25s\n", "Flight ID", 
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateActualFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
                
                System.out.println("\n                  ============ Departing 1 day before Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateMinusOneFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
                
                System.out.println("\n                  ============ Departing 2 days before Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateMinusTwoFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
                
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateMinusThreeFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
                
                System.out.println("\n                  ============ Departing 1 day after Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateAddOneFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
                
                System.out.println("\n                  ============ Departing 2 days after Desired Date ============ ");
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
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateAddTwoFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
                
                System.out.println("\n                  ============ Departing 3 days before Desired Date ============ ");
                System.out.printf("%15s%20s%40s%40s%30s%20s%30s%30s%30s%25s%25s%25s%30s%45s%45s%40s%20s%30s%30s%30s%25s%25s\n", "Flight ID", //15
                        "Flight Number", //20
                        "Departure Airport", //40
                        "Arrival Airport", //40
                        "Departure Date & Time", //30 
                        "Duration (HRS)",  //20
                        "Arrival Date & Time", // 30  
                        "Cabin Type",  //30
                        "Number of Seats Balanced", //30
                        "Price per head", //25
                        "Total Price", //25
                        "Connecting Flight ID", //25 
                        "Connecting Flight Number", //30
                        "Connecting Departure Airport", //45
                        "Arrival Airport",  //45
                        "Departure Date & Time", //40 
                        "Duration (HRS)", //20
                        "Arrival Date & Time",  //30
                        "Cabin Type",  //30
                        "Number of Seats Balanced", //30 
                        "Price per head", //25
                        "Total Price"); //25
                for (Pair<FlightScheduleEntity, FlightScheduleEntity> pair: dateAddThreeFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getKey();
                    FlightScheduleEntity flight2 = pair.getValue();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //xmlgregorian
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                            flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    //xmlgregorian
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
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
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight1, seats1.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers)),
                                flight2.getFlightScheduleID(), 
                                flight2.getFlightSchedulePlan().getFlightNum(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(), 
                                flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), 
                                flight2.getDepartureDateTime().toString().substring(0, 19), 
                                flight2.getDuration(), 
                                arrival2.toString().substring(0, 19), 
                                cabinClassType2, 
                                seats2.getBalance(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount(), 
                                getBiggestFare(flight2, seats2.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                            );
                        }
                    }
                }
            } catch (FlightNotFoundException ex) {
                System.out.print("Sorry there are no indirect return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex) {
                // will never hit this
            }
        }
        System.out.println("\n");
        
        System.out.print("Would you like to reserve a flight? (Y/N)> ");
        String ans = sc.nextLine().trim();
        
        if (ans.equalsIgnoreCase("n")) {
            return;
        }
               
        System.out.print("Enter flight you would like to reserve (Enter by Index Number)> ");
        int index = sc.nextInt();
        sc.nextLine();
        //reserveFlight(schedWithDesiredCabinOnly.get(index - 1), passengers, fares.get(index - 1));
        
        if (type == 2) {
            System.out.println("Enter return flight you would like to reserve (Enter by Index Number)> ");
            int returnIndex = sc.nextInt();
            //reserveFlight(schedWithDesiredCabinOnlyReturn.get(returnIndex - 1), passengers, faresReturn.get(index - 1));
        }
    }
}
        
        
                      

    

    
    
    
    
    


    

