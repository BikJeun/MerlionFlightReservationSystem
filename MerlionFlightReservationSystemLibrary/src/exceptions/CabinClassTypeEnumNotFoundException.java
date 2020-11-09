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
public class CabinClassTypeEnumNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>CabinClassTypeEnumNotFoundException</code> without detail message.
     */
    public CabinClassTypeEnumNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>CabinClassTypeEnumNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public CabinClassTypeEnumNotFoundException(String msg) {
        super(msg);
    }
}
