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
public class FareExistException extends Exception {

    /**
     * Creates a new instance of <code>FareExistException</code> without detail
     * message.
     */
    public FareExistException() {
    }

    /**
     * Constructs an instance of <code>FareExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FareExistException(String msg) {
        super(msg);
    }
}
