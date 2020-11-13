/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.ws.CabinClassTypeEnum;
import ejb.session.ws.FareEntity;
import ejb.session.ws.FlightNotFoundException_Exception;
import ejb.session.ws.FlightScheduleEntity;
import ejb.session.ws.FlightScheduleNotFoundException_Exception;
import ejb.session.ws.InvalidLoginCredentialException_Exception;
import ejb.session.ws.CabinClassNotFoundException_Exception;
import ejb.session.ws.InputDataValidationException_Exception;
import ejb.session.ws.ItineraryEntity;
import ejb.session.ws.ItineraryExistException_Exception;
import ejb.session.ws.ItineraryNotFoundException_Exception;
import ejb.session.ws.MyPair;
import ejb.session.ws.ParseException_Exception;
import ejb.session.ws.PassengerEntity;
import ejb.session.ws.ReservationEntity;
import ejb.session.ws.ReservationExistException_Exception;
import ejb.session.ws.SeatInventoryEntity;
import ejb.session.ws.SeatInventoryNotFoundException_Exception;
import ejb.session.ws.UnknownPersistenceException_Exception;
import ejb.session.ws.UpdateSeatsException_Exception;
import ejb.session.ws.UserNotFoundException_Exception;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import net.java.dev.jaxb.array.UnsignedShortArray;

/**
 *
 * @author Mitsuki
 */
public class HolidayReservationSystemClientApp {

    boolean login;
    Long currentPartner;

    public HolidayReservationSystemClientApp() {
    }

