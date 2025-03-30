import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class HandGestureController {
    public Rect detectHand(Mat frame) {
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
}