package org.korecky.bluetooth.client.hc06;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import org.korecky.bluetooth.client.hc06.entity.RFCommBluetoothDevice;
import org.korecky.bluetooth.client.hc06.enums.ServiceUUID;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.ProgressUpdatedEvent;
import org.korecky.bluetooth.client.hc06.event.ScanFinishedEvent;
import org.korecky.bluetooth.client.hc06.listener.BluetoothScanEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vkorecky
 */
public class BluetoothScanThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothScanThread.class);
    private final List<BluetoothScanEventListener> listenerList = new ArrayList<>();

    private final UUID[] uuidSet = new UUID[]{ServiceUUID.RFCOMM.getUUID()};
    private static final Object LOCK = new Object();
    private final LocalDevice localDevice;
    private final DiscoveryAgent agent;
    private List<RFCommBluetoothDevice> foundDevices = new ArrayList<>();
    private RFCommBluetoothDevice tempDevice = null;
    private int workDone = 0;
    private int workMax = 2;

    /**
     * Thread for scan bluetooth devices
     *
     * @param listener Listener
     * @throws BluetoothStateException Exceptions
     */
    public BluetoothScanThread(BluetoothScanEventListener listener) throws BluetoothStateException {
        listenerList.add(listener);
        localDevice = LocalDevice.getLocalDevice();
        agent = localDevice.getDiscoveryAgent();
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        try {
            fireBluetooothEvent(new ProgressUpdatedEvent(workDone, workMax, "Starting search of devices", this));
            List<RFCommBluetoothDevice> rfCommDices = new ArrayList<>();
            discoverDevices();
            fireBluetooothEvent(new ProgressUpdatedEvent(workDone, workMax, "Starting search of services", this));
            for (RFCommBluetoothDevice device : foundDevices) {
                discoverServices(device);
                if (device.getUrl() != null) {
                    rfCommDices.add(device);
                }
                workDone++;
                fireBluetooothEvent(new ProgressUpdatedEvent(workDone, workMax, String.format("Services scanned: %s", device.getAddress()), this));
            }
            fireBluetooothEvent(new ProgressUpdatedEvent(workMax, workMax, "Search of RFComm bluetooth devices finished", this)
            );
            fireBluetooothEvent(new ScanFinishedEvent(rfCommDices, this));
        } catch (Throwable ex) {
            LOGGER.error("Error when try scann bluetooth devices.", ex);
            fireBluetooothEvent(new ErrorEvent(ex, this));
        }
    }

    private void fireBluetooothEvent(EventObject evt) {
        for (BluetoothScanEventListener listener : listenerList) {
            if (evt instanceof ErrorEvent) {
                listener.error((ErrorEvent) evt);
            } else if (evt instanceof ProgressUpdatedEvent) {
                listener.progressUpdated((ProgressUpdatedEvent) evt);
            } else if (evt instanceof ScanFinishedEvent) {
                listener.scanFinished((ScanFinishedEvent) evt);
            }
        }
    }

    private void discoverDevices() throws BluetoothStateException {
        foundDevices = new ArrayList<>();
        agent.startInquiry(DiscoveryAgent.GIAC, getDiscoveryListener());
        try {
            synchronized (LOCK) {
                LOCK.wait();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error when discoverDevices().", e);
            fireBluetooothEvent(new ErrorEvent(e, this));
        }
    }

    private void discoverServices(RFCommBluetoothDevice device) throws BluetoothStateException {
        this.tempDevice = device;
        agent.searchServices(null, uuidSet, this.tempDevice.getRemoteDevice(), getDiscoveryListener());
        try {
            synchronized (LOCK) {
                LOCK.wait();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error when discoverServices().", e);
            fireBluetooothEvent(new ErrorEvent(e, this));
        }
    }

    private DiscoveryListener getDiscoveryListener() {
        return new DiscoveryListener() {
            /**
             * deviceDiscovered
             *
             * @param btDevice bluetooth device
             * @param arg1 class
             */
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
                try {
                    String friendlyName = null;
                    try {
                        friendlyName = btDevice.getFriendlyName(false);
                    } catch (Throwable ex) {
                        LOGGER.warn("Cannot get firendly name of device.", ex);
                    }
                    RFCommBluetoothDevice device = new RFCommBluetoothDevice(friendlyName, btDevice.getBluetoothAddress(), btDevice);
                    foundDevices.add(device);
                    workDone++;
                    workMax = workMax + 2;
                    fireBluetooothEvent(new ProgressUpdatedEvent(workDone, workMax, String.format("Found bluetooth device: %s", device.getAddress()), this));
                } catch (Throwable ex) {
                    LOGGER.error("Error when ask on device name.", ex);
                    fireBluetooothEvent(new ErrorEvent(ex, this));
                }
            }

            /**
             * inquiryCompleted
             *
             * @param arg0 int
             */
            @Override
            public void inquiryCompleted(int arg0) {
                synchronized (LOCK) {
                    LOCK.notify();
                }
            }

            /**
             * servicesDiscovered
             *
             * @param transID ID
             * @param servRecord service record
             */
            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                if (tempDevice != null) {
                    for (ServiceRecord servRecord1 : servRecord) {
                        String url = servRecord1.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                        if ((url != null) && (url.toLowerCase().startsWith("btspp://"))) {
                            tempDevice.setUrl(url);
                        }
                    }
                }
            }

            /**
             * serviceSearchCompleted
             *
             * @param i i
             * @param i1 i1
             */
            @Override
            public void serviceSearchCompleted(int i, int i1) {
                synchronized (LOCK) {
                    LOCK.notify();
                }
            }
        };
    }
}
