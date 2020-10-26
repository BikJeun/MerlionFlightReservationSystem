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
public class FlightRouteExistButDisabledException extends Exception {

    /**
     * Creates a new instance of
     * <code>FlightRouteExistButDisabledException</code> without detail message.
     */
    public FlightRouteExistButDisabledException() {
    }

    /**
     * Constructs an instance of
     * <code>FlightRouteExistButDisabledException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public FlightRouteExistButDisabledException(String msg) {
        super(msg);
    }
}
