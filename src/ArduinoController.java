import com.fazecast.jSerialComm.*;

public class ArduinoController {
    private SerialPort serialPort; //Serial port (Arduino Uses COM3) ᓚ₍ ^. .^₎

    public ArduinoController(String portName) {
        //Mannage serial comunication w JSerialComm
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(9600);
        // No Arduino?
        if (!serialPort.openPort()) {
            System.out.println("No se pudo abrir el puerto serial");
        }
    }

    // Arduino is connected ฅᨐฅ
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