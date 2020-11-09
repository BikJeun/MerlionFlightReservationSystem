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
public class PartnerUsernameExistException extends Exception {

    /**
     * Creates a new instance of <code>PartnerUsernameExistException</code>
     * without detail message.
     */
    public PartnerUsernameExistException() {
    }

    /**
     * Constructs an instance of <code>PartnerUsernameExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public PartnerUsernameExistException(String msg) {
        super(msg);
    }
}
