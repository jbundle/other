/*
 *
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.control.serial;

public class SerialControlException extends Exception {

    /**
     * Constructs a <code>SerialConnectionException</code>
     * with the specified detail message.
     *
     * @param   s   the detail message.
     */
    public SerialControlException(String str) {
        super(str);
    }

    /**
     * Constructs a <code>SerialControlException</code>
     * with no detail message.
     */
    public SerialControlException() {
        super();
    }
}
