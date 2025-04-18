import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.util.ArrayList;
import java.util.List;
import com.fazecast.jSerialComm.*;

public class Old_Delete_Later {
    private static final int BUTTON_COUNT = 3;
    private static final Scalar[] BUTTON_COLORS = {
            new Scalar(0, 0, 255),
            new Scalar(0, 255, 0),
            new Scalar(255, 0, 0)
    };
    private static final Scalar[] ALT_BUTTON_COLORS = {
            new Scalar(0, 100, 255),
            new Scalar(100, 255, 100),
            new Scalar(255, 100, 255)
    };
    private static Rect[] buttonRects = new Rect[BUTTON_COUNT];
    private static boolean[] buttonStates = new boolean[BUTTON_COUNT];
    private static boolean[] buttonWasPressed = new boolean[BUTTON_COUNT];
    private static SerialPort serialPort;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            System.out.println("Error al abrir la cámara");
            return;
        }

        serialPort = SerialPort.getCommPort("COM3"); // Cambia según tu puerto
        serialPort.setBaudRate(9600);
        if (!serialPort.openPort()) {
            System.out.println("No se pudo abrir el puerto");
            return;
        }

        Mat frame = new Mat();
        initializeButtons(800, 600);

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
        serialPort.closePort();
        HighGui.destroyAllWindows();
    }

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
                Imgproc.drawContours(frame, contours, contours.indexOf(largestContour), new Scalar(255, 0, 255), 2);
                Imgproc.rectangle(frame, handRect.tl(), handRect.br(), new Scalar(0, 255, 255), 2);
                return handRect;
            }
        }
        return null;
    }

    private static void initializeButtons(int width, int height) {
        int btnWidth = 150, btnHeight = 80, margin = 20;
        int startY = height - btnHeight - margin;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            buttonRects[i] = new Rect(margin + i * (btnWidth + margin), startY, btnWidth, btnHeight);
            buttonStates[i] = false;
            buttonWasPressed[i] = false;
        }
    }

    private static void drawButtons(Mat frame) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            Scalar color = buttonStates[i] ? ALT_BUTTON_COLORS[i] : BUTTON_COLORS[i];
            Imgproc.rectangle(frame, buttonRects[i], color, -1);

            String text = "Botón " + (i + 1);
            Point textPos = new Point(buttonRects[i].x + 20, buttonRects[i].y + buttonRects[i].height / 2);
            Imgproc.putText(frame, text, textPos, Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255), 2);
        }
    }

    private static void handleButtonInteractions(Rect handRect) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            boolean isTouching = handRect != null && rectanglesIntersect(handRect, buttonRects[i]);

            if (isTouching && !buttonWasPressed[i]) {
                executeButtonAction(i);
                buttonWasPressed[i] = true;
            } else if (!isTouching) {
                buttonWasPressed[i] = false;
            }

            buttonStates[i] = isTouching;
        }
    }

    private static void executeButtonAction(int buttonIndex) {
        switch (buttonIndex) {
            case 0:
                sendToArduino("R");
                break;
            case 1:
                sendToArduino("G");
                break;
            case 2:
                sendToArduino("B");
                break;
        }
    }

    private static void sendToArduino(String command) {
        if (serialPort.isOpen()) {
            serialPort.writeBytes(command.getBytes(), command.length());
        }
    }

    private static boolean rectanglesIntersect(Rect rect1, Rect rect2) {
        return !(rect1.x + rect1.width < rect2.x ||
                rect2.x + rect2.width < rect1.x ||
                rect1.y + rect1.height < rect2.y ||
                rect2.y + rect2.height < rect1.y);
    }
}
