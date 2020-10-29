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
public class FlightSchedulePlanNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>FlightSchedulePlanNotFoundException</code> without detail message.
     */
    public FlightSchedulePlanNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>FlightSchedulePlanNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public FlightSchedulePlanNotFoundException(String msg) {
        super(msg);
    }
}
