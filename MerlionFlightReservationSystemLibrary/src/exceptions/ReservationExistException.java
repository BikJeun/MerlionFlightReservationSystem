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
public class ReservationExistException extends Exception {

    /**
     * Creates a new instance of <code>ReservationExistException</code> without
     * detail message.
     */
    public ReservationExistException() {
    }

    /**
     * Constructs an instance of <code>ReservationExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ReservationExistException(String msg) {
        super(msg);
    }
}
