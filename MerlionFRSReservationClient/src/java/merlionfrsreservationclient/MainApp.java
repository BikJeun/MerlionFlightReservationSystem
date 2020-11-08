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
import ejb.session.stateless.PassengerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.SeatsInventorySessionBeanRemote;
import entity.AirportEntity;
import entity.CabinClassEntity;
import entity.CustomerEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightScheduleEntity.FlightScheduleComparator;
import entity.FlightSchedulePlanEntity;
import entity.PassengerEntity;
import entity.ReservationEntity;
import entity.SeatInventoryEntity;
import enumeration.CabinClassTypeEnum;
import exceptions.CustomerExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightScheduleNotFoundException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.InvalidLoginCredentialException;
import exceptions.PassengerAlreadyExistException;
import exceptions.ReservationExistException;
import exceptions.ReservationNotFoundException;
import exceptions.SeatInventoryNotFoundException;
import exceptions.UnknownPersistenceException;
import exceptions.UpdateSeatsException;
import exceptions.UserNotFoundException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Ong Bik Jeun
 */
public class MainApp {
    
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
    
    public MainApp(ReservationSessionBeanRemote reservationSessionBean, PassengerSessionBeanRemote passengerSessionBean, FareSessionBeanRemote fareSessionBean, SeatsInventorySessionBeanRemote seatsInventorySessionBean,FlightRouteSessionBeanRemote flightRouteSessionBean, FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean, FlightScheduleSessionBeanRemote flightScheduleSessionBean, AirportSessionBeanRemote airportSessionBean, CustomerSessionBeanRemote customerSessionBean) {
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
        //System.out.print(username +" "+ password);
        
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
        
        System.out.print("Enter Trip Type (1. One-Way 2. Round-Trip/Return)> ");
        int type = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter departure airport (By IATA code)> ");
        String departure = sc.nextLine().trim();
        System.out.print("Enter destination airport (By IATA code)> ");
        String destination = sc.nextLine().trim();
        System.out.print("Enter departure date (dd/mm/yyyy)> ");
        String date = sc.nextLine().trim();
        Date departureDate = null;
        try {
            departureDate = inputFormat.parse(date);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        Calendar c = Calendar.getInstance();
        c.setTime(departureDate);
        
        System.out.print("Enter number of passengers> ");
        int passengers = sc.nextInt();
        
        System.out.print("State you preference (1. Direct Flight 2.Connecting Flight)> ");
        int flightPref = sc.nextInt();
        System.out.print("Enter preference for cabin class (1. First Class 2. Business Class 3. Premium Economy Class 4.Economy Class)> ");
        int cabinPref = sc.nextInt();
        CabinClassTypeEnum cabin = null;
        switch(cabinPref) {
            case 1:
                cabin = CabinClassTypeEnum.F;
                break;
            case 2:
                cabin = CabinClassTypeEnum.J;
                break;
            case 3:
                cabin = CabinClassTypeEnum.W;
                break;
            case 4:
                cabin = CabinClassTypeEnum.Y;
                break;
        }
        
        List<FlightScheduleEntity> list = new ArrayList<>();
        list.addAll(getDesiredFlightSchedules(departure, destination, departureDate, cabin));
        for(int i = 0; i < 3; i++) {
            c.add(Calendar.DATE, 1);
            list.addAll(getDesiredFlightSchedules(departure, destination, c.getTime(), cabin));
        }
        c.setTime(departureDate);
        for(int i = 0; i < 3; i++) {
            c.add(Calendar.DATE, -1);
            list.addAll(getDesiredFlightSchedules(departure, destination, c.getTime(), cabin));
        }
        Collections.sort(list, new FlightScheduleComparator());
        
        List<SeatInventoryEntity> schedWithDesiredCabinOnly = new ArrayList<>();
        for(FlightScheduleEntity sched : list) {
            List<SeatInventoryEntity> schedWithDesired = sched.getSeatInventory();
            for(int i = 0; i< schedWithDesired.size(); i++) {
                if(schedWithDesired.get(i).getCabin().getCabinClassType().equals(cabin)) {
                    schedWithDesiredCabinOnly.add(schedWithDesired.get(i));
                }
            }
        }
        
        List<FareEntity> fares = new ArrayList<>();
        for(int i = 0; i<schedWithDesiredCabinOnly.size(); i++) {
            List<FareEntity> desired = schedWithDesiredCabinOnly.get(i).getFlightSchedule().getFlightSchedulePlan().getFares();
            FareEntity fare = getSmallestFare(desired);
            fares.add(fare);
        }
        
        System.out.println("Available Flights : ");
        System.out.printf("%10s%20s%20s%30s%30s%40s%20s%20s%30s%25s%25s\n", "Index", "Flight ID", "Flight Number", "Departure Airport", "Arrival Airport", "Departure Date & Time", "Duration", "Cabin Type", "Number of Seats Balanced", "Price per head", "Total Price");
        for(int i = 0; i < list.size(); i++) {
            System.out.printf("%10s%20s%20s%30s%30s%40s%20s%20s%30s%25s%25s\n", i+1, list.get(i).getFlightSchedulePlan().getFlight().getFlightID(), list.get(i).getFlightSchedulePlan().getFlightNum(), list.get(i).getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(),
                    list.get(i).getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), list.get(i).getDepartureDateTime(), list.get(i).getDuration(), cabin, schedWithDesiredCabinOnly.get(i).getBalance(), fares.get(i).getFareAmount(), fares.get(i).getFareAmount().multiply(BigDecimal.valueOf(passengers)));
        }
        
        System.out.println("\n");
        sc.nextLine();
        
        
        List<FlightScheduleEntity> listReturn = new ArrayList<>();
        List<SeatInventoryEntity> schedWithDesiredCabinOnlyReturn = new ArrayList<>();
        List<FareEntity> faresReturn = new ArrayList<>();
        if(type == 2) {
            System.out.print("Enter return date (dd/mm/yyyy)> ");
            String returnD = sc.nextLine().trim();
            Date returnDate = null;
            try {
                returnDate = inputFormat.parse(returnD);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }
            c.setTime(returnDate);
            
            listReturn.addAll(getDesiredFlightSchedules(destination, departure, returnDate, cabin));
            for(int i = 0; i < 3; i++) {
                c.add(Calendar.DATE, 1);
                listReturn.addAll(getDesiredFlightSchedules(departure, destination, c.getTime(), cabin));
            }
            c.setTime(returnDate);
            for(int i = 0; i < 3; i++) {
                c.add(Calendar.DATE, -1);
                listReturn.addAll(getDesiredFlightSchedules(departure, destination, c.getTime(), cabin));
            }
            Collections.sort(listReturn, new FlightScheduleComparator());
            
            for(FlightScheduleEntity sched : listReturn) {
                List<SeatInventoryEntity> schedWithDesired = sched.getSeatInventory();
                for(int i = 0; i< schedWithDesired.size(); i++) {
                    if(schedWithDesired.get(i).getCabin().getCabinClassType().equals(cabin)) {
                        schedWithDesiredCabinOnlyReturn.add(schedWithDesired.get(i));
                    }
                }
            }
            
            
            for(int i = 0; i<schedWithDesiredCabinOnlyReturn.size(); i++) {
                List<FareEntity> desired = schedWithDesiredCabinOnlyReturn.get(i).getFlightSchedule().getFlightSchedulePlan().getFares();
                FareEntity fare = getSmallestFare(desired);
                faresReturn.add(fare);
            }
            
            System.out.println("Return Flights: ");
            System.out.printf("%10s%20s%20s%30s%30s%40s%20s%20s%30s%25s%25s\n", "Index", "Flight ID", "Flight Number", "Departure Airport", "Arrival Airport", "Departure Date & Time", "Duration", "Cabin Type", "Number of Seats Balanced", "Price per head", "Total Price");
            for(int i = 0; i < listReturn.size(); i++) {
                System.out.printf("%10s%20s%20s%30s%30s%40s%20s%20s%30s%25s%25s\n", i+1, listReturn.get(i).getFlightSchedulePlan().getFlight().getFlightID(), listReturn.get(i).getFlightSchedulePlan().getFlightNum(), listReturn.get(i).getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getAirportName(),
                        listReturn.get(i).getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportName(), listReturn.get(i).getDepartureDateTime(), listReturn.get(i).getDuration(), cabin, schedWithDesiredCabinOnlyReturn.get(i).getBalance(), faresReturn.get(i).getFareAmount(), faresReturn.get(i).getFareAmount().multiply(BigDecimal.valueOf(passengers)));
            }
            
            System.out.println("\n");
            
            
        }
        System.out.print("Would you like to reserve a flight? (Y/N)> ");
        String ans = sc.nextLine().trim();
        
        if(ans.equalsIgnoreCase("n")) {
            runApp();
        } else if (ans.equalsIgnoreCase("y") && !login) {
            try {
                doLogin();
                login = true;
                System.out.println("*** Welcome to Merlion Airlines ***\n");
                System.out.println("You are currently logged in as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "!\n");
            } catch (InvalidLoginCredentialException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.print("Enter flight you would like to reserve (Enter by Index Number)> ");
        int index = sc.nextInt();
        sc.nextLine();
        reserveFlight(schedWithDesiredCabinOnly.get(index - 1), passengers, fares.get(index - 1));
        
        if(type == 2) {
            System.out.println("Enter return flight you would like to reserve (Enter by Index Number)> ");
            int returnIndex = sc.nextInt();
            reserveFlight(schedWithDesiredCabinOnlyReturn.get(returnIndex - 1), passengers, faresReturn.get(index - 1));
        }
    }
    
    
    
    
    private List<FlightScheduleEntity> getDesiredFlightSchedules(String departure, String destination, Date date, CabinClassTypeEnum cabin) {
        try {
            List<FlightScheduleEntity> schedule = new ArrayList<>();
            List<FlightEntity> flight = flightSessionBean.retrieveAllFlightByFlightRoute(departure, destination);
            
            //FSP that contains this flight
            List<FlightSchedulePlanEntity> fsp = new ArrayList<>();
            for(FlightEntity fli : flight) {
                fsp.addAll(fli.getFlightSchedulePlan());
            }
            
            //schedule in the plan that flies on this date
            for(FlightSchedulePlanEntity plan : fsp) {
                schedule.addAll(plan.getFlightSchedule());
            }
            
            //filtering to ensure the schedule flight has the preferred cabin class
            for(FlightScheduleEntity sched : schedule) {
                boolean hasCabin = flightScheduleSessionBean.checkIfHaveCabin(sched.getFlightScheduleID(), cabin);
                if(!hasCabin) {
                    schedule.remove(sched);
                }
            }
            
            List<FlightScheduleEntity> toDelete = new ArrayList<>();
            for(int i = 0; i<schedule.size(); i++) {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String test = df.format(schedule.get(i).getDepartureDateTime());
                Date testerDate = df.parse(test);
                
                if(testerDate.compareTo(date) != 0) {
                    toDelete.add(schedule.get(i));
                }
            }
            schedule.removeAll(toDelete);
            
            return schedule;
        } catch (FlightNotFoundException | ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
        
    }
    
    private FareEntity getSmallestFare(List<FareEntity> desired) {
        FareEntity smallest = desired.get(0);
        for(FareEntity fare : desired) {
            if(fare.getFareAmount().compareTo(smallest.getFareAmount()) < 0) {
                smallest = fare;
            }
        }
        return smallest;
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
    
    private void reserveFlight(SeatInventoryEntity sched, int passengers, FareEntity fare) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Reserve Flight ***\n");
            
            int totalAvailSeats = 0;
            int totalReservedSeats = 0;
            int totalBalanceSeats = 0;
            
            totalAvailSeats += sched.getAvailable();
            totalReservedSeats += sched.getReserved();
            totalBalanceSeats += sched.getBalance();
            
            char[][] seats = sched.getSeats();
            String cabinClassConfig = sched.getCabin().getSeatingConfigPerColumn();
            
            //Display Seats
            String type = "";
            if (null !=  sched.getCabin().getCabinClassType())
                switch (sched.getCabin().getCabinClassType()) {
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
            
            
            //Reservation Process
            List<PassengerEntity> passengerList = new ArrayList<>();
            
            for(int i = 0; i < passengers; i++) {
                boolean proceed = true;
                while(proceed) {
                    System.out.print("\nEnter seat to reserve for Passenger " +  i+1 + "(Eg. A5)> ");
                    String  reserve = sc.nextLine().trim();
                    boolean booked = seatsInventorySessionBean.checkIfBooked(sched, reserve);
                    if(booked) {
                        System.out.println("Seat already taken!\nPlease choose another seat.");
                    } else {
                        sched = blockOutReservedSeats(sched, reserve);
                        System.out.print("Enter passenger first name> ");
                        String firstName = sc.nextLine().trim();
                        System.out.print("Enter passenger last name> ");
                        String lastName = sc.nextLine().trim();
                        System.out.print("Enter passport number> ");
                        String passport = sc.nextLine().trim();
                        
                        PassengerEntity passenger = new PassengerEntity(firstName, lastName, passport);
                        try {
                            passenger = passengerSessionBean.createNewPassenger(passenger);
                        } catch (PassengerAlreadyExistException ex) {
                            passenger = passengerSessionBean.retrievePassengerByPassport(passport);
                        }
                        passengerList.add(passenger);
                        proceed = false;
                    }
                }
            }
            
            System.out.println("*** Payment ***\n");
            System.out.println("Price per person : $" + fare.getFareAmount() + ".\nTotal Amount : $" + fare.getFareAmount().multiply(BigDecimal.valueOf(passengers)));
            System.out.print("Enter Credit Card Number> ");
            String creditCardNum = sc.nextLine().trim();
            System.out.print("Enter cvv> ");
            String cvv = sc.nextLine().trim();
            
            ReservationEntity reservation = new ReservationEntity(creditCardNum, cvv);
            reservation.setFare(fare);
            reservation.setFlightSchedule(sched.getFlightSchedule());
            reservation.setCabinClass(sched.getCabin());
            reservation.setPassenger(passengerList);
            reservation.setUser(currentCustomer);
            
            //may need transaction here??
            reservation = reservationSessionBean.createNewReservation(sched.getFlightSchedule(), currentCustomer,reservation);
            seatsInventorySessionBean.updateSeatInventory(sched);
            
            System.out.println("Reservation " + reservation.getReservationID() + " created successfully for User " + reservation.getUser().getUserID() + " !\n");
            
            
        } catch (SeatInventoryNotFoundException | UpdateSeatsException | InputDataValidationException | UnknownPersistenceException | ReservationExistException | FlightScheduleNotFoundException | UserNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private SeatInventoryEntity blockOutReservedSeats(SeatInventoryEntity sched, String reserve) {
        char[][] seats = sched.getSeats();
        int col = reserve.charAt(0) - 'A';
        int row = Integer.parseInt(reserve.substring(1));
        
        seats[row-1][col] = 'X';
        sched.setSeats(seats);
        return sched;
    }
    //error wif jpql
    private void viewFlightReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** View Flight Reservations ***\n");
        List<ReservationEntity> list = reservationSessionBean.retrieveReservationsByCustomerId(currentCustomer.getUserID());
        System.out.printf("%25s%30s%20s%20s\n", "Reservation ID", "Flight Schedule ID", "Flight Number", "Cabin Class");
        for(ReservationEntity res : list) {
            System.out.printf("%25s%30s%20s%20s\n", res.getReservationID(), res.getFlightSchedule(), res.getFlightSchedule().getFlightSchedulePlan().getFlightNum(), res.getCabinClass());
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }
    //probably need something to record the seat number of each passenger reservation?
    private void viewFlightReservationDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** View Flight Reservations Details ***\n");
            
            List<ReservationEntity> list = reservationSessionBean.retrieveReservationsByCustomerId(currentCustomer.getUserID());
            System.out.printf("%25s%30s%20s%20s\n", "Reservation ID", "Flight Schedule ID", "Flight Number", "Cabin Class");
            for(ReservationEntity res : list) {
                System.out.printf("%25s%30s%20s%20s\n", res.getReservationID(), res.getFlightSchedule(), res.getFlightSchedule().getFlightSchedulePlan().getFlightNum(), res.getCabinClass());
            }
            
            System.out.print("Enter ID of reservation to view in detail> ");
            long id = sc.nextLong();
            ReservationEntity res = reservationSessionBean.retrieveReservationById(id);
            FlightScheduleEntity sched = res.getFlightSchedule();
            List<PassengerEntity> passengers = res.getPassenger();
            CabinClassEntity cabin = res.getCabinClass();
            BigDecimal amt = res.getFare().getFareAmount().multiply(BigDecimal.valueOf(passengers.size()));
            
            System.out.printf("%35s%25s%25s%35s%25s%15s%30s%20s%20s%35s\n", "Flight Schedule ID","Flight Number","Origin Airport","Destination Airport","Departure Date",
                    "Duration", "Passenger Name", "Cabin Type", "Seat Number", "Total amount paid");
            
            for(int i = 0; i<passengers.size(); i++) {
                System.out.printf("%35s%25s%25s%35s%25s%15s%30s%20s%35s\n", sched.getFlightScheduleID(), sched.getFlightSchedulePlan().getFlightNum(), sched.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin(),
                        sched.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination(), sched.getDepartureDateTime(), sched.getDuration(), passengers.get(i).getFirstName() + passengers.get(i).getLastName(),
                        cabin.getCabinClassType(), amt);
            }
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
