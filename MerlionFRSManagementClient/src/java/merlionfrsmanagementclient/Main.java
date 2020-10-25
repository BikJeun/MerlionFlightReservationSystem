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
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.SeatsInventorySessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author Ong Bik Jeun
 */
public class Main {

    @EJB
    private static AirportSessionBeanRemote airportSessionBean;

    @EJB
    private static AircraftTypeSessionBeanRemote aircraftTypeSessionBean;

    @EJB
    private static CabinClassSessionBeanRemote cabinClassSessionBean;

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static SeatsInventorySessionBeanRemote seatsInventorySessionBean;

    @EJB
    private static FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;

    @EJB
    private static FlightSessionBeanRemote flightSessionBean;

    @EJB
    private static FlightRouteSessionBeanRemote flightRouteSessionBean;

    @EJB
    private static AircraftConfigurationSessionBeanRemote aircraftConfigurationSessionBean;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;
    
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(airportSessionBean, aircraftTypeSessionBean, cabinClassSessionBean,reservationSessionBean,seatsInventorySessionBean, flightSchedulePlanSessionBean, flightSessionBean, flightRouteSessionBean, aircraftConfigurationSessionBean, employeeSessionBean);
        mainApp.runApp();
    }
    
}
