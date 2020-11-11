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
public class ItineraryNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ItineraryNotFoundException</code> without
     * detail message.
     */
    public ItineraryNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ItineraryNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ItineraryNotFoundException(String msg) {
        super(msg);
    }
}
