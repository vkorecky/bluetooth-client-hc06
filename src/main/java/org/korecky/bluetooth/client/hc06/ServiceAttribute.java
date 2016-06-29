/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.korecky.bluetooth.client.hc06;

/**
 *
 * @author vkorecky
 */
public enum ServiceAttribute {
    SERVICERECORDHANDLE(0x0000),
    SERVICECLASSIDLIST(0x0001),
    SERVICERECORDSTATE(0x0002),
    SERVICEID(0x0003),
    PROTOCOLDESCRIPTORLIST(0x0004),
    SERVICE_NAME(0x0100);

    private final int id;

    private ServiceAttribute(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
