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
public class FareNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>FareNotFoundException</code> without
     * detail message.
     */
    public FareNotFoundException() {
    }

    /**
     * Constructs an instance of <code>FareNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FareNotFoundException(String msg) {
        super(msg);
    }
}
