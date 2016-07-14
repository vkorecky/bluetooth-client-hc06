package org.korecky.bluetooth.client.hc06.event;

import java.util.EventObject;

/**
 *
 * @author vkorecky
 */
public class ErrorEvent extends EventObject {

    private final Throwable error;

    /**
     * Constructor
     *
     * @param error Exception
     * @param source Source
     */
    public ErrorEvent(Throwable error, Object source) {
        super(source);
        this.error = error;
    }

    /**
     * Gets error
     *
     * @return Exception
     */
    public Throwable getError() {
        return error;
    }
}
