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
public class UpdateFareException extends Exception {

    /**
     * Creates a new instance of <code>UpdateFareException</code> without detail
     * message.
     */
    public UpdateFareException() {
    }

    /**
     * Constructs an instance of <code>UpdateFareException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateFareException(String msg) {
        super(msg);
    }
}
