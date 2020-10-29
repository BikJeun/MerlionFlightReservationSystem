/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ejb.session.singleton;

import ejb.session.stateless.AircraftTypeSessionBeanLocal;
import ejb.session.stateless.AirportSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.AircraftTypeEntity;
import entity.AirportEntity;
import entity.EmployeeEntity;
import entity.PartnerEntity;
import enumeration.EmployeeAccessRightEnum;
import exceptions.AircraftTypeExistException;
import exceptions.AirportExistException;
import exceptions.EmployeeNotFoundException;
import exceptions.EmployeeUsernameExistException;
import exceptions.PartnerUsernameExistException;
import exceptions.UnknownPersistenceException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author Ong Bik Jeun
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {
    
    @EJB
    private AircraftTypeSessionBeanLocal aircraftTypeSessionBean;
    
    @EJB
    private AirportSessionBeanLocal airportSessionBean;
    
    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;
    
    
    
    public DataInitSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        
        try {
            employeeSessionBean.retrieveEmployeeByUsername("Employee 01");
        } catch (EmployeeNotFoundException ex) {
            doDataInitialisation();
        }
    }
    
    private void doDataInitialisation() {
        try {
            /*Initialise Employees*/
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Ong", "Bik Jeun", "Employee 01", "password", EmployeeAccessRightEnum.ADMINISTRATOR));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Ooi", "Jun Hao", "Employee 02", "password", EmployeeAccessRightEnum.ADMINISTRATOR));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Udall", "Tofanelli", "Employee 03", "password", EmployeeAccessRightEnum.FLEETMANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Corby", "Delahunty", "Employee 04", "password", EmployeeAccessRightEnum.ROUTEPLANNER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Aleda", "Lippett", "Employee 05", "password", EmployeeAccessRightEnum.SALESMANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Tuesday", "Tortice", "Employee 06", "password", EmployeeAccessRightEnum.SCHEDULEMANAGER));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Symon", "O'Hallihane", "Employee 07", "password", EmployeeAccessRightEnum.EMPLOYEE));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Gustavus", "Haylor", "Employee 08", "password", EmployeeAccessRightEnum.EMPLOYEE));
            employeeSessionBean.createNewEmployee(new EmployeeEntity("Eloisa", "Giacomazzo", "Employee 09", "password", EmployeeAccessRightEnum.EMPLOYEE));
            
            /*Initialise Partner*/
            partnerSessionBean.createNewPartner(new PartnerEntity("Holiday.com", "Partner 01", "password"));
            
            /*Initialise Airport*/
            airportSessionBean.createNewAirport(new AirportEntity("Changi Airport", "SIN", "Singapore", "Singapore", "Singapore", 8));
            airportSessionBean.createNewAirport(new AirportEntity("Narita International Airport", "NRT", "Narita", "Chiba", "Japan", 9));
            airportSessionBean.createNewAirport(new AirportEntity("Incheon International Airport", "ICN", "Seoul", "Seoul", "South Korea", 9));
            airportSessionBean.createNewAirport(new AirportEntity("Sydney Airport", "SYD", "Sydney", "New South Wales", "Australia", 11));
            airportSessionBean.createNewAirport(new AirportEntity("Sendai Airport", "SDJ", "Sendai", "Miyagi", "Japan", 9));
            airportSessionBean.createNewAirport(new AirportEntity("Taoyuan International Airport", "TPE", "Taoyuan", "Taipei", "Taiwan", 8));
            
            /*Initialise Aircraft Type*/
            aircraftTypeSessionBean.createNewAircraftType(new AircraftTypeEntity("Boeing 737", 189));
            aircraftTypeSessionBean.createNewAircraftType(new AircraftTypeEntity("Boeing 747", 416));
            
        } catch (EmployeeUsernameExistException | UnknownPersistenceException | PartnerUsernameExistException | AirportExistException | AircraftTypeExistException ex) {
            System.out.println(ex.getMessage());
        }
    } 
}
