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
public class CabinClassExistException extends Exception {

    /**
     * Creates a new instance of <code>CabinClassExistExeception</code> without
     * detail message.
     */
    public CabinClassExistException() {
    }

    /**
     * Constructs an instance of <code>CabinClassExistExeception</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CabinClassExistException(String msg) {
        super(msg);
    }
}
