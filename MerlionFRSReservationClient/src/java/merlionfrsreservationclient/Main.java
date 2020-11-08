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
import javax.ejb.EJB;

/**
 *
 * @author Ong Bik Jeun
 */
public class Main {

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static PassengerSessionBeanRemote passengerSessionBean;

    @EJB
    private static FareSessionBeanRemote fareSessionBean;

    @EJB
    private static SeatsInventorySessionBeanRemote seatsInventorySessionBean; 

    @EJB
    private static FlightScheduleSessionBeanRemote flightScheduleSessionBean;

    @EJB
    private static FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBean;

    @EJB
    private static FlightSessionBeanRemote flightSessionBean;

    @EJB
    private static FlightRouteSessionBeanRemote flightRouteSessionBean;

    @EJB
    private static AirportSessionBeanRemote airportSessionBean;

    @EJB
    private static CustomerSessionBeanRemote customerSessionBean;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(reservationSessionBean, passengerSessionBean, fareSessionBean, seatsInventorySessionBean, flightRouteSessionBean, flightSessionBean, flightSchedulePlanSessionBean, flightScheduleSessionBean, airportSessionBean, customerSessionBean);
        mainApp.runApp();
    }
    
}
