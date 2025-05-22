import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/*
 *          IMPORTANT: Please check the "README.md" file in Git repo before running this code
 *                                       <( °^° )>
 *          Running the program without meeting requirements may cause problems
 */

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load Native OpenCv library

        VideoCapture capture = new VideoCapture(0); //Opens camera ( 0 Main Camera )
        if (!capture.isOpened()) {
            //If the camera isn't detected
            System.out.println("Error al abrir la cámara");
            return;
        }

        // Load Arduino Module
        ArduinoController arduinoController = new ArduinoController("COM3"); // Port COM3 from arduibo
        if (!arduinoController.isConnected()) {
            //If arduino is not open
            System.out.println("No se pudo abrir el puerto");
            return;
        }

        //Start other modules
        HandGestureController gestureController = new HandGestureController(); // Instance  Hand Module
        ButtonController buttonController = new ButtonController(800, 600); // Instance Buttons Module w/ buttons size
        buttonController.setArduinoController(arduinoController); // Instance arduino controller module

        //Frame capture loop
        Mat frame = new Mat();
        while (true) {
            capture.read(frame);
            if (frame.empty()) break; //Frame empty, break the loopp

            Imgproc.resize(frame, frame, new Size(800, 600)); //Rezize the frame to 800x600

            // Detect gestures
            Rect handRect = gestureController.detectHand(frame);
            buttonController.drawButtons(frame); // Draw buttons
            buttonController.handleButtonInteractions(handRect); //Check interacions

            HighGui.imshow("Control por Mano", frame); // Show Img
            if (HighGui.waitKey(10) == 27) break; //Exit if escape key is pressed
        }

        //End process ≽^•⩊•^≼


        //Release resources
        capture.release();
        arduinoController.close();
        HighGui.destroyAllWindows();
    }
}