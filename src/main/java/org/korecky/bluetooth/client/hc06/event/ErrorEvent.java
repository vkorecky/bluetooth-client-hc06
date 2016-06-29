package org.korecky.bluetooth.client.hc06.event;

import java.util.EventObject;

/**
 *
 * @author vkorecky
 */
public class ErrorEvent extends EventObject {

    private final Exception error;

    public ErrorEvent(Exception error, Object source) {
        super(source);
        this.error = error;
    }

    public Exception getError() {
        return error;
    }
}
