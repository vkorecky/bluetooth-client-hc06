package org.korecky.bluetooth.client.hc06.enums;

import javax.bluetooth.UUID;

/**
 *
 * @author vkorecky
 */
public enum ServiceUUID {
    /**
     * BASE_UUID_VALUE
     */
    BASE_UUID_VALUE(new UUID("0000110100001000800000805F9B34FB", false)),
    /**
     * SDP
     */
    SDP(new UUID(0x0001)),
    /**
     * RFCOMM
     */
    RFCOMM(new UUID(0x0003)),
    /**
     * OBEX
     */
    OBEX(new UUID(0x0008)),
    /**
     * HTTP
     */
    HTTP(new UUID(0x000C)),
    /**
     * L2CAP
     */
    L2CAP(new UUID(0x0100)),
    /**
     * BNEP
     */
    BNEP(new UUID(0x000F)),
    /**
     * SERIAL_PORT
     */
    SERIAL_PORT(new UUID(0x1101)),
    /**
     * SERVICEDISCOVERYSERVERSERVICECLASSID
     */
    SERVICEDISCOVERYSERVERSERVICECLASSID(new UUID(0x1000)),
    /**
     * BROWSEGROUPDESCRIPTORSERVICECLASSID
     */
    BROWSEGROUPDESCRIPTORSERVICECLASSID(new UUID(0x1001)),
    /**
     * PUBLICBROWSEGROUP
     */
    PUBLICBROWSEGROUP(new UUID(0x1002)),
    /**
     * OBEX_OBJECT_PUSH_PROFILE
     */
    OBEX_OBJECT_PUSH_PROFILE(new UUID(0x1105)),
    /**
     * OBEX_FILE_TRANSFER_PROFILE
     */
    OBEX_FILE_TRANSFER_PROFILE(new UUID(0x1106)),
    /**
     * PERSONAL_AREA_NETWORKING_USER
     */
    PERSONAL_AREA_NETWORKING_USER(new UUID(0x1115)),
    /**
     * NETWORK_ACCESS_POINT
     */
    NETWORK_ACCESS_POINT(new UUID(0x1116)),
    /**
     * GROUP_NETWORK
     */
    GROUP_NETWORK(new UUID(0x1117));

    private final UUID id;

    private ServiceUUID(UUID id) {
        this.id = id;
    }

    /**
     * Gets UUID
     *
     * @return ID
     */
    public UUID getUUID() {
        return id;
    }
}
