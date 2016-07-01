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
     *
     * @param evt
     */
    public void error(ErrorEvent evt);

    /**
     *
     * @param evt
     */
    public void scanFinished(ScanFinishedEvent evt);

    /**
     *
     * @param evt
     */
    public void progressUpdated(ProgressUpdatedEvent evt);
}
