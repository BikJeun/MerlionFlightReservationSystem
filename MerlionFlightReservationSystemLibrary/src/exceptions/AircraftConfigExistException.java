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
public class AircraftConfigExistException extends Exception {

    /**
     * Creates a new instance of <code>AircraftConfigExistException</code>
     * without detail message.
     */
    public AircraftConfigExistException() {
    }

    /**
     * Constructs an instance of <code>AircraftConfigExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public AircraftConfigExistException(String msg) {
        super(msg);
    }
}
