import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

public class FaceDetection {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        CascadeClassifier faceCascade = new CascadeClassifier("haarcascade_frontalface_default.xml");
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: No se pudo abrir la cámara");
            return;
        }

        Mat frame = new Mat();
        while (true) {
            camera.read(frame);
            if (frame.empty()) {
                System.out.println("Error: Fotograma vacío");
                break;
            }

            MatOfRect faces = new MatOfRect();
            faceCascade.detectMultiScale(frame, faces);

            for (Rect rect : faces.toArray()) {
                Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0), 2);
            }

            HighGui.imshow("Detección de Cara", frame);
            if (HighGui.waitKey(30) == 27) { // Presiona 'ESC' para salir
                break;
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
    }
}