package org.korecky.bluetooth.client.hc06.listener;

import java.util.EventListener;
import org.korecky.bluetooth.client.hc06.event.DevicesScanFinishedEvent;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.MessageReceivedEvent;
import org.korecky.bluetooth.client.hc06.event.ProgressUpdatedEvent;
import org.korecky.bluetooth.client.hc06.event.ServicesScanFinishedEvent;

/**
 *
 * @author vkorecky
 */
public interface RFCommClientEventListener extends EventListener {

    /**
     *
     * @param evt
     */
    public void error(ErrorEvent evt);

    /**
     *
     * @param evt
     */
    public void messageReceived(MessageReceivedEvent evt);
}
