/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.singleton;

import ejb.session.stateless.AircraftConfigurationSessionBeanLocal;
import ejb.session.stateless.AircraftTypeSessionBeanLocal;
import ejb.session.stateless.AirportSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FareSessionBeanLocal;
import ejb.session.stateless.FlightRouteSessionBeanLocal;
import ejb.session.stateless.FlightSchedulePlanSessionBeanLocal;
import ejb.session.stateless.FlightSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.AircraftConfigurationEntity;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.CabinClassEntity;
import entity.EmployeeEntity;
import entity.FareEntity;
import entity.FlightEntity;
import entity.FlightRouteEntity;
import entity.FlightScheduleEntity;
import entity.FlightSchedulePlanEntity;
import entity.PartnerEntity;
import enumeration.CabinClassTypeEnum;
import enumeration.EmployeeAccessRightEnum;
import enumeration.ScheduleTypeEnum;
import exceptions.AircraftConfigExistException;
import exceptions.AircraftConfigNotFoundException;
import exceptions.AircraftTypeExistException;
import exceptions.AirportExistException;
import exceptions.AirportNotFoundException;
import exceptions.CabinClassNotFoundException;
import exceptions.CreateNewAircraftConfigException;
import exceptions.EmployeeNotFoundException;
import exceptions.EmployeeUsernameExistException;
import exceptions.FareExistException;
import exceptions.FlightExistException;
import exceptions.FlightNotFoundException;
import exceptions.FlightRouteNotFoundException;
import exceptions.FlightSchedulePlanExistException;
import exceptions.FlightSchedulePlanNotFoundException;
import exceptions.InputDataValidationException;
import exceptions.PartnerUsernameExistException;
import exceptions.UnknownPersistenceException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mitsuki
 */
@Singleton
@LocalBean
//@Startup
public class TestDataSessionBean {
    
    @EJB
    private FareSessionBeanLocal fareSessionBean;
    
    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBean;
    
    @EJB
    private FlightSessionBeanLocal flightSessionBean;
    
    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBean;
    
    @EJB
    private AircraftConfigurationSessionBeanLocal aircraftConfigurationSessionBean;
    
    @EJB
    private AircraftTypeSessionBeanLocal aircraftTypeSessionBean;
    
    @EJB
    private AirportSessionBeanLocal airportSessionBean;
    
    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;
    
    @PersistenceContext(unitName = "MerlionFlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public TestDataSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        
        if(em.find(EmployeeEntity.class, 1l) == null) {
            doDataInit();
        }
    }
    
