# bluetooth-client-hc06
Java library: Bluetooth client for HC06 module which is widely used in projects with Arduino boards

## Linux prerequisites
Install libbluetooth-dev package

On Ubuntu
``` bash
sudo apt-get -s install libbluetooth-dev
```

## Usage
### Maven
Add to you pom.xml file this dependency
``` xml
<dependency>
    <groupId>org.korecky</groupId>
    <artifactId>bluetooth-client-hc06</artifactId>
    <version>1.0</version>
</dependency>
```

### Demo project
You can find demo project at link:
[https://github.com/vkorecky/bluetooth-client-hc06-example](https://github.com/vkorecky/bluetooth-client-hc06-example)

### Code example
#### Find HC06 bluetooth device
``` java
	// Prepare search thread
    BluetoothScanThread scanThread = new BluetoothScanThread(new BluetoothScanEventListener() {
        @Override
        public void error(ErrorEvent evt) {
            // TODO: When error happenes
            ....
        }

        @Override
        public void scanFinished(ScanFinishedEvent evt) {
        	// TODO: When bluetooth scan finished
            ....
        }

        @Override
        public void progressUpdated(ProgressUpdatedEvent evt) {
            // TODO: When work progress is updated
            ....
        }
    });

    // Start search of bluetooth device
    scanThread.start();
```

#### Communication with HC06 bluetooth device
``` java
	RFCommBluetoothDevice selectedDevice; // Fill this object by found HC06 from previous scan
    RFCommClientThread commThread = new RFCommClientThread(selectedDevice.getUrl(), '\n', new RFCommClientEventListener() {
        @Override
        public void error(ErrorEvent evt) {
            // TODO: When error happenes
            ....
        }

        @Override
        public void messageReceived(MessageReceivedEvent evt) {
            // TODO: When message is received from HC06 module
            ....
        }
    });
    // Starts communication
    commThread.start();

    // Send message to HC06 module
    commThread.send("This is message for Arduino.");
```