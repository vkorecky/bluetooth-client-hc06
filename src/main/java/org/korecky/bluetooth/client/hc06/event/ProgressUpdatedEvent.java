package org.korecky.bluetooth.client.hc06.event;

import java.util.EventObject;

/**
 *
 * @author vkorecky
 */
public class ProgressUpdatedEvent extends EventObject {

    private final double workDone;
    private final double workMax;
    private final String message;

    public ProgressUpdatedEvent(double workDone, double workMax, String message, Object source) {
        super(source);
        this.workDone = workDone;
        this.workMax = workMax;
        this.message = message;
    }

    public double getWorkDone() {
        return workDone;
    }

    public double getWorkMax() {
        return workMax;
    }

    public String getMessage() {
        return message;
    }
}
