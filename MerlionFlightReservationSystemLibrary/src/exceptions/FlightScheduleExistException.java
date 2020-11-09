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
public class FlightScheduleExistException extends Exception {

    /**
     * Creates a new instance of <code>FlightScheduleExistException</code>
     * without detail message.
     */
    public FlightScheduleExistException() {
    }

    /**
     * Constructs an instance of <code>FlightScheduleExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public FlightScheduleExistException(String msg) {
        super(msg);
    }
}
