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
    private final List<RFCommClientEventListener> listenerList = new ArrayList<>();
    private final StreamConnection con;
    private final String terminationChar;
    private boolean stop = false;
    private final Object lockObj = new Object();

    /**
     * RFComm client thread
     *
     * @param clientURL URL of RFComm device
     * @param terminationChar Termination char which indicates end of message
     * @param listener Listener
     * @throws IOException Exceptions
     */
    public RFCommClientThread(String clientURL, char terminationChar, RFCommClientEventListener listener) throws IOException {
        this.terminationChar = String.valueOf(terminationChar);
        listenerList.add(listener);
        con = (StreamConnection) Connector.open(clientURL);
    }

    private void fireBluetooothEvent(EventObject evt) {
        for (RFCommClientEventListener listener : listenerList) {
            if (evt instanceof ErrorEvent) {
                listener.error((ErrorEvent) evt);
            } else if (evt instanceof MessageReceivedEvent) {
                listener.messageReceived((MessageReceivedEvent) evt);
            }
        }
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        try {
            LocalDevice local = LocalDevice.getLocalDevice();
            if (con != null) {
                InputStream is = con.openInputStream();
                String messageBuffer = "";
                while (!stop) {
                    //reciever string
                    byte buffer[] = new byte[1024];
                    int bytes_read = is.read(buffer);
                    String received = new String(buffer, 0, bytes_read);
                    messageBuffer += received;
                    if (messageBuffer.contains(terminationChar)) {
                        // Wait until message is complete (wait on termination char)
                        String[] messages = messageBuffer.split(String.format("\\%s", terminationChar));
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
                    synchronized (lockObj) {
                        try {
                            lockObj.wait(100);
                        } catch (InterruptedException e) {
                            LOGGER.error("Cannot sleep thread", e);
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

    /**
     * Terminate running thread
     */
    public void terminate() {
        this.stop = true;
    }

    /**
     * Sned message to bluetooth device
     *
     * @param message String message without termination character. The
     * character is added automatically.
     */
    public void send(String message) {
        OutputStream os = null;
        try {
            message += terminationChar;
            //sender string
            if (con != null) {
                os = con.openOutputStream();
                os.write(message.getBytes());
            }
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
    
    /**
     * Wakeup thread
     */
    public void wakeup() {
        synchronized (lockObj) {
            LOGGER.trace(String.format("RFCommClientThread wakeup"));
            lockObj.notify();            
        }
    }
}
