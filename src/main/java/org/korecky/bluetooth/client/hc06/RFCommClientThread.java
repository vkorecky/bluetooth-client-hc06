package org.korecky.bluetooth.client.hc06;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import org.korecky.bluetooth.client.hc06.event.ErrorEvent;
import org.korecky.bluetooth.client.hc06.event.MessageReceivedEvent;
import org.korecky.bluetooth.client.hc06.listener.RFCommClientEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vkorecky
 */
public class RFCommClientThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(BluetoothScanThread.class);
    protected List<RFCommClientEventListener> listenerList = new ArrayList<>();
    private StreamConnection con;
    private String clientURL;

    public RFCommClientThread(String clientURL, RFCommClientEventListener listener) {
        listenerList.add(listener);
        this.clientURL = clientURL;
    }

    /**
     * @param listener
     */
    public void addNetworkStatusChangedEventListener(RFCommClientEventListener listener) {
        listenerList.add(listener);
    }

    /**
     * @param listener
     */
    public void removeCommunicationDeviceSelectedEventListener(RFCommClientEventListener listener) {
        listenerList.remove(listener);
    }

    /**
     * @param evt
     */
    protected void fireBluetooothEvent(EventObject evt) {
        for (RFCommClientEventListener listener : listenerList) {
            if (evt instanceof ErrorEvent) {
                listener.error((ErrorEvent) evt);
            } else if (evt instanceof MessageReceivedEvent) {
                listener.messageReceived((MessageReceivedEvent) evt);
            }
        }
    }

    public void run() {
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            con = (StreamConnection) Connector.open(clientURL);
            if (con != null) {
                InputStream is = con.openInputStream();
                String messageBuffer = "";
                while (true) {
                    //reciever string
                    byte buffer[] = new byte[1024];
                    int bytes_read = is.read(buffer);
                    String received = new String(buffer, 0, bytes_read);
                    messageBuffer += received;
                    if (messageBuffer.contains("\n")) {
                        // Wait until message is complete
                        String[] messages = messageBuffer.split("\\n");
                        if (messages.length == 1) {
                            fireBluetooothEvent(new MessageReceivedEvent(messages[0], this));
                            messageBuffer = "";
                        }
                        if (messages.length > 1) {
                            for (int i = 0; i < (messages.length - 1); i++) {
                                fireBluetooothEvent(new MessageReceivedEvent(messages[i], this));
                            }
                            messageBuffer = messages[messages.length - 1];
                        }
                    }
                }
            } else {
                LOGGER.error("Cannot initialize connection.");
                fireBluetooothEvent(new ErrorEvent(new IOException("Cannot initialize connections."), local));
            }
        } catch (Exception e) {
            System.err.print(e.toString());
        }
    }

    public void send(String message) {
        OutputStream os = null;
        try {
            //sender string
            os = con.openOutputStream();
            os.write(message.getBytes());
        } catch (IOException ex) {
            LOGGER.error("Cannot send message.", ex);
            fireBluetooothEvent(new ErrorEvent(ex, this));
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                LOGGER.error("Cannot close output stream.", ex);
                fireBluetooothEvent(new ErrorEvent(ex, this));
            }
        }
    }
}
