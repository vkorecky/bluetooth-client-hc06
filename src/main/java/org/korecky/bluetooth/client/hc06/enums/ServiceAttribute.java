/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.korecky.bluetooth.client.hc06.enums;

/**
 *
 * @author vkorecky
 */
public enum ServiceAttribute {
    /**
     * SERVICERECORDHANDLE
     */
    SERVICERECORDHANDLE(0x0000),
    /**
     * SERVICECLASSIDLIST
     */
    SERVICECLASSIDLIST(0x0001),
    /**
     * SERVICERECORDSTATE
     */
    SERVICERECORDSTATE(0x0002),
    /**
     * SERVICEID
     */
    SERVICEID(0x0003),
    /**
     * PROTOCOLDESCRIPTORLIST
     */
    PROTOCOLDESCRIPTORLIST(0x0004),
    /**
     * SERVICE_NAME
     */
    SERVICE_NAME(0x0100);

    private final int id;

    private ServiceAttribute(int id) {
        this.id = id;
    }

    /**
     * Gets ID
     *
     * @return ID
     */
    public int getId() {
        return id;
    }
}
