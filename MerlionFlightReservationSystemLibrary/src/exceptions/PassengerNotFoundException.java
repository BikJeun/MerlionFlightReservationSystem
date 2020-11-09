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
public class PassengerNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>PassengerNotFoundException</code> without
     * detail message.
     */
    public PassengerNotFoundException() {
    }

    /**
     * Constructs an instance of <code>PassengerNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public PassengerNotFoundException(String msg) {
        super(msg);
    }
}
