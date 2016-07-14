package org.korecky.bluetooth.client.hc06.entity;

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

    /**
     * Constructor
     *
     * @param name
     * @param address
     * @param remoteDevice
     */
    public RFCommBluetoothDevice(String name, String address, RemoteDevice remoteDevice) {
        this.name = name;
        this.address = address;
        this.remoteDevice = remoteDevice;
    }

    /**
     * To string
     *
     * @return String
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets name
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets address
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets addressF
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets remote device
     *
     * @return RemoteDevice
     */
    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }

    /**
     * Sets remote device
     *
     * @param remoteDevice
     */
    public void setRemoteDevice(RemoteDevice remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    /**
     * Gets URL
     *
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets URL
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
