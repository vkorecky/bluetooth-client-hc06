package org.korecky.bluetooth.client.hc06;

import com.intel.bluetooth.RemoteDeviceHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

/**
 *
 * @author vkorecky
 */
public class BluetoothDiscoverTask extends Task<List<BluetoothDevice>> {

    private UUID[] uuidSet = new UUID[]{ServiceUUID.BASE_UUID_VALUE.getUUID()};
    private static Object lock = new Object();
    private LocalDevice localDevice;
    private DiscoveryAgent agent;
    private List<BluetoothDevice> foundDevices = new ArrayList<>();
    private BluetoothDevice tempDevice = null;

    public BluetoothDiscoverTask() throws BluetoothStateException {
//        uuidSet = new UUID[ServiceUUID.values().length];
//        int i = 0;
//        for (ServiceUUID uuid : ServiceUUID.values()) {
//            uuidSet[i] = uuid.getUUID();
//            i++;
//        }        
        localDevice = LocalDevice.getLocalDevice();
        agent = localDevice.getDiscoveryAgent();
    }

    @Override
    protected List<BluetoothDevice> call() throws Exception {
        discoverDevices();
        for (BluetoothDevice device : foundDevices) {
            discoverServices(device);
        }
        return foundDevices;
    }

    public void discoverDevices() throws BluetoothStateException {
        foundDevices = new ArrayList<>();
        agent.startInquiry(DiscoveryAgent.GIAC, getDiscoveryListener());
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    public void discoverServices(BluetoothDevice device) throws BluetoothStateException {
        this.tempDevice = device;
        tempDevice.getServices().clear();
        agent.searchServices(null, uuidSet, this.tempDevice.getRemoteDevice(), getDiscoveryListener());
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    private DiscoveryListener getDiscoveryListener() {
        return new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
                String name;
                try {
                    name = btDevice.getFriendlyName(false);
                } catch (Exception e) {
                    name = btDevice.getBluetoothAddress();
                }

                foundDevices.add(new BluetoothDevice(name, btDevice.getBluetoothAddress(), btDevice));
                System.out.println("device found: " + name);
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
                        if (url == null) {
                            continue;
                        }
                        Service service = new Service();
                        tempDevice.addService(service);
                        service.setUrl(url);

                        DataElement name = servRecord[i].getAttributeValue(ServiceAttribute.SERVICE_NAME.getId());
                        if (name != null) {
                            service.setName(String.valueOf(name.getValue()));
                        }

//                        DataElement protocolDescription = servRecord[i].getAttributeValue(ServiceAttribute.PROTOCOLDESCRIPTORLIST.getId());
//                        if ((protocolDescription != null) && (protocolDescription.getValue() != null)) {
//                            for (Object description : (Vector) protocolDescription.getValue()) {
//                                service.getProtocolDescriptionList().add(String.valueOf(description));
//                            }
//                        }
                        DataElement id = servRecord[i].getAttributeValue(ServiceAttribute.SERVICEID.getId());
                        if (id != null) {
                            service.setId(String.valueOf(id.getValue()));
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
