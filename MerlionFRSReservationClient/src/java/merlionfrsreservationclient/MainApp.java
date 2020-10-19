/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merlionfrsreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.CustomerEntity;
import exceptions.CustomerExistException;
import exceptions.InvalidLoginCredentialException;
import exceptions.UnknownPersistenceException;
import java.util.Scanner;

/**
 *
 * @author Ong Bik Jeun
 */
public class MainApp {
    
    private CustomerSessionBeanRemote customerSessionBean;
    private boolean login;
    private CustomerEntity currentCustomer;
    
    public MainApp(CustomerSessionBeanRemote customerSessionBean) {
        this.customerSessionBean = customerSessionBean;
    }
    
    public void runApp() {
        
        while (true) {
            
            if(!login) {
                Scanner sc = new Scanner(System.in);
                Integer response = 0;
                
                System.out.println("=== Welcome to Merlion Airlines ===\n");
                System.out.println("1: Customer Log in");
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
                            //mainMenu();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                CustomerEntity customer = new CustomerEntity(firstname, lastname, idNumber, contactnumber, address, postalcode, username, password, null);
                currentCustomer = customerSessionBean.createNewCustomerEntity(customer);
                login = true;
                System.out.println("Account successfully created: " + currentCustomer.getIdentificationNumber()+"\n");
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
            System.out.println("2: View Flight Reservations");
            System.out.println("3: View Flight Reservation Details");
            System.out.println("4: Log Out");
            
            response = 0;
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if(response == 1) {
                    //reserveFight
                } else if(response == 2) {
                    //viewFlight
                } else if (response == 3) {
                    //viewFlightDets
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
}
