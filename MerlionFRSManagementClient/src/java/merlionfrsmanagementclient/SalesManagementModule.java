/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merlionfrsmanagementclient;

import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.SeatsInventorySessionBeanRemote;
import entity.EmployeeEntity;
import entity.FlightEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.SeatInventoryEntity;
import enumeration.EmployeeAccessRightEnum;
import exceptions.FlightNotFoundException;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ong Bik Jeun
 */
public class SalesManagementModule {
    private EmployeeEntity currentEmployee;
    private SeatsInventorySessionBeanRemote seatsInventorySessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private FlightSessionBeanRemote flightSessionBean;

    public SalesManagementModule(EmployeeEntity currentEmployee, SeatsInventorySessionBeanRemote seatsInventorySessionBean, ReservationSessionBeanRemote reservationSessionBean, FlightSessionBeanRemote flightSessionBean) {
        this.currentEmployee = currentEmployee;
        this.seatsInventorySessionBean = seatsInventorySessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.flightSessionBean = flightSessionBean;
    }

    public void mainMenu() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        
        while(true) {
            System.out.println("=== Sales Management Module ===\n");
            System.out.println("1: View Seats Inventory");
            System.out.println("2: View Flight Reservation");
            System.out.println("3: Exit\n");
            
            response = 0;
            while(response < 1 || response > 3) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.SALESMANAGER)) {
                        viewSeatsInventory();
                    } else {
                        System.out.println("You are not authorised to use this feature.");
                    }
                } else if (response == 2) {
                    if(currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.ADMINISTRATOR) || currentEmployee.getAccessRight().equals(EmployeeAccessRightEnum.SALESMANAGER)) {
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

    private void viewSeatsInventory() {
        
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** View Seats Inventory ***");
            System.out.print("Enter Flight Number> ");
            String flightNum = sc.nextLine().trim();
            FlightEntity flight = flightSessionBean.retrieveFlightByFlightNumber(flightNum);
            if (flight.getFlightSchedulePlan().isEmpty()) {
                System.out.println("Error: The selected flight has no flight schedule plans associated with it\n");
                return;
            }
            System.out.println("Displaying all flight schedules for Flight " + flightNum + ": " + flight.getFlightRoute().getOrigin().getIATACode() + " -> " + flight.getFlightRoute().getDestination().getIATACode());
            System.out.printf("%25s%30s%20s\n", "Flight Schedule ID", "Departure Date Time", "Duration (HRS)");
            for (FlightSchedulePlanEntity fsp: flight.getFlightSchedulePlan()) {
                for (FlightScheduleEntity fs: fsp.getFlightSchedule()) {
                    System.out.printf("%25s%30s%20s\n", fs.getFlightScheduleID().toString(), fs.getDepartureDateTime().toString(), String.valueOf(fs.getDuration()));
                }
            }
            System.out.print("Select flight schedule (BY ID)>  ");
            Long chosenFlightScheduleId = sc.nextLong();
            sc.nextLine();
            
            FlightScheduleEntity flightSchedule = null;
            for (FlightSchedulePlanEntity fsp: flight.getFlightSchedulePlan()) {
                for (FlightScheduleEntity fs: fsp.getFlightSchedule()) {
                    if (Objects.equals(fs.getFlightScheduleID(), chosenFlightScheduleId)) {
                        flightSchedule = fs;
                    }
                }
            }
            if (flightSchedule == null) {
                System.out.println("Error: Flight Schedule with ID " + chosenFlightScheduleId + " does not exist with flight " + flightNum + "\n");
                return;
            }
            
            int totalAvailSeats = 0;
            int totalReservedSeats = 0;
            int totalBalanceSeats = 0;
            for (SeatInventoryEntity seatInventory: flightSchedule.getSeatInventory()) {
                totalAvailSeats += seatInventory.getAvailable();
                totalReservedSeats += seatInventory.getReserved();
                totalBalanceSeats += seatInventory.getBalance();
                
                char[][] seats = seatInventory.getSeats();
                String cabinClassConfig = seatInventory.getCabin().getSeatingConfigPerColumn();
                
                String type = "";
                if (null !=  seatInventory.getCabin().getCabinClassType()) switch (seatInventory.getCabin().getCabinClassType()) {
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
                
                System.out.println("\nNumber of available seats: " + seatInventory.getAvailable());
                System.out.println("Number of reserved seats: " + seatInventory.getReserved());
                System.out.println("Number of balance seats: " + seatInventory.getBalance() + "\n");
               
            }
            
            System.out.println(" --- Total --- ");
            System.out.println("Number of available seats: " + totalAvailSeats);
            System.out.println("Number of reserved seats: " + totalReservedSeats);
            System.out.println("Number of balance seats: " + totalBalanceSeats);
            System.out.print("Press any key to continue...> ");
            sc.nextLine();
            
        } catch (FlightNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage() + "\nPlease try again!\n");
        } 
    }
    
    
}
