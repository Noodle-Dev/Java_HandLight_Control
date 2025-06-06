import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

/*
 Initialize buttons and define their positions and
 their own actions depending on the color of it */

public class ButtonController {
    private final int BUTTON_COUNT = 3;
    private final Scalar[] BUTTON_COLORS = {
            new Scalar(0, 0, 255),
            new Scalar(0, 255, 0),
            new Scalar(255, 0, 0)
    };
    private final Scalar[] ALT_BUTTON_COLORS = {
            new Scalar(0, 100, 255),
            new Scalar(100, 255, 100),
            new Scalar(255, 100, 255)
    };
    private Rect[] buttonRects; //Buttons rect
    private boolean[] buttonStates; //Detect buttons states
    private boolean[] buttonWasPressed; // prevent button glitch
    private ArduinoController arduinoController; // instance of arduino modulo to send signals

    public ButtonController(int width, int height) {
        buttonRects = new Rect[BUTTON_COUNT];
        buttonStates = new boolean[BUTTON_COUNT];
        buttonWasPressed = new boolean[BUTTON_COUNT];
        initializeButtons(width, height);
    }

    public void setArduinoController(ArduinoController arduinoController) {
        this.arduinoController = arduinoController;
    }

    // Define buttons positions based on window resolution
    private void initializeButtons(int width, int height) {
        int btnWidth = 150, btnHeight = 80, margin = 20;
        int startY = height - btnHeight - margin;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            buttonRects[i] = new Rect(margin + i * (btnWidth + margin), startY, btnWidth, btnHeight);
            buttonStates[i] = false;
            buttonWasPressed[i] = false;
        }
    }

    // Draw buttons on window
    public void drawButtons(Mat frame) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            Scalar color = buttonStates[i] ? ALT_BUTTON_COLORS[i] : BUTTON_COLORS[i];
            Imgproc.rectangle(frame, buttonRects[i], color, -1);

            String text = "Botón " + (i + 1);
            Point textPos = new Point(buttonRects[i].x + 20, buttonRects[i].y + buttonRects[i].height / 2);
            Imgproc.putText(frame, text, textPos, Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255), 2);
        }
    }

    // For every button, checks if hand is in button
    public void handleButtonInteractions(Rect handRect) {
        for (int i = 0; i < BUTTON_COUNT; i++) {
            boolean isTouching = handRect != null && rectanglesIntersect(handRect, buttonRects[i]);

            if (isTouching && !buttonWasPressed[i]) {
                //Func to execute the action
                executeButtonAction(i);
                buttonWasPressed[i] = true;
            } else if (!isTouching) {
                buttonWasPressed[i] = false;
            }

            buttonStates[i] = isTouching;
        }
    }

    //Send signal to arduino /ᐠ - ˕ -マ
    private void executeButtonAction(int buttonIndex) {
        if (arduinoController == null) return;

        //Signals stored in the Arduino
        switch (buttonIndex) {
            case 0:
                arduinoController.sendCommand("R"); // Action available in /ino src/Arduino_Board_UWU2025033
                break;
            case 1:
                arduinoController.sendCommand("G"); // Action available in /ino src/Arduino_Board_UWU2025033
                break;
            case 2:
                arduinoController.sendCommand("B"); // Action available in /ino src/Arduino_Board_UWU2025033
                break;
        }
    }

    //Detect if two rectangles intersect
    private boolean rectanglesIntersect(Rect rect1, Rect rect2) {
        return !(rect1.x + rect1.width < rect2.x ||
                rect2.x + rect2.width < rect1.x ||
                rect1.y + rect1.height < rect2.y ||
                rect2.y + rect2.height < rect1.y);
    }
}