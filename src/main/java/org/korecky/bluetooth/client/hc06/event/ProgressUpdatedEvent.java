package org.korecky.bluetooth.client.hc06.event;

import java.util.EventObject;

/**
 *
 * @author vkorecky
 */
public class ProgressUpdatedEvent extends EventObject {

    private final int workDone;
    private final int workMax;
    private final String message;

    public ProgressUpdatedEvent(int workDone, int workMax, String message, Object source) {
        super(source);
        this.workDone = workDone;
        this.workMax = workMax;
        this.message = message;
    }

    public int getWorkDone() {
        return workDone;
    }

    public int getWorkMax() {
        return workMax;
    }

    public String getMessage() {
        return message;
    }
}
