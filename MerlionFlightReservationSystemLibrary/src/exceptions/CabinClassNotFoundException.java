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
public class CabinClassNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CabinClassNotFoundException</code>
     * without detail message.
     */
    public CabinClassNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CabinClassNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CabinClassNotFoundException(String msg) {
        super(msg);
    }
}
