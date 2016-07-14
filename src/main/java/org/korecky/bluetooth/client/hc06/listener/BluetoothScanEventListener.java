package org.korecky.bluetooth.client.hc06.listener;

import java.util.EventListener;
import org.korecky.bluetooth.client.hc06.event.ScanFinishedEvent;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.ProgressUpdatedEvent;

/**
 *
 * @author vkorecky
 */
public interface BluetoothScanEventListener extends EventListener {

    /**
     * Error event
     *
     * @param evt
     */
    public void error(ErrorEvent evt);

    /**
     * Scan finished
     *
     * @param evt
     */
    public void scanFinished(ScanFinishedEvent evt);

    /**
     * Progress updated
     *
     * @param evt
     */
    public void progressUpdated(ProgressUpdatedEvent evt);
}
