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
public class DeleteFlightException extends Exception {

    /**
     * Creates a new instance of <code>DeleteFlightException</code> without
     * detail message.
     */
    public DeleteFlightException() {
    }

    /**
     * Constructs an instance of <code>DeleteFlightException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteFlightException(String msg) {
        super(msg);
    }
}
