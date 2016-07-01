package org.korecky.bluetooth.client.hc06;

import org.korecky.bluetooth.client.hc06.enums.ServiceUUID;
import org.korecky.bluetooth.client.hc06.entity.RFCommBluetoothDevice;
import com.intel.bluetooth.RemoteDeviceHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import org.korecky.bluetooth.client.hc06.event.ScanFinishedEvent;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.ProgressUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.korecky.bluetooth.client.hc06.listener.BluetoothScanEventListener;

/**
 *
 * @author vkorecky
 */
public class BluetoothScanThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothScanThread.class);
    protected List<BluetoothScanEventListener> listenerList = new ArrayList<>();

    private UUID[] uuidSet = new UUID[]{ServiceUUID.RFCOMM.getUUID()};
    private static Object lock = new Object();
    private LocalDevice localDevice;
    private DiscoveryAgent agent;
    private List<RFCommBluetoothDevice> foundDevices = new ArrayList<>();
    private RFCommBluetoothDevice tempDevice = null;
    private int workDone = 0;
    private int workMax = 2;

    public BluetoothScanThread(BluetoothScanEventListener listener) throws BluetoothStateException {
        listenerList.add(listener);
        localDevice = LocalDevice.getLocalDevice();
        agent = localDevice.getDiscoveryAgent();
    }

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

    /**
     * @param listener
     */
    public void addNetworkStatusChangedEventListener(BluetoothScanEventListener listener) {
        listenerList.add(listener);
    }

    /**
     * @param listener
     */
    public void removeCommunicationDeviceSelectedEventListener(BluetoothScanEventListener listener) {
        listenerList.remove(listener);
    }

    /**
     * @param evt
     */
    protected void fireBluetooothEvent(EventObject evt) {
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

    public void discoverDevices() throws BluetoothStateException {
        foundDevices = new ArrayList<>();
        agent.startInquiry(DiscoveryAgent.GIAC, getDiscoveryListener());
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error when discoverDevices().", e);
            fireBluetooothEvent(new ErrorEvent(e, this));
        }
    }

    public void discoverServices(RFCommBluetoothDevice device) throws BluetoothStateException {
        this.tempDevice = device;
        agent.searchServices(null, uuidSet, this.tempDevice.getRemoteDevice(), getDiscoveryListener());
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error when discoverServices().", e);
            fireBluetooothEvent(new ErrorEvent(e, this));
        }
    }

    private DiscoveryListener getDiscoveryListener() {
        return new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
                try {
                    RFCommBluetoothDevice device = new RFCommBluetoothDevice(btDevice.getFriendlyName(false), btDevice.getBluetoothAddress(), btDevice);
                    foundDevices.add(device);
                    workDone++;
                    workMax = workMax + 2;
                    fireBluetooothEvent(new ProgressUpdatedEvent(workDone, workMax, String.format("Found bluetooth device: %s", device.getAddress()), this));
                } catch (IOException ex) {
                    LOGGER.error("Error when ask on device name.", ex);
                    fireBluetooothEvent(new ErrorEvent(ex, this));
                }
            }

            @Override
            public void inquiryCompleted(int arg0) {
                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                if (tempDevice != null) {
                    for (int i = 0; i < servRecord.length; i++) {
                        String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                        if ((url != null) && (url.toLowerCase().startsWith("btspp://"))) {
                            tempDevice.setUrl(url);
                        }
                    }
                }
            }

            @Override
            public void serviceSearchCompleted(int i, int i1) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        };
    }

    public static boolean pairDevice(RemoteDevice remoteDevice, String PIN) {
        boolean devicePaired = false;
        //check if authenticated already
        if (remoteDevice.isAuthenticated()) {
            return true;
        } else {

            System.out.println("--> Pairing device");

            try {
                boolean paired = RemoteDeviceHelper.authenticate(remoteDevice, PIN);
                //LOG.info("Pair with " + remoteDevice.getFriendlyName(true) + (paired ? " succesfull" : " failed"));
                devicePaired = paired;
                if (devicePaired) {
                    System.out.println("--> Pairing successful with device " + remoteDevice.getBluetoothAddress());
                } else {
                    System.out.println("--> Pairing unsuccessful with device " + remoteDevice.getBluetoothAddress());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("--> Pairing unsuccessful with device " + remoteDevice.getBluetoothAddress());
                devicePaired = false;
            }
            System.out.println("--> Pairing device Finish");
            return devicePaired;
        }
    }
}
