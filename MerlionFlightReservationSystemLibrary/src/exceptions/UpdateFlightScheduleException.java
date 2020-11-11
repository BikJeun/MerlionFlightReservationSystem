/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Ooi Jun Hao
 */
public class UpdateFlightScheduleException extends Exception {

    /**
     * Creates a new instance of <code>UpdateFlightScheduleException</code>
     * without detail message.
     */
    public UpdateFlightScheduleException() {
    }

    /**
     * Constructs an instance of <code>UpdateFlightScheduleException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateFlightScheduleException(String msg) {
        super(msg);
    }
}
