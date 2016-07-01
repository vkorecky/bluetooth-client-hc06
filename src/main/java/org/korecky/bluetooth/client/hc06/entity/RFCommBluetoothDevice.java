package org.korecky.bluetooth.client.hc06.entity;

import java.util.ArrayList;
import java.util.List;
import javax.bluetooth.RemoteDevice;

/**
 *
 * @author vkorecky
 */
public class RFCommBluetoothDevice {

    String name;
    String address;
    String url;
    RemoteDevice remoteDevice;

    public RFCommBluetoothDevice(String name, String address, RemoteDevice remoteDevice) {
        this.name = name;
        this.address = address;
        this.remoteDevice = remoteDevice;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }

    public void setRemoteDevice(RemoteDevice remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
