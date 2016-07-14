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

    /**
     * Constructor
     *
     * @param workDone Done
     * @param workMax Max
     * @param message message
     * @param source source
     */
    public ProgressUpdatedEvent(int workDone, int workMax, String message, Object source) {
        super(source);
        this.workDone = workDone;
        this.workMax = workMax;
        this.message = message;
    }

    /**
     * Gets information about finished work
     *
     * @return finished work
     */
    public int getWorkDone() {
        return workDone;
    }

    /**
     * Gets information about total amount of work
     *
     * @return total amount of work
     */
    public int getWorkMax() {
        return workMax;
    }

    /**
     * Gets message about current job
     *
     * @return job message
     */
    public String getMessage() {
        return message;
    }
}