    public void runApp() {
        while (true) {
            if (!login) {
                Scanner sc = new Scanner(System.in);

                System.out.println(" === Welcome to Holiday Reservation System ===\n");
                System.out.println("*** Login ***");
                System.out.println("Enter username> ");
                String username = sc.nextLine().trim();
                System.out.println("Enter password> ");
                String password = sc.nextLine().trim();

                if (username.length() > 0 && password.length() > 0) {
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

        while (login) {
            System.out.println("Welcome to Holiday Reservation System!\n");
            System.out.println("1: Search Flight");
            System.out.println("2: Reserve Flight");
            System.out.println("3: View My Flight Reservation");
            System.out.println("4: View My Flight Reservation Details");
            System.out.println("5: Log Out\n");

            response = 0;
            while (response < 1 || response > 5) {
                System.out.print("> ");
                response = sc.nextInt();

                if (response == 1) {
                    doSearchFlight();
                } else if (response == 2) {
                    doSearchFlight();
                } else if (response == 3) {
                    doViewFlightReservation();
                } else if (response == 4) {
                    doViewFlightReservationDetails();
                } else if (response == 5) {
                    doLogout();
                    System.out.println("Log out successful.\n");
                    break;
                } else {
                    System.out.println("Invalid Option, please try again!");
                }
            }
            if (response == 5) {
                break;
            }
        }
    }

    private void doLogout() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Are you sure you want to log out? (Y or N)> ");
        String reply = sc.nextLine().trim();

        if ((reply.equals("Y") || reply.equals("y")) && login) {
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
        String date;
        while (true) {
            try { 
                System.out.print("Enter departure date (dd/mm/yyyy)> ");
                date = sc.nextLine().trim();
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
            } else {
                System.out.println("Error: Invalid option\nPlease try again!");
            }
        }
        
        if (flightPref == 0) {
            boolean exit = false;
            boolean exit2 = false;
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleOutBound = getFlightSchedules(departure, destination, departureDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);


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
            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry, there are no direct flights with your desired flight route\n");   
                exit = true;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // wont hit
            }
                
             try {
                List<MyPair> dateActualFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, departureDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);

                System.out.println("\n\n                      ============= Available Connecting Outbound Flights ============= ");

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
                              
            } catch (FlightNotFoundException_Exception ex) {
                System.out.println("Sorry there are no indirect flights for your specified route\n");
                exit2 = true;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // will nvr hit
            }      
            if (exit && exit2) {
                return;
            }        
        }

        if (flightPref == 1) {
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleOutBound = getFlightSchedules(departure, destination, departureDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleOutBound = getFlightSchedules(departure, destination, c.getTime().toString(), cabin);

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
                
            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry, there are no flights with your desired flight route\n");
                return;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // wont hit
            }
        
                      
        }
        if (flightPref == 2) {
            try {
                List<MyPair> dateActualFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, departureDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime().toString(), cabin);

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
                              
            } catch (FlightNotFoundException_Exception ex) {
                System.out.println("Sorry there are not indirect flights for your specified route");
                return;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // will nvr hit
            }                         
        }
        System.out.println("\n");
        
         if (type == 2 && flightPref == 0) {
            Date returnDate;
            while (true) {
                try { 
                    System.out.print("Enter return date (dd/mm/yyyy)> ");
                    String date2 = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date2);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }      
            }
            boolean exit = false;
            boolean exit2 = false;
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleInBound = getFlightSchedules(destination, departure, returnDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                
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
               
            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry there are no return flights for this flight route within this period");
                exit = true;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // will nvr hit this
            }        
             
            try {
                List<MyPair> dateActualFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, returnDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddOneFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddTwoFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddThreeFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusOneFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusTwoFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusThreeFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);

                System.out.println("                      ============= Available Connecting Inbound Flights ============= ");

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
             
            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry there are no indirect return flights for this flight route within this period");
                exit2 = true;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // will never hit this
            } 
            if (exit && exit2) {
                return;
            }          
         }

        if (type == 2 && flightPref == 1) {
            System.out.print("Enter return date (dd/mm/yyyy)> ");

            Date returnDate;
            while (true) {
                try {
                    System.out.print("Enter departure date (dd/mm/yyyy)> ");
                    String date2 = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date2);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }
            }
            try {
                List<FlightScheduleEntity> dateActualFlightScheduleInBound = getFlightSchedules(destination, departure, returnDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddOneFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddTwoFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<FlightScheduleEntity> dateAddThreeFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusOneFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusTwoFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<FlightScheduleEntity> dateMinusThreeFlightScheduleInBound = getFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                
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
               
            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry there are no return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // will nvr hit this
            }         
        }
        if (type == 2 && flightPref == 2) {
            System.out.print("Enter return date (dd/mm/yyyy)> ");

            Date returnDate;
            while (true) {
                try {
                    System.out.print("Enter departure date (dd/mm/yyyy)> ");
                    String date2 = sc.nextLine().trim();
                    returnDate = inputFormat.parse(date2);
                    break;
                } catch (ParseException ex) {
                    System.out.println("Error: Invalid date\nPlease try again!");
                }
            }

            try {
                List<MyPair> dateActualFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, returnDate.toString(), cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddOneFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddTwoFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddThreeFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusOneFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusTwoFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusThreeFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime().toString(), cabin);

                System.out.println("\n\n                      ============= Available Connecting Flights ============= ");

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
             
            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry there are no indirect return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ParseException_Exception ex) {
                // will never hit this
            }
            
                 
         
        }
        System.out.println("\n");

        System.out.print("Would you like to reserve a flight? (Y/N)> ");
        String ans = sc.nextLine().trim();

        if (ans.equalsIgnoreCase("n")) {
            return;
        }

        Long outbound1, outbound2, inbound1, inbound2;
        if (type == 1 && flightPref == 1) {
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
        doReserveFlight(outbound1, outbound2, inbound1, inbound2, cabin, passengers);
    }

    private void doReserveFlight(Long outbound1, Long outbound2, Long inbound1, Long inbound2, CabinClassTypeEnum cabinClassType, int noOfPassengers) {
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
                    
            BigDecimal pricePerPax;

            if (outbound2 == null && inbound1 == null && inbound2 == null) {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                            
                outbound1Reservation = new ReservationEntity();
                outbound1Reservation.setCabinClassType(outbound1Seats.getCabin().getCabinClassType());       
                outbound1Reservation.setFareBasisCode(outbound1Fare.getFareBasisCode());
                outbound1Reservation.setFareAmount(outbound1Fare.getFareAmount()); 
                
                pricePerPax = outbound1Fare.getFareAmount();
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                
                
                long itineraryId = createNewItinerary(creditCardNum, cvv, currentPartner);
                               
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itineraryId);                  
                
                System.out.println("Reservation Itinerary (Booking ID: " + itineraryId + ") created successfully!\n");
            } else if (outbound2 == null && inbound2 == null) {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                outbound1Reservation = new ReservationEntity();
                outbound1Reservation.setCabinClassType(outbound1Seats.getCabin().getCabinClassType());       
                outbound1Reservation.setFareBasisCode(outbound1Fare.getFareBasisCode());
                outbound1Reservation.setFareAmount(outbound1Fare.getFareAmount()); 
                
                inbound1FlightSchedule = retrieveFlightScheduleById(inbound1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound1Seats = getDesiredSeatInventory(inbound1FlightSchedule);   
                } else {
                    inbound1Seats = getCorrectSeatInventory(inbound1FlightSchedule, cabinClassType);
                }
                inbound1Fare = getBiggestFare(inbound1FlightSchedule, inbound1Seats.getCabin().getCabinClassType());
                inbound1SeatSelection = getSeatBookings(inbound1Seats, noOfPassengers); 
                inbound1Reservation = new ReservationEntity();
                inbound1Reservation.setCabinClassType(inbound1Seats.getCabin().getCabinClassType());       
                inbound1Reservation.setFareBasisCode(inbound1Fare.getFareBasisCode());
                inbound1Reservation.setFareAmount(inbound1Fare.getFareAmount()); 
                
                pricePerPax = outbound1Fare.getFareAmount().add(inbound1Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                
                long itineraryId = createNewItinerary(creditCardNum, cvv, currentPartner);
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itineraryId);                  
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound1SeatSelection.get(i));
                }  
                createNewReservation(inbound1Reservation, passengers, inbound1FlightSchedule.getFlightScheduleID(), itineraryId);  
                
                System.out.println("Reservation Itinerary (Booking ID: " + itineraryId + ") created successfully!\n");
            } else if (inbound1 == null && inbound2 == null) {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                outbound1Reservation = new ReservationEntity();
                outbound1Reservation.setCabinClassType(outbound1Seats.getCabin().getCabinClassType());       
                outbound1Reservation.setFareBasisCode(outbound1Fare.getFareBasisCode());
                outbound1Reservation.setFareAmount(outbound1Fare.getFareAmount()); 
                
                outbound2FlightSchedule = retrieveFlightScheduleById(outbound2);
                System.out.println("Seat Selection for outbound connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound2Seats = getDesiredSeatInventory(outbound2FlightSchedule);  
                } else {
                   outbound2Seats = getCorrectSeatInventory(outbound2FlightSchedule, cabinClassType);
                }    
                outbound2Fare = getBiggestFare(outbound2FlightSchedule, outbound2Seats.getCabin().getCabinClassType());
                outbound2SeatSelection = getSeatBookings(outbound2Seats, noOfPassengers);
                outbound2Reservation = new ReservationEntity();
                outbound2Reservation.setCabinClassType(outbound2Seats.getCabin().getCabinClassType());       
                outbound2Reservation.setFareBasisCode(outbound2Fare.getFareBasisCode());
                outbound2Reservation.setFareAmount(outbound2Fare.getFareAmount()); 
                
                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                
                long itineraryId = createNewItinerary(creditCardNum, cvv, currentPartner);
                      
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itineraryId);
            
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound2SeatSelection.get(i));
                }
                createNewReservation(outbound2Reservation, passengers, outbound2FlightSchedule.getFlightScheduleID(), itineraryId);
                
                System.out.println("Reservation Itinerary (Booking ID: " + itineraryId + ") created successfully!\n");
            } else {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);  
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);
                outbound1Reservation = new ReservationEntity();
                outbound1Reservation.setCabinClassType(outbound1Seats.getCabin().getCabinClassType());       
                outbound1Reservation.setFareBasisCode(outbound1Fare.getFareBasisCode());
                outbound1Reservation.setFareAmount(outbound1Fare.getFareAmount()); 
                
                outbound2FlightSchedule = retrieveFlightScheduleById(outbound2);
                System.out.println("Seat Selection for outbound connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound2Seats = getDesiredSeatInventory(outbound2FlightSchedule);  
                } else {
                   outbound2Seats = getCorrectSeatInventory(outbound2FlightSchedule, cabinClassType);
                }
                outbound2Fare = getBiggestFare(outbound2FlightSchedule, outbound2Seats.getCabin().getCabinClassType());
                outbound2SeatSelection = getSeatBookings(outbound2Seats, noOfPassengers);
                outbound2Reservation = new ReservationEntity();
                outbound2Reservation.setCabinClassType(outbound2Seats.getCabin().getCabinClassType());       
                outbound2Reservation.setFareBasisCode(outbound2Fare.getFareBasisCode());
                outbound2Reservation.setFareAmount(outbound2Fare.getFareAmount()); 
                
                inbound1FlightSchedule = retrieveFlightScheduleById(inbound1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound1Seats = getDesiredSeatInventory(inbound1FlightSchedule);   
                } else {
                    inbound1Seats = getCorrectSeatInventory(inbound1FlightSchedule, cabinClassType);
                }
                inbound1Fare = getBiggestFare(inbound1FlightSchedule, inbound1Seats.getCabin().getCabinClassType());
                inbound1SeatSelection = getSeatBookings(inbound1Seats, noOfPassengers);   
                inbound1Reservation = new ReservationEntity();
                inbound1Reservation.setCabinClassType(inbound1Seats.getCabin().getCabinClassType());       
                inbound1Reservation.setFareBasisCode(inbound1Fare.getFareBasisCode());
                inbound1Reservation.setFareAmount(inbound1Fare.getFareAmount()); 
                
                inbound2FlightSchedule = retrieveFlightScheduleById(inbound2);
                System.out.println("Seat Selection for inbound connecting flight " + inbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound2Seats = getDesiredSeatInventory(inbound2FlightSchedule);
                } else {
                    inbound2Seats = getCorrectSeatInventory(inbound2FlightSchedule, cabinClassType);
                }
                inbound2Fare = getBiggestFare(inbound2FlightSchedule, inbound2Seats.getCabin().getCabinClassType());
                inbound2SeatSelection = getSeatBookings(inbound2Seats, noOfPassengers);
                inbound2Reservation = new ReservationEntity();
                inbound2Reservation.setCabinClassType(inbound2Seats.getCabin().getCabinClassType());       
                inbound2Reservation.setFareBasisCode(inbound2Fare.getFareBasisCode());
                inbound2Reservation.setFareAmount(inbound2Fare.getFareAmount()); 
                
                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount()).add(inbound1Fare.getFareAmount()).add(inbound2Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);
                
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));
                
                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();
                 
                long itineraryId = createNewItinerary(creditCardNum, cvv, currentPartner);
                
                 for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                createNewReservation(outbound1Reservation, passengers, outbound1FlightSchedule.getFlightScheduleID(), itineraryId);
            
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound2SeatSelection.get(i));
                }
                createNewReservation(outbound2Reservation, passengers, outbound2FlightSchedule.getFlightScheduleID(), itineraryId);
            
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound1SeatSelection.get(i));
                }  
                createNewReservation(inbound1Reservation, passengers, inbound1FlightSchedule.getFlightScheduleID(), itineraryId);
                
                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound2SeatSelection.get(i));
                }  
                createNewReservation(inbound2Reservation, passengers, inbound2FlightSchedule.getFlightScheduleID(), itineraryId);
                
                System.out.println("Reservation Itinerary (Booking ID: " + itineraryId + ") created successfully!\n");
            }           
        } catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception | ReservationExistException_Exception | UnknownPersistenceException_Exception | UserNotFoundException_Exception | SeatInventoryNotFoundException_Exception | UpdateSeatsException_Exception | InputDataValidationException_Exception | ItineraryExistException_Exception | ItineraryNotFoundException_Exception ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        } 
    }

    private SeatInventoryEntity getDesiredSeatInventory(FlightScheduleEntity flightSchedule) {
        Scanner sc = new Scanner(System.in);
        int i = 1;
        System.out.println(" * Available Cabin Classes * ");
        for (SeatInventoryEntity seats : flightSchedule.getSeatInventory()) {
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

    private List<String> getSeatBookings(SeatInventoryEntity seatInventory, int noOfPassengers) {
        Scanner sc = new Scanner(System.in);
        int totalAvailSeats = seatInventory.getAvailable();
        int totalReservedSeats = seatInventory.getReserved();
        int totalBalanceSeats = seatInventory.getBalance();
        
        //idk how change to char
        char[][] seats = new char[seatInventory.getCabin().getNumOfRows()][seatInventory.getCabin().getNumOfSeatsAbreast()];

        for(int i = 0; i < seats.length; i++) {
            List<Integer> list = seatInventory.getSeats().get(i).getItem();
            for(int j = 0; j < seats[0].length; j++) {
                seats[i][j] = (char) list.get(j).intValue();
            }
        }
        //System.out.println(seats);
        
        //char[][] seats = seatInventory.getSeats();
        String cabinClassConfig = seatInventory.getCabin().getSeatingConfigPerColumn();

        //Display Seats
        String type = "";
        if (null != seatInventory.getCabin().getCabinClassType()) {
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
        }

        System.out.println(" -- " + type + " -- ");
        System.out.print("Row  ");
        int count = 0;
        int no = 0;
        for (int i = 0; i < cabinClassConfig.length(); i++) {
            if (Character.isDigit(cabinClassConfig.charAt(i))) {
                no += Integer.parseInt(String.valueOf(cabinClassConfig.charAt(i)));
                while (count < no) {
                    System.out.print((char) ('A' + count) + "  ");
                    count++;
                }
            } else {
                System.out.print("   ");
            }
        }
        System.out.println();

        for (int j = 0; j < seats.length; j++) {
            System.out.printf("%-5s", String.valueOf(j + 1));
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
                    System.out.print("\nEnter seat to reserve for Passenger " + (i + 1) + "(Eg. A5)> ");
                    seatNumber = sc.nextLine().trim();
                    boolean booked = checkIfBooked(seatInventory, seatNumber);
                    if (booked) {
                        System.out.println("Seat already taken!\nPlease choose another seat");
                    } else {
                        break;
                    }
                }
                seatSelection.add(seatNumber);
            }
            boolean distinct = seatSelection.stream().distinct().count() == seatSelection.size();
            if (distinct) {
                return seatSelection;
            } else {
                System.out.println("Duplicate seats detected!\nPlease try again");
            }
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
            PassengerEntity person = new PassengerEntity();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setPassportNumber(passport);
            person.setSeatNumber(null);
            passengers.add(person);
        }
        return passengers;
    }

    private void doViewFlightReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View Flight Reservations ***\n");
        List<ItineraryEntity> list = retrieveItinerariesByUserId(currentPartner);
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

    private void doViewFlightReservationDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** View Flight Reservations Details ***\n");     
            
            System.out.print("Enter ID of reservation to view in detail> ");
            long id = sc.nextLong();
            sc.nextLine();
            System.out.println();
            ItineraryEntity itinerary = retreiveItineraryById(id);
            
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
                
        } catch (ItineraryNotFoundException_Exception ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        }
    }
    
     private void printSingleFlightSchedule(List<FlightScheduleEntity> flightSchedules, CabinClassTypeEnum cabinClassPreference, int passengers) throws CabinClassNotFoundException_Exception, FlightScheduleNotFoundException_Exception {
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
            c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
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
                        getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount(),
                        getBiggestFare(flightScheduleEntity, seats.getCabin().getCabinClassType()).getFareAmount().multiply(BigDecimal.valueOf(passengers))
                );

            }
        }
    }
    
    private void printFlightScheduleWithConnecting(List<MyPair> flightSchedulePairs, CabinClassTypeEnum cabin, int passengers) throws FlightScheduleNotFoundException_Exception, CabinClassNotFoundException_Exception {
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
        for (MyPair pair: flightSchedulePairs) {
            FlightScheduleEntity flight1 = pair.getFs1();
            FlightScheduleEntity flight2 = pair.getFs2();
            int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt() - 
                    flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
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
            c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
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
    }
    


/*
    private static java.util.List<ejb.session.ws.FlightScheduleEntity> getFlightSchedules22(java.lang.String arg0, java.lang.String arg1, java.util.Date arg2, ejb.session.ws.CabinClassTypeEnum arg3) throws FlightNotFoundException_Exception {
        ejb.session.ws.ReservationWebService_Service service = new ejb.session.ws.ReservationWebService_Service();
        ejb.session.ws.ReservationWebService port = service.getReservationWebServicePort();
        
        XMLGregorianCalendar cal;
        try {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(arg2.getYear(), arg2.getMonth(), arg2.getDay(), arg2.getHours(), arg2.getMinutes(), arg2.getSeconds()));
            return port.getFlightSchedules(arg0, arg1, cal, arg3);
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static java.util.List<ejb.session.ws.MyPair> getIndirectFlightSchedules22(java.lang.String arg0, java.lang.String arg1, java.util.Date arg2, ejb.session.ws.CabinClassTypeEnum arg3) throws FlightNotFoundException_Exception {
        ejb.session.ws.ReservationWebService_Service service = new ejb.session.ws.ReservationWebService_Service();
        ejb.session.ws.ReservationWebService port = service.getReservationWebServicePort();
        
        XMLGregorianCalendar cal;
        try {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(arg2.getYear(), arg2.getMonth(), arg2.getDay(), arg2.getHours(), arg2.getMinutes(), arg2.getSeconds()));
            return port.getIndirectFlightSchedules(arg0, arg1, cal, arg3);
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
        return null; 
    }*/

    private static boolean checkIfBooked(ejb.session.ws.SeatInventoryEntity seatinventoryentity, java.lang.String seatnumber) {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.checkIfBooked(seatinventoryentity, seatnumber);
    }

    private static Long doLogin(java.lang.String username, java.lang.String password) throws InvalidLoginCredentialException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.doLogin(username, password);
    }

    private static FareEntity getBiggestFare(ejb.session.ws.FlightScheduleEntity flightscheduleentity, ejb.session.ws.CabinClassTypeEnum cabinclasstype) throws FlightScheduleNotFoundException_Exception, CabinClassNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.getBiggestFare(flightscheduleentity, cabinclasstype);
    }

    private static SeatInventoryEntity getCorrectSeatInventory(ejb.session.ws.FlightScheduleEntity flightscheduleentity, ejb.session.ws.CabinClassTypeEnum cabinclasstype) throws SeatInventoryNotFoundException_Exception, FlightScheduleNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.getCorrectSeatInventory(flightscheduleentity, cabinclasstype);
    }


    private static FlightScheduleEntity retrieveFlightScheduleById(long flightscheduleid) throws FlightScheduleNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.retrieveFlightScheduleById(flightscheduleid);
    }

    private static long createNewReservation(ejb.session.ws.ReservationEntity reservationentity, java.util.List<ejb.session.ws.PassengerEntity> passengers, long flightscheduleid, long itineraryid) throws ReservationExistException_Exception, InputDataValidationException_Exception, FlightScheduleNotFoundException_Exception, UnknownPersistenceException_Exception, SeatInventoryNotFoundException_Exception, UpdateSeatsException_Exception, ItineraryNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.createNewReservation(reservationentity, passengers, flightscheduleid, itineraryid);
    }

    private static long createNewItinerary(java.lang.String creditcardnumber, java.lang.String cvv, long userid) throws UnknownPersistenceException_Exception, ItineraryExistException_Exception, InputDataValidationException_Exception, UserNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.createNewItinerary(creditcardnumber, cvv, userid);
    }

    private static java.util.List<ejb.session.ws.ItineraryEntity> retrieveItinerariesByUserId(long userid) {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.retrieveItinerariesByUserId(userid);
    }

    private static ItineraryEntity retreiveItineraryById(long itineraryid) throws ItineraryNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.retreiveItineraryById(itineraryid);
    }


    private static java.util.List<ejb.session.ws.FlightScheduleEntity> getFlightSchedules(java.lang.String origin, java.lang.String destination, java.lang.String date, ejb.session.ws.CabinClassTypeEnum cabinclasstype) throws FlightNotFoundException_Exception, ParseException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.getFlightSchedules(origin, destination, date, cabinclasstype);
    }

    private static java.util.List<ejb.session.ws.MyPair> getIndirectFlightSchedules(java.lang.String origin, java.lang.String destination, java.lang.String date, ejb.session.ws.CabinClassTypeEnum cabinclasstype) throws FlightNotFoundException_Exception, ParseException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.getIndirectFlightSchedules(origin, destination, date, cabinclasstype);
    }
    
    

}
