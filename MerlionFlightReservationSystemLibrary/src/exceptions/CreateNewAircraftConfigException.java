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
public class CreateNewAircraftConfigException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewAircraftConfigException</code>
     * without detail message.
     */
    public CreateNewAircraftConfigException() {
    }

    /**
     * Constructs an instance of <code>CreateNewAircraftConfigException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewAircraftConfigException(String msg) {
        super(msg);
    }
}
