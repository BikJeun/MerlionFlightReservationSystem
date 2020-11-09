/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Ong Bik Jeun
 */
public class EmployeeUsernameExistException extends Exception {

    /**
     * Creates a new instance of <code>EmployeeUsernameExistException</code>
     * without detail message.
     */
    public EmployeeUsernameExistException() {
    }

    /**
     * Constructs an instance of <code>EmployeeUsernameExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeUsernameExistException(String msg) {
        super(msg);
    }
}
