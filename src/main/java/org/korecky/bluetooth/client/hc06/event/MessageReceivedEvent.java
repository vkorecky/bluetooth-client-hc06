package org.korecky.bluetooth.client.hc06.event;

import java.util.EventObject;

/**
 *
 * @author vkorecky
 */
public class MessageReceivedEvent extends EventObject {

    private final String message;

    /**
     * Constructor
     *
     * @param message received message
     * @param source Source
     */
    public MessageReceivedEvent(String message, Object source) {
        super(source);
        this.message = message;
    }

    /**
     * Gets received message
     *
     * @return received message
     */
    public String getMessage() {
        return message;
    }
}
