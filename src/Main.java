import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Error al abrir la cámara");
            return;
        }

        //Create module from "ArduinoController" in COM3 port
        ArduinoController arduinoController = new ArduinoController("COM3");
        if (!arduinoController.isConnected()) {
            System.out.println("No se pudo abrir el puerto");
            return;
        }

        //Start other modules
        HandGestureController gestureController = new HandGestureController();
        ButtonController buttonController = new ButtonController(800, 600);
        buttonController.setArduinoController(arduinoController);

        Mat frame = new Mat(); // Frame Capture
        while (true) {
            capture.read(frame);
            if (frame.empty()) break;

            Imgproc.resize(frame, frame, new Size(800, 600));

            // Detect gestures
            Rect handRect = gestureController.detectHand(frame);
            buttonController.drawButtons(frame); // Draw buttons
            buttonController.handleButtonInteractions(handRect); //Check interacions

            HighGui.imshow("Control por Mano", frame); // Show Img
            if (HighGui.waitKey(10) == 27) break; //Exit if escape key is pressed
        }

        //End process

        capture.release();
        arduinoController.close();
        HighGui.destroyAllWindows();
    }
}