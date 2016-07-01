package org.korecky.bluetooth.client.hc06.event;

import java.util.EventObject;

/**
 *
 * @author vkorecky
 */
public class ErrorEvent extends EventObject {

    private final Throwable error;

    public ErrorEvent(Throwable error, Object source) {
        super(source);
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }
}