    private void doDataInit() {
        try {
            //Employees
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Fleet", "Manager", "fleetmanager", "password", EmployeeAccessRightEnum.FLEETMANAGER ));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Route", "Planner", "routeplanner", "password", EmployeeAccessRightEnum.ROUTEPLANNER ));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Schedule", "Manager", "schedulemanager", "password", EmployeeAccessRightEnum.SCHEDULEMANAGER ));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Sales", "Manager", "salesmanager", "password", EmployeeAccessRightEnum.SALESMANAGER ));
            
            //Partner
            partnerSessionBean.createNewPartner(new PartnerEntity("Holiday.com", "holidaydotcom", "password"));
            
            //Airport
            airportSessionBean.createNewAirport(new AirportEntity("Changi", "SIN", "Singapore", "Singapore", "Singapore", 8));
            airportSessionBean.createNewAirport(new AirportEntity("Hong Kong", "HKG", "Chek Lap Kok", "Hong Kong", "China", 8));
            airportSessionBean.createNewAirport(new AirportEntity("Taoyuan", "TPE", "Taoyuan", "Taipei", "Taiwan R.O.C.", 8));
            airportSessionBean.createNewAirport(new AirportEntity("Narita", "NRT", "Narita", "Chiba", "Japan", 9));
            airportSessionBean.createNewAirport(new AirportEntity("Sydney", "SYD", "Sydney", "New South Wales", "Australia", 11));
            
            //Aircraft Type
            aircraftTypeSessionBean.createNewAircraftType(new AircraftTypeEntity("Boeing 737", 200));
            aircraftTypeSessionBean.createNewAircraftType(new AircraftTypeEntity("Boeing 747", 400));
            
            //Aircraft Config
            CabinClassEntity cabin = new CabinClassEntity(CabinClassTypeEnum.Y, 1, 30, 6, "3-3", 180);
            AircraftConfigurationEntity config = new AircraftConfigurationEntity("Boeing 737 All Economy", 1);
            List<CabinClassEntity> list = new ArrayList<>();
            list.add(cabin);
            config = aircraftConfigurationSessionBean.createNewAircraftConfig(config, Long.parseLong("1"), list);
            
            list.clear();
            cabin = new CabinClassEntity(CabinClassTypeEnum.F, 1, 5, 2, "1-1", 10);
            list.add(cabin);
            cabin = new CabinClassEntity(CabinClassTypeEnum.J, 1, 5, 4, "2-2", 20);
            list.add(cabin);
            cabin = new CabinClassEntity(CabinClassTypeEnum.Y, 1, 25, 6, "3-3", 150);
            list.add(cabin);
            config = new AircraftConfigurationEntity("Boeing 737 Three Classes", 3);
            config = aircraftConfigurationSessionBean.createNewAircraftConfig(config, Long.parseLong("1"), list);
            
            list.clear();
            cabin = new CabinClassEntity(CabinClassTypeEnum.Y, 2, 38, 10, "3-4-3", 380);
            list.add(cabin);
            config = new AircraftConfigurationEntity("Boeing 747 All Economy", 1);
            config = aircraftConfigurationSessionBean.createNewAircraftConfig(config, Long.parseLong("2"), list);
            
            list.clear();
            cabin = new CabinClassEntity(CabinClassTypeEnum.F, 1, 5, 2, "1-1", 10);
            list.add(cabin);
            cabin = new CabinClassEntity(CabinClassTypeEnum.J, 2, 5, 6, "2-2-2", 30);
            list.add(cabin);
            cabin = new CabinClassEntity(CabinClassTypeEnum.Y, 2, 32, 10, "3-4-3", 320);
            list.add(cabin);
            config = new AircraftConfigurationEntity("Boeing 747 Three Classes", 3);
            config = aircraftConfigurationSessionBean.createNewAircraftConfig(config, Long.parseLong("2"), list);
            
            //Flight Route
            FlightRouteEntity source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("SIN"), airportSessionBean.retrieveAirportByIATA("HKG"));
            FlightRouteEntity comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("HKG"), airportSessionBean.retrieveAirportByIATA("SIN"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("SIN"), airportSessionBean.retrieveAirportByIATA("TPE"));
            comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("TPE"), airportSessionBean.retrieveAirportByIATA("SIN"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("SIN"), airportSessionBean.retrieveAirportByIATA("NRT"));
            comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("NRT"), airportSessionBean.retrieveAirportByIATA("SIN"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("HKG"), airportSessionBean.retrieveAirportByIATA("NRT"));
            comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("NRT"), airportSessionBean.retrieveAirportByIATA("HKG"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("TPE"), airportSessionBean.retrieveAirportByIATA("NRT"));
            comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("NRT"), airportSessionBean.retrieveAirportByIATA("TPE"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("SIN"), airportSessionBean.retrieveAirportByIATA("SYD"));
            comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("SYD"), airportSessionBean.retrieveAirportByIATA("SIN"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            source = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("SYD"), airportSessionBean.retrieveAirportByIATA("NRT"));
            comp = new FlightRouteEntity(airportSessionBean.retrieveAirportByIATA("NRT"), airportSessionBean.retrieveAirportByIATA("SYD"));
            flightRouteSessionBean.setComplementaryFlightRoute(source.getFlightRouteID());
            
            //Flight
            FlightEntity flight = new FlightEntity("ML111");
            FlightRouteEntity route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SIN", "HKG");
            AircraftConfigurationEntity configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            FlightEntity flightReturn = new FlightEntity("ML112");
            FlightRouteEntity routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("HKG", "SIN");
            AircraftConfigurationEntity configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML211");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SIN", "TPE");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightReturn = new FlightEntity("ML212");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("TPE", "SIN");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML311");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SIN", "NRT");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 747 Three Classes");
            flightReturn = new FlightEntity("ML312");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("NRT", "SIN");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 747 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML411");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("HKG", "NRT");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightReturn = new FlightEntity("ML412");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("NRT", "HKG");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML511");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("TPE", "NRT");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightReturn = new FlightEntity("ML512");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("NRT", "TPE");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML611");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SIN", "SYD");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightReturn = new FlightEntity("ML612");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SYD", "SIN");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML621");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SIN", "SYD");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 All Economy");
            flightReturn = new FlightEntity("ML622");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SYD", "SIN");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 737 All Economy");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            flight = new FlightEntity("ML711");
            route = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("SYD", "NRT");
            configs = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 747 Three Classes");
            flightReturn = new FlightEntity("ML712");
            routeReturn = flightRouteSessionBean.searchForFlightRouteByOriginAndDestination("NRT", "SYD");
            configsReturn = aircraftConfigurationSessionBean.retrieveAircraftConfigByName("Boeing 747 Three Classes");
            flightSessionBean.createNewFlight(flight, route.getFlightRouteID(), configs.getAircraftConfigID());
            flightSessionBean.createNewFlight(flightReturn, routeReturn.getFlightRouteID(), configsReturn.getAircraftConfigID());
            flightSessionBean.associateExistingFlightWithReturnFlight(flight.getFlightID(), flightReturn.getFlightID());
            
            //Flight Schedule Plan
            SimpleDateFormat recurrentInputFormat = new SimpleDateFormat("dd/M/yyyy");
            SimpleDateFormat scheduleFormatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss a");
            
            Date recurrentEnd = recurrentInputFormat.parse("31/12/2020");
            FlightEntity flight1 = flightSessionBean.retrieveFlightByFlightNumber("ML711");
            Date startDateTime = scheduleFormatter.parse("1/12/2020 9:00:00 AM");
            Pair<Date, Integer> pair = new Pair<>(startDateTime, 14);
            FlightSchedulePlanEntity fsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight1);
            fsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(fsp, flight1.getFlightID(), pair, 7);
            FlightEntity flight2 = flightSessionBean.retrieveFlightByFlightNumber("ML712");
            FlightSchedulePlanEntity returnFsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight2);
            List<Pair<Date,Integer>> info = new ArrayList<>();
            for(FlightScheduleEntity fs : fsp.getFlightSchedule()) {
                Calendar c = Calendar.getInstance();
                c.setTime(fs.getDepartureDateTime());
                c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + 2);
                Date date = c.getTime();
                info.add(new Pair<>(date, fs.getDuration()));
            }
            returnFsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnFsp, flight2.getFlightID(), info);
            flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(fsp.getFlightSchedulePlanID(), returnFsp.getFlightSchedulePlanID());
            FareEntity fare = new FareEntity("F001", BigDecimal.valueOf(6500));
            FareEntity fare1 = new FareEntity("F002", BigDecimal.valueOf(6000));
            FareEntity fare2 = new FareEntity("J001", BigDecimal.valueOf(3500));
            FareEntity fare3 = new FareEntity("J002", BigDecimal.valueOf(3000));
            FareEntity fare4 = new FareEntity("Y001", BigDecimal.valueOf(1500));
            FareEntity fare5 = new FareEntity("Y002", BigDecimal.valueOf(1000));
            List<CabinClassEntity> cabins = fsp.getFlight().getAircraftConfig().getCabin();
            List<CabinClassEntity> cabinsReturn = returnFsp.getFlight().getAircraftConfig().getCabin();
            fareSessionBean.createFareEntity(fare, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            
            recurrentEnd = recurrentInputFormat.parse("31/12/2020");
            flight1 = flightSessionBean.retrieveFlightByFlightNumber("ML611");
            startDateTime = scheduleFormatter.parse("1/12/2020 12:00:00 PM");
            pair = new Pair<>(startDateTime, 8);
            fsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight1);
            fsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(fsp, flight1.getFlightID(), pair, 7);
            flight2 = flightSessionBean.retrieveFlightByFlightNumber("ML612");
            returnFsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight2);
            info = new ArrayList<>();
            for(FlightScheduleEntity fs : fsp.getFlightSchedule()) {
                Calendar c = Calendar.getInstance();
                c.setTime(fs.getDepartureDateTime());
                c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + 2);
                Date date = c.getTime();
                info.add(new Pair<>(date, fs.getDuration()));
            }
            returnFsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnFsp, flight2.getFlightID(), info);
            flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(fsp.getFlightSchedulePlanID(), returnFsp.getFlightSchedulePlanID());
            fare = new FareEntity("F001", BigDecimal.valueOf(3250));
            fare1 = new FareEntity("F002", BigDecimal.valueOf(3000));
            fare2 = new FareEntity("J001", BigDecimal.valueOf(1750));
            fare3 = new FareEntity("J002", BigDecimal.valueOf(1500));
            fare4 = new FareEntity("Y001", BigDecimal.valueOf(750));
            fare5 = new FareEntity("Y002", BigDecimal.valueOf(500));
            cabins = fsp.getFlight().getAircraftConfig().getCabin();
            cabinsReturn = returnFsp.getFlight().getAircraftConfig().getCabin();
            fareSessionBean.createFareEntity(fare, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            
            recurrentEnd = recurrentInputFormat.parse("31/12/2020");
            flight1 = flightSessionBean.retrieveFlightByFlightNumber("ML621");
            startDateTime = scheduleFormatter.parse("1/12/2020 10:00:00 AM");
            pair = new Pair<>(startDateTime, 8);
            fsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight1);
            fsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(fsp, flight1.getFlightID(), pair, 7);
            flight2 = flightSessionBean.retrieveFlightByFlightNumber("ML622");
            returnFsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight2);
            info = new ArrayList<>();
            for(FlightScheduleEntity fs : fsp.getFlightSchedule()) {
                Calendar c = Calendar.getInstance();
                c.setTime(fs.getDepartureDateTime());
                c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + 2);
                Date date = c.getTime();
                info.add(new Pair<>(date, fs.getDuration()));
            }
            returnFsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnFsp, flight2.getFlightID(), info);
            flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(fsp.getFlightSchedulePlanID(), returnFsp.getFlightSchedulePlanID());
            fare4 = new FareEntity("Y001", BigDecimal.valueOf(700));
            fare5 = new FareEntity("Y002", BigDecimal.valueOf(400));
            cabins = fsp.getFlight().getAircraftConfig().getCabin();
            cabinsReturn = returnFsp.getFlight().getAircraftConfig().getCabin();
            fareSessionBean.createFareEntity(fare, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            
            recurrentEnd = recurrentInputFormat.parse("31/12/2020");
            flight1 = flightSessionBean.retrieveFlightByFlightNumber("ML311");
            startDateTime = scheduleFormatter.parse("1/12/2020 10:00:00 AM");
            pair = new Pair<>(startDateTime, 6.5);
            fsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight1);
            fsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(fsp, flight1.getFlightID(), pair, 7);
            flight2 = flightSessionBean.retrieveFlightByFlightNumber("ML312");
            returnFsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight2);
            info = new ArrayList<>();
            for(FlightScheduleEntity fs : fsp.getFlightSchedule()) {
                Calendar c = Calendar.getInstance();
                c.setTime(fs.getDepartureDateTime());
                c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + 3);
                Date date = c.getTime();
                info.add(new Pair<>(date, fs.getDuration()));
            }
            returnFsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnFsp, flight2.getFlightID(), info);
            flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(fsp.getFlightSchedulePlanID(), returnFsp.getFlightSchedulePlanID());
            fare = new FareEntity("F001", BigDecimal.valueOf(3350));
            fare1 = new FareEntity("F002", BigDecimal.valueOf(3100));
            fare2 = new FareEntity("J001", BigDecimal.valueOf(1850));
            fare3 = new FareEntity("J002", BigDecimal.valueOf(1600));
            fare4 = new FareEntity("Y001", BigDecimal.valueOf(850));
            fare5 = new FareEntity("Y002", BigDecimal.valueOf(600));
            cabins = fsp.getFlight().getAircraftConfig().getCabin();
            cabinsReturn = returnFsp.getFlight().getAircraftConfig().getCabin();
            fareSessionBean.createFareEntity(fare, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            
            recurrentEnd = recurrentInputFormat.parse("31/12/2020");
            flight1 = flightSessionBean.retrieveFlightByFlightNumber("ML411");
            startDateTime = scheduleFormatter.parse("1/12/2020 1:00:00 PM");
            pair = new Pair<>(startDateTime, 4);
            fsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight1);
            fsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlan(fsp, flight1.getFlightID(), pair, 2);
            flight2 = flightSessionBean.retrieveFlightByFlightNumber("ML412");
            returnFsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.RECURRENTWEEK, recurrentEnd, flight2);
            info = new ArrayList<>();
            for(FlightScheduleEntity fs : fsp.getFlightSchedule()) {
                Calendar c = Calendar.getInstance();
                c.setTime(fs.getDepartureDateTime());
                c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + 4);
                Date date = c.getTime();
                info.add(new Pair<>(date, fs.getDuration()));
            }
            returnFsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnFsp, flight2.getFlightID(), info);
            flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(fsp.getFlightSchedulePlanID(), returnFsp.getFlightSchedulePlanID());
            fare = new FareEntity("F001", BigDecimal.valueOf(3150));
            fare1 = new FareEntity("F002", BigDecimal.valueOf(2900));
            fare2 = new FareEntity("J001", BigDecimal.valueOf(1650));
            fare3 = new FareEntity("J002", BigDecimal.valueOf(1400));
            fare4 = new FareEntity("Y001", BigDecimal.valueOf(650));
            fare5 = new FareEntity("Y002", BigDecimal.valueOf(400));
            cabins = fsp.getFlight().getAircraftConfig().getCabin();
            cabinsReturn = returnFsp.getFlight().getAircraftConfig().getCabin();
            fareSessionBean.createFareEntity(fare, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            
            flight1 = flightSessionBean.retrieveFlightByFlightNumber("ML511");
            Date startDateTime1 = scheduleFormatter.parse("7/12/2020 5:00:00 PM");
            Date startDateTime2 = scheduleFormatter.parse("8/12/2020 5:00:00 PM");
            Date startDateTime3 = scheduleFormatter.parse("9/12/2020 5:00:00 PM");
            List<Pair<Date,Integer>> list1 = new ArrayList<>();
            list1.add(new Pair<>(startDateTime1, 3));
            list1.add(new Pair<>(startDateTime2, 3));
            list1.add(new Pair<>(startDateTime3, 3));
            fsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.MULTIPLE, flight1);
            fsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(fsp, flight1.getFlightID(), list1);
            flight2 = flightSessionBean.retrieveFlightByFlightNumber("ML512");
            returnFsp = new FlightSchedulePlanEntity(ScheduleTypeEnum.MULTIPLE, flight2);
            info = new ArrayList<>();
            for(FlightScheduleEntity fs : fsp.getFlightSchedule()) {
                Calendar c = Calendar.getInstance();
                c.setTime(fs.getDepartureDateTime());
                c.add(Calendar.HOUR_OF_DAY, fs.getDuration() + 2);
                Date date = c.getTime();
                info.add(new Pair<>(date, fs.getDuration()));
            }
            returnFsp = flightSchedulePlanSessionBean.createNewFlightSchedulePlanMultiple(returnFsp, flight2.getFlightID(), info);
            flightSchedulePlanSessionBean.associateExistingPlanToComplementaryPlan(fsp.getFlightSchedulePlanID(), returnFsp.getFlightSchedulePlanID());
            fare = new FareEntity("F001", BigDecimal.valueOf(3100));
            fare1 = new FareEntity("F002", BigDecimal.valueOf(2850));
            fare2 = new FareEntity("J001", BigDecimal.valueOf(1600));
            fare3 = new FareEntity("J002", BigDecimal.valueOf(1350));
            fare4 = new FareEntity("Y001", BigDecimal.valueOf(600));
            fare5 = new FareEntity("Y002", BigDecimal.valueOf(350));
            cabins = fsp.getFlight().getAircraftConfig().getCabin();
            cabinsReturn = returnFsp.getFlight().getAircraftConfig().getCabin();
            fareSessionBean.createFareEntity(fare, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, fsp.getFlightSchedulePlanID(), cabins.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, fsp.getFlightSchedulePlanID(), cabins.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, fsp.getFlightSchedulePlanID(), cabins.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare1, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(0).getCabinClassID());
            fareSessionBean.createFareEntity(fare2, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare3, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(1).getCabinClassID());
            fareSessionBean.createFareEntity(fare4, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
            fareSessionBean.createFareEntity(fare5, returnFsp.getFlightSchedulePlanID(), cabinsReturn.get(2).getCabinClassID());
        } catch (EmployeeUsernameExistException | UnknownPersistenceException | PartnerUsernameExistException | AirportExistException | AircraftTypeExistException | CreateNewAircraftConfigException | AircraftConfigExistException | FlightRouteNotFoundException | FlightExistException | AircraftConfigNotFoundException | FlightNotFoundException | FlightSchedulePlanNotFoundException | CabinClassNotFoundException | FareExistException | ParseException | InputDataValidationException | FlightSchedulePlanExistException | AirportNotFoundException ex) {
            System.out.println(ex.getMessage());
        }  
    }
}
