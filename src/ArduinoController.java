import com.fazecast.jSerialComm.*;

public class ArduinoController {
    private SerialPort serialPort;

    public ArduinoController(String portName) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(9600);
        // print in terminal if there isnt any arduino connected
        if (!serialPort.openPort()) {
            System.out.println("No se pudo abrir el puerto serial");
        }
    }

    // Arduino is connected
    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    // Send commands
    public void sendCommand(String command) {
        if (isConnected()) {
            serialPort.writeBytes(command.getBytes(), command.length());
        }
    }

    //Close port
    public void close() {
        if (isConnected()) {
            serialPort.closePort();
        }
    }
}