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
public class AircraftConfigNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>AircraftConfigNotFoundException</code>
     * without detail message.
     */
    public AircraftConfigNotFoundException() {
    }

    /**
     * Constructs an instance of <code>AircraftConfigNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AircraftConfigNotFoundException(String msg) {
        super(msg);
    }
}
