import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Main {
    // Configuración de botones
    private static final int BUTTON_COUNT = 3;
    private static final Scalar[] BUTTON_COLORS = {
            new Scalar(0, 0, 255),   // Rojo
            new Scalar(0, 255, 0),    // Verde
            new Scalar(255, 0, 0)     // Azul
    };
    private static final Scalar[] ALT_BUTTON_COLORS = {
            new Scalar(0, 100, 255),  // Naranja
            new Scalar(100, 255, 100),// Verde claro
            new Scalar(255, 100, 255) // Rosa
    };
    private static Rect[] buttonRects = new Rect[BUTTON_COUNT];
    private static boolean[] buttonStates = new boolean[BUTTON_COUNT];
    private static boolean[] buttonWasPressed = new boolean[BUTTON_COUNT];

    // Comunicación con el puerto serie
    private static SerialPort serialPort;
    private static OutputStream outputStream;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            System.out.println("Error al abrir la cámara");
            return;
        }

        // Inicializar botones y puerto serie
        initializeButtons(800, 600);
        connectToArduino("COM3");  // Cambia esto según tu puerto

        Mat frame = new Mat();

        while (true) {
            capture.read(frame);
            if (frame.empty()) break;

            Imgproc.resize(frame, frame, new Size(800, 600));
            Rect handRect = detectHand(frame);
            drawButtons(frame);
            handleButtonInteractions(handRect);

            HighGui.imshow("Control por Mano", frame);
            if (HighGui.waitKey(10) == 27) break;
        }

        capture.release();
        HighGui.destroyAllWindows();
        closeSerialConnection();
    }

    // Conectar con Arduino a través del puerto serie
    private static void connectToArduino(String portName) {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                System.out.println("El puerto está en uso.");
                return;
            }
            CommPort commPort = portIdentifier.open("ArduinoControl", 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                outputStream = serialPort.getOutputStream();
            } else {
                System.out.println("Error: No es un puerto serie.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cerrar conexión con Arduino
    private static void closeSerialConnection() {
        try {
            if (outputStream != null) outputStream.close();
            if (serialPort != null) serialPort.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Enviar datos al Arduino
    private static void sendData(String data) {
        try {
            if (outputStream != null) {
                outputStream.write((data + "\n").getBytes());
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Detección de mano
    private static Rect detectHand(Mat frame) {
        Mat hsvFrame = new Mat();
        Mat mask = new Mat();
        Mat hierarchy = new Mat();

        Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsvFrame, new Scalar(0, 40, 60), new Scalar(25, 255, 255), mask);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (!contours.isEmpty()) {
            MatOfPoint largestContour = contours.stream()
                    .max((c1, c2) -> Double.compare(Imgproc.contourArea(c1), Imgproc.contourArea(c2)))
                    .orElse(null);

            if (largestContour != null) {
                Rect handRect = Imgproc.boundingRect(largestContour);
                Imgproc.rectangle(frame, handRect.tl(), handRect.br(), new Scalar(0, 255, 255), 2);
                return handRect;
            }
        }
        return null;
    }

    // Inicialización de botones
    private static void initializeButtons(int width, int height) {
        int btnWidth = 150, btnHeight = 80, margin = 20;
        int startY = height - btnHeight - margin;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            buttonRects[i] = new Rect(margin + i * (btnWidth + margin), startY, btnWidth, btnHeight);
            buttonStates[i] = false;
            buttonWasPressed[i] = false;
        }
    }

    // Dibujar botones
    private static void drawButtons(Mat frame) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            Scalar color = buttonStates[i] ? ALT_BUTTON_COLORS[i] : BUTTON_COLORS[i];
            Imgproc.rectangle(frame, buttonRects[i], color, -1);
            Imgproc.putText(frame, "Botón " + (i + 1), new Point(buttonRects[i].x + 20, buttonRects[i].y + 50),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255), 2);
        }
    }

    // Manejo de interacciones
    private static void handleButtonInteractions(Rect handRect) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            boolean isTouching = handRect != null && rectanglesIntersect(handRect, buttonRects[i]);

            if (isTouching && !buttonWasPressed[i]) {
                executeButtonAction(i);
                buttonWasPressed[i] = true;
            } else if (!isTouching && buttonWasPressed[i]) {
                sendData("LED" + (i + 1) + "_OFF");
                buttonWasPressed[i] = false;
            }

            buttonStates[i] = isTouching;
        }
    }

    private static void executeButtonAction(int buttonIndex) {
        sendData("LED" + (buttonIndex + 1) + "_ON");
    }

    private static boolean rectanglesIntersect(Rect rect1, Rect rect2) {
        return !(rect1.x + rect1.width < rect2.x ||
                rect2.x + rect2.width < rect1.x ||
                rect1.y + rect1.height < rect2.y ||
                rect2.y + rect2.height < rect1.y);
    }
}
