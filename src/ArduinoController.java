import com.fazecast.jSerialComm.*;

public class ArduinoController {
    private SerialPort serialPort;

    public ArduinoController(String portName) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(9600);
        if (!serialPort.openPort()) {
            System.out.println("No se pudo abrir el puerto serial");
        }
    }

    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    public void sendCommand(String command) {
        if (isConnected()) {
            serialPort.writeBytes(command.getBytes(), command.length());
        }
    }

    public void close() {
        if (isConnected()) {
            serialPort.closePort();
        }
    }
}