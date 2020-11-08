/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Mitsuki
 */
public class PassengerAlreadyExistException extends Exception {

    /**
     * Creates a new instance of <code>PassengerAlreadyExistException</code>
     * without detail message.
     */
    public PassengerAlreadyExistException() {
    }

    /**
     * Constructs an instance of <code>PassengerAlreadyExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PassengerAlreadyExistException(String msg) {
        super(msg);
    }
}
