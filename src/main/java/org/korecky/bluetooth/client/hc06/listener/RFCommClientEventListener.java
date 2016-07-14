package org.korecky.bluetooth.client.hc06.listener;

import java.util.EventListener;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.MessageReceivedEvent;

/**
 *
 * @author vkorecky
 */
public interface RFCommClientEventListener extends EventListener {

    /**
     * Error event
     *
     * @param evt
     */
    public void error(ErrorEvent evt);

    /**
     * Message received from bluetooth device
     *
     * @param evt
     */
    public void messageReceived(MessageReceivedEvent evt);
}
