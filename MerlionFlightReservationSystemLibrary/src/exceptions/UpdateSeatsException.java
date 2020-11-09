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
public class UpdateSeatsException extends Exception {

    /**
     * Creates a new instance of <code>UpdateSeatsException</code> without
     * detail message.
     */
    public UpdateSeatsException() {
    }

    /**
     * Constructs an instance of <code>UpdateSeatsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateSeatsException(String msg) {
        super(msg);
    }
}
