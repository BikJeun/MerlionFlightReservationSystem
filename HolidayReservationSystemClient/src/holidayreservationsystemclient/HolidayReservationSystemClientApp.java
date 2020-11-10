/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package holidayreservationsystemclient;

import ejb.session.ws.CabinClassEntity;
import ejb.session.ws.CabinClassNotFoundException_Exception;
import ejb.session.ws.CabinClassTypeEnum;
import ejb.session.ws.FareEntity;
import ejb.session.ws.FareNotFoundException_Exception;
import ejb.session.ws.FlightNotFoundException_Exception;
import ejb.session.ws.FlightScheduleEntity;
import ejb.session.ws.FlightScheduleNotFoundException_Exception;
import ejb.session.ws.InvalidLoginCredentialException_Exception;
import ejb.session.ws.MyPair;
import ejb.session.ws.PartnerEntity;
import ejb.session.ws.PassengerEntity;
import ejb.session.ws.ReservationEntity;
import ejb.session.ws.ReservationExistException_Exception;
import ejb.session.ws.ReservationNotFoundException_Exception;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import net.java.dev.jaxb.array.UnsignedShortArray;

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
        while (true) {
            if (!login) {
                Scanner sc = new Scanner(System.in);

                System.out.println(" === Welcome to Merlion Flight Reservation System ===\n");
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
            System.out.println("You are currently logged in from " + currentPartner.getName() + "!\n");
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
            } else {
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
                for (FlightScheduleEntity flightScheduleEntity : dateActualFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    //may need to check if its in the format we want
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateMinusOneFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateMinusTwoFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateMinusThreeFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateAddOneFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateAddTwoFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateAddThreeFlightScheduleOutBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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

            } catch (FlightNotFoundException_Exception ex) {
                System.out.println(ex.getMessage());
                return;
            } catch (FlightScheduleNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (CabinClassNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (flightPref == 2) {
            try {
                List<MyPair> dateActualFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, departureDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(departureDate);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

                c.setTime(departureDate);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusOneFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusTwoFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusThreeFlightScheduleOutBound = getIndirectFlightSchedules(departure, destination, c.getTime(), cabin);

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
                for (MyPair pair : dateActualFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateMinusOneFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateMinusTwoFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateMinusThreeFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateAddOneFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateAddTwoFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateAddThreeFlightScheduleOutBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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

            } catch (FlightScheduleNotFoundException_Exception ex) {
                System.out.println("Sorry there are not indirect flights for your specified route");
                return;
            } catch (CabinClassNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (FlightNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
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
                for (FlightScheduleEntity flightScheduleEntity : dateActualFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateMinusOneFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateMinusTwoFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateMinusThreeFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateAddOneFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateAddTwoFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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
                for (FlightScheduleEntity flightScheduleEntity : dateAddThreeFlightScheduleInBound) {
                    int diff = flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flightScheduleEntity.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flightScheduleEntity.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flightScheduleEntity.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff);
                    Date arrival = c2.getTime();
                    for (SeatInventoryEntity seats : flightScheduleEntity.getSeatInventory()) {
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

            } catch (FlightNotFoundException_Exception ex) {
                System.out.print("Sorry there are no return flights for this flight route within this period");
                return;
            } catch (FlightScheduleNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (CabinClassNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
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
                List<MyPair> dateActualFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, returnDate, cabin);

                Calendar c = Calendar.getInstance();

                c.setTime(returnDate);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddOneFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddTwoFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, 1);
                List<MyPair> dateAddThreeFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);

                c.setTime(returnDate);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusOneFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusTwoFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);
                c.add(Calendar.DATE, -1);
                List<MyPair> dateMinusThreeFlightScheduleInBound = getIndirectFlightSchedules(destination, departure, c.getTime(), cabin);

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
                for (MyPair pair : dateActualFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateMinusOneFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateMinusTwoFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateMinusThreeFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateAddOneFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                for (MyPair pair : dateAddTwoFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
                        "Duration (HRS)", //20
                        "Arrival Date & Time", // 30
                        "Cabin Type", //30
                        "Number of Seats Balanced", //30
                        "Price per head", //25
                        "Total Price", //25
                        "Connecting Flight ID", //25
                        "Connecting Flight Number", //30
                        "Connecting Departure Airport", //45
                        "Arrival Airport", //45
                        "Departure Date & Time", //40
                        "Duration (HRS)", //20
                        "Arrival Date & Time", //30
                        "Cabin Type", //30
                        "Number of Seats Balanced", //30
                        "Price per head", //25
                        "Total Price"); //25
                for (MyPair pair : dateAddThreeFlightScheduleInBound) {
                    FlightScheduleEntity flight1 = pair.getFs1();
                    FlightScheduleEntity flight2 = pair.getFs2();
                    int diff1 = flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(flight1.getDepartureDateTime().toGregorianCalendar().getTime());
                    c2.add(Calendar.HOUR_OF_DAY, flight1.getDuration());
                    c2.add(Calendar.HOUR_OF_DAY, diff1);
                    Date arrival1 = c2.getTime();
                    int diff2 = flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getGmt()
                            - flight2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getGmt();
                    Calendar c3 = Calendar.getInstance();
                    c3.setTime(flight2.getDepartureDateTime().toGregorianCalendar().getTime());
                    c3.add(Calendar.HOUR_OF_DAY, flight2.getDuration());
                    c3.add(Calendar.HOUR_OF_DAY, diff2);
                    Date arrival2 = c3.getTime();
                    for (SeatInventoryEntity seats1 : flight1.getSeatInventory()) {
                        for (SeatInventoryEntity seats2 : flight2.getSeatInventory()) {
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
            } catch (FlightScheduleNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (CabinClassNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (FlightNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystemClientApp.class
                        .getName()).log(Level.SEVERE, null, ex);
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
            outbound2 = null;
            inbound2 = null;
            System.out.print("Enter the outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the inbound flight you would like to reserve (Flight ID)> ");
            inbound1 = sc.nextLong();
            sc.nextLine();
        } else if (type == 2 && flightPref == 1) {
            inbound1 = null;
            inbound2 = null;
            System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
            outbound2 = sc.nextLong();
        } else if (type == 2 && flightPref == 2) {
            System.out.print("Enter the first outbound flight you would like to reserve (Flight ID)> ");
            outbound1 = sc.nextLong();
            System.out.print("Enter the connecting outbound flight you would like to reserve (Flight ID)> ");
            outbound2 = sc.nextLong();
            System.out.print("Enter the first inbound flight you would like to reserve (Flight ID)> ");
            inbound1 = sc.nextLong();
            System.out.print("Enter the connecting inbound flight you would like to reserve (Flight ID)> ");
            inbound2 = sc.nextLong();
        } else {
            return;
        }
        doReserveFlight(outbound1, outbound2, inbound1, inbound2, cabin, passengers);
    }

    private void doReserveFlight(Long outbound1, Long outbound2, Long inbound1, Long inbound2, CabinClassTypeEnum cabinClassType, int noOfPassengers) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Reserve Flight ***\n");

        FlightScheduleEntity outbound1FlightSchedule;
        List<String> outbound1SeatSelection;
        FareEntity outbound1Fare;
        SeatInventoryEntity outbound1Seats;

        FlightScheduleEntity outbound2FlightSchedule;
        List<String> outbound2SeatSelection;
        FareEntity outbound2Fare;
        SeatInventoryEntity outbound2Seats;

        FlightScheduleEntity inbound1FlightSchedule;
        List<String> inbound1SeatSelection;
        FareEntity inbound1Fare;
        SeatInventoryEntity inbound1Seats;

        FlightScheduleEntity inbound2FlightSchedule;
        List<String> inbound2SeatSelection;
        FareEntity inbound2Fare;
        SeatInventoryEntity inbound2Seats;

        BigDecimal pricePerPax;

        if (outbound2 == null && inbound1 == null && inbound2 == null) {
            try {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);

                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }

                pricePerPax = outbound1Fare.getFareAmount();
                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));

                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();

                ReservationEntity reservation = new ReservationEntity();
                reservation.setCreditCardNumber(creditCardNum);
                reservation.setCvv(cvv);

                long reservationId = createNewReservation(reservation,
                        passengers, outbound1FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        outbound1Fare.getFareID(), outbound1Seats.getCabin().getCabinClassID());

                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");
            } catch (InvalidLoginCredentialException_Exception | FlightScheduleNotFoundException_Exception | SeatInventoryNotFoundException_Exception | CabinClassNotFoundException_Exception | UnknownPersistenceException_Exception | FareNotFoundException_Exception | ReservationExistException_Exception | UserNotFoundException_Exception | UpdateSeatsException_Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else if (outbound2 == null && inbound2 == null) {
            try {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);

                inbound1FlightSchedule = retrieveFlightScheduleById(inbound1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound1Seats = getDesiredSeatInventory(inbound1FlightSchedule);
                } else {
                    inbound1Seats = getCorrectSeatInventory(inbound1FlightSchedule, cabinClassType);
                }
                inbound1Fare = getBiggestFare(inbound1FlightSchedule, inbound1Seats.getCabin().getCabinClassType());
                inbound1SeatSelection = getSeatBookings(inbound1Seats, noOfPassengers);

                pricePerPax = outbound1Fare.getFareAmount().add(inbound1Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);

                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));

                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();

                ReservationEntity reservation = new ReservationEntity();
                reservation.setCreditCardNumber(creditCardNum);
                reservation.setCvv(cvv);

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                long reservationId = createNewReservation(reservation,
                        passengers, outbound1FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        outbound1Fare.getFareID(), outbound1Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound1SeatSelection.get(i));
                }
                reservationId = createNewReservation(reservation,
                        passengers, inbound1FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        inbound1Fare.getFareID(), inbound1Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for return flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");
            } catch (InvalidLoginCredentialException_Exception | FlightScheduleNotFoundException_Exception | SeatInventoryNotFoundException_Exception | CabinClassNotFoundException_Exception | UnknownPersistenceException_Exception | FareNotFoundException_Exception | ReservationExistException_Exception | UserNotFoundException_Exception | UpdateSeatsException_Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else if (inbound1 == null && inbound2 == null) {
            try {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);

                outbound2FlightSchedule = retrieveFlightScheduleById(outbound2);
                System.out.println("Seat Selection for outbound connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound2Seats = getDesiredSeatInventory(outbound2FlightSchedule);
                } else {
                    outbound2Seats = getCorrectSeatInventory(outbound2FlightSchedule, cabinClassType);
                }
                outbound2Fare = getBiggestFare(outbound2FlightSchedule, outbound2Seats.getCabin().getCabinClassType());
                outbound2SeatSelection = getSeatBookings(outbound2Seats, noOfPassengers);

                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);

                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));

                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();

                ReservationEntity reservation = new ReservationEntity();
                reservation.setCreditCardNumber(creditCardNum);
                reservation.setCvv(cvv);

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                long reservationId = createNewReservation(reservation,
                        passengers, outbound1FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        outbound1Fare.getFareID(), outbound1Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound2SeatSelection.get(i));
                }
                reservationId = createNewReservation(reservation,
                        passengers, outbound2FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        outbound2Fare.getFareID(), outbound2Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");
            } catch (InvalidLoginCredentialException_Exception | FlightScheduleNotFoundException_Exception | SeatInventoryNotFoundException_Exception | CabinClassNotFoundException_Exception | FareNotFoundException_Exception | ReservationExistException_Exception | UserNotFoundException_Exception | UpdateSeatsException_Exception ex) {
                System.out.println(ex.getMessage());
            } catch (UnknownPersistenceException_Exception ex) {

            }
        } else {
            try {
                outbound1FlightSchedule = retrieveFlightScheduleById(outbound1);
                System.out.println("Seat Selection for outbound flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound1Seats = getDesiredSeatInventory(outbound1FlightSchedule);
                } else {
                    outbound1Seats = getCorrectSeatInventory(outbound1FlightSchedule, cabinClassType);
                }
                outbound1Fare = getBiggestFare(outbound1FlightSchedule, outbound1Seats.getCabin().getCabinClassType());
                outbound1SeatSelection = getSeatBookings(outbound1Seats, noOfPassengers);

                outbound2FlightSchedule = retrieveFlightScheduleById(outbound2);
                System.out.println("Seat Selection for outbound connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    outbound2Seats = getDesiredSeatInventory(outbound2FlightSchedule);
                } else {
                    outbound2Seats = getCorrectSeatInventory(outbound2FlightSchedule, cabinClassType);
                }
                outbound2Fare = getBiggestFare(outbound2FlightSchedule, outbound2Seats.getCabin().getCabinClassType());
                outbound2SeatSelection = getSeatBookings(outbound2Seats, noOfPassengers);

                inbound1FlightSchedule = retrieveFlightScheduleById(inbound1);
                System.out.println("Seat Selection for inbound flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound1Seats = getDesiredSeatInventory(inbound1FlightSchedule);
                } else {
                    inbound1Seats = getCorrectSeatInventory(inbound1FlightSchedule, cabinClassType);
                }
                inbound1Fare = getBiggestFare(inbound1FlightSchedule, inbound1Seats.getCabin().getCabinClassType());
                inbound1SeatSelection = getSeatBookings(inbound1Seats, noOfPassengers);

                inbound2FlightSchedule = retrieveFlightScheduleById(inbound2);
                System.out.println("Seat Selection for inbound connecting flight " + inbound2FlightSchedule.getFlightSchedulePlan().getFlightNum());
                if (cabinClassType == null) {
                    inbound2Seats = getDesiredSeatInventory(inbound2FlightSchedule);
                } else {
                    inbound2Seats = getCorrectSeatInventory(inbound2FlightSchedule, cabinClassType);
                }
                inbound2Fare = getBiggestFare(inbound2FlightSchedule, inbound2Seats.getCabin().getCabinClassType());
                inbound2SeatSelection = getSeatBookings(inbound2Seats, noOfPassengers);

                pricePerPax = outbound1Fare.getFareAmount().add(outbound2Fare.getFareAmount()).add(inbound1Fare.getFareAmount()).add(inbound2Fare.getFareAmount());
                List<PassengerEntity> passengers = obtainPassengerDetails(noOfPassengers);

                System.out.println("Price per person : $" + pricePerPax.toString() + "\nTotal Amount : $" + pricePerPax.multiply(new BigDecimal(noOfPassengers)));

                System.out.print("Enter Credit Card Number> ");
                String creditCardNum = sc.nextLine().trim();
                System.out.print("Enter cvv> ");
                String cvv = sc.nextLine().trim();

                ReservationEntity reservation = new ReservationEntity();
                reservation.setCreditCardNumber(creditCardNum);
                reservation.setCvv(cvv);

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound1SeatSelection.get(i));
                }
                long reservationId = createNewReservation(reservation,
                        passengers, outbound1FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        outbound1Fare.getFareID(), outbound1Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for flight " + outbound1FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(outbound2SeatSelection.get(i));
                }
                reservationId = createNewReservation(reservation,
                        passengers, outbound2FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        outbound2Fare.getFareID(), outbound2Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for connecting flight " + outbound2FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound1SeatSelection.get(i));
                }
                reservationId = createNewReservation(reservation,
                        passengers, inbound1FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        inbound1Fare.getFareID(), inbound1Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for return flight " + inbound1FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");

                for (int i = 0; i < passengers.size(); i++) {
                    passengers.get(i).setSeatNumber(inbound2SeatSelection.get(i));
                }
                reservationId = createNewReservation(reservation,
                        passengers, inbound2FlightSchedule.getFlightScheduleID(), currentPartner.getUserID(),
                        inbound2Fare.getFareID(), inbound2Seats.getCabin().getCabinClassID());
                System.out.println("Reservation " + reservationId + " created successfully for User " + currentPartner.getUserID() + " for connecting return flight " + inbound2FlightSchedule.getFlightSchedulePlan().getFlightNum() + "!\n");
            } catch (InvalidLoginCredentialException_Exception | FlightScheduleNotFoundException_Exception | SeatInventoryNotFoundException_Exception | CabinClassNotFoundException_Exception | UnknownPersistenceException_Exception | FareNotFoundException_Exception | ReservationExistException_Exception | UserNotFoundException_Exception | UpdateSeatsException_Exception ex) {
                System.out.println(ex.getMessage());
            } 
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
        int[][] seats = new int[seatInventory.getCabin().getNumOfRows()][seatInventory.getCabin().getNumOfSeatsAbreast()];

        for(UnsignedShortArray a:seatInventory.getSeats()) {  
            List<Integer> list = a.getItem();
            for(int i = 0; i < seats.length; i++) {
                for(int j = 0; j < seats[0].length; j++) {
                    seats[i][j] = list.get(j);
                }
            }
        }
        
        System.out.println(seats);
        
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
        List<ReservationEntity> list = retrieveReservationsByCustomerId(currentPartner.getUserID());
        System.out.printf("%25s%30s%20s%20s\n", "Reservation ID", "Flight Schedule ID", "Flight Number", "Cabin Class");
        for (ReservationEntity res : list) {
            System.out.printf("%25s%30s%20s%20s\n", res.getReservationID(), res.getFlightSchedule(), res.getFlightSchedule().getFlightSchedulePlan().getFlightNum(), res.getCabinClass());
        }
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

    private void doViewFlightReservationDetails() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** View Flight Reservations Details ***\n");
            
            List<ReservationEntity> list = retrieveReservationsByCustomerId(currentPartner.getUserID());
            System.out.printf("%25s%30s%20s%20s\n", "Reservation ID", "Flight Schedule ID", "Flight Number", "Cabin Class");
            for (ReservationEntity res : list) {
                System.out.printf("%25s%30s%20s%20s\n", res.getReservationID(), res.getFlightSchedule(), res.getFlightSchedule().getFlightSchedulePlan().getFlightNum(), res.getCabinClass());
            }
            
            System.out.print("Enter ID of reservation to view in detail> ");
            long id = sc.nextLong();
            ReservationEntity res = retrieveReservationById(id);
            FlightScheduleEntity sched = res.getFlightSchedule();
            List<PassengerEntity> passengers = res.getPassenger();
            CabinClassEntity cabin = res.getCabinClass();
            BigDecimal amt = res.getFare().getFareAmount().multiply(BigDecimal.valueOf(passengers.size()));
            
            System.out.printf("%35s%25s%25s%35s%25s%15s%30s%20s%20s%35s\n", "Flight Schedule ID", "Flight Number", "Origin Airport", "Destination Airport", "Departure Date",
                    "Duration", "Passenger Name", "Cabin Type", "Seat Number", "Total amount paid");
            
            for (int i = 0; i < passengers.size(); i++) {
                System.out.printf("%35s%25s%25s%35s%25s%15s%30s%20s%20s%35s\n", sched.getFlightScheduleID(), sched.getFlightSchedulePlan().getFlightNum(), sched.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin(),
                        sched.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination(), sched.getDepartureDateTime().toString().substring(0, 19), sched.getDuration(), passengers.get(i).getFirstName() + " " + passengers.get(i).getLastName(),
                        cabin.getCabinClassType(), passengers.get(i).getSeatNumber(), amt);
            }
        } catch (ReservationNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static boolean checkIfBooked(ejb.session.ws.SeatInventoryEntity arg0, java.lang.String arg1) {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.checkIfBooked(arg0, arg1);
    }

    private static long createNewReservation(ejb.session.ws.ReservationEntity arg0, java.util.List<ejb.session.ws.PassengerEntity> arg1, long arg2, long arg3, long arg4, long arg5) throws UnknownPersistenceException_Exception, FareNotFoundException_Exception, CabinClassNotFoundException_Exception, FlightScheduleNotFoundException_Exception, SeatInventoryNotFoundException_Exception, ReservationExistException_Exception, UserNotFoundException_Exception, UpdateSeatsException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.createNewReservation(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    private static PartnerEntity doLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginCredentialException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.doLogin(arg0, arg1);
    }

    private static FareEntity getBiggestFare(ejb.session.ws.FlightScheduleEntity arg0, ejb.session.ws.CabinClassTypeEnum arg1) throws FlightScheduleNotFoundException_Exception, CabinClassNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.getBiggestFare(arg0, arg1);
    }

    private static SeatInventoryEntity getCorrectSeatInventory(ejb.session.ws.FlightScheduleEntity arg0, ejb.session.ws.CabinClassTypeEnum arg1) throws FlightScheduleNotFoundException_Exception, SeatInventoryNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.getCorrectSeatInventory(arg0, arg1);
    }

    private static java.util.List<ejb.session.ws.FlightScheduleEntity> getFlightSchedules(java.lang.String arg0, java.lang.String arg1, java.util.Date arg2, ejb.session.ws.CabinClassTypeEnum arg3) throws FlightNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        
        XMLGregorianCalendar cal;
        try {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(arg2.getYear(), arg2.getMonth(), arg2.getDay(), arg2.getHours(), arg2.getMinutes(), arg2.getSeconds()));
            return port.getFlightSchedules(arg0, arg1, cal, arg3);
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private static java.util.List<ejb.session.ws.MyPair> getIndirectFlightSchedules(java.lang.String arg0, java.lang.String arg1, java.util.Date arg2, ejb.session.ws.CabinClassTypeEnum arg3) throws FlightNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        
        XMLGregorianCalendar cal;
        try {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(arg2.getYear(), arg2.getMonth(), arg2.getDay(), arg2.getHours(), arg2.getMinutes(), arg2.getSeconds()));
            return port.getIndirectFlightSchedules(arg0, arg1, cal, arg3);
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
        return null; 
    }

    private static FlightScheduleEntity retrieveFlightScheduleById(java.lang.Long arg0) throws InvalidLoginCredentialException_Exception, FlightScheduleNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.retrieveFlightScheduleById(arg0);
    }

    private static ReservationEntity retrieveReservationById(long arg0) throws ReservationNotFoundException_Exception {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.retrieveReservationById(arg0);
    }

    private static java.util.List<ejb.session.ws.ReservationEntity> retrieveReservationsByCustomerId(java.lang.Long arg0) {
        ejb.session.ws.FlightReservationWebService_Service service = new ejb.session.ws.FlightReservationWebService_Service();
        ejb.session.ws.FlightReservationWebService port = service.getFlightReservationWebServicePort();
        return port.retrieveReservationsByCustomerId(arg0);
    }

}
