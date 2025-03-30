
# Java Hand Light System with OpenCV and Arduino

This project implements a gesture control system that detects the user's hand through a camera and allows interaction with virtual on-screen buttons. When these buttons are touched, commands are sent to an Arduino via serial communication.

## * Technologies Used

- **OpenCV**: For image processing and hand detection
- **jSerialComm**: For serial communication with Arduino
- **Java**: Main programming language
- **Arduino**: Microcontroller that receives commands

## * System Requirements

### Hardware
- Webcam
- Arduino board connected via USB
- Computer with Java installed

### Software
- JDK 8 or higher
- OpenCV for Java
- jSerialComm library
- Java IDE (IntelliJ, Eclipse, etc.)

## * Arduino Setup

1. Connect your Arduino via USB
2. Upload the following basic sketch located in:

```bash
Java_HandLight_Control/ino/src/Arduino_Board_UwU_20250330000359.ino
```

## * Installation and Execution

1. Clone the repository:
   ```bash
   git clone git@github.com:Noodle-Dev/Java_HandLight_Control.git
   ```

2. Configure dependencies:
   - Ensure OpenCV is properly installed and configured in your project
   - Add the jSerialComm library to your classpath

3. Modify configuration:
   - Edit the COM port in `Main.java` according to your setup:
   ```java
   ArduinoController arduinoController = new ArduinoController("COM3");
   ```

4. Run the project:
   - Compile and execute the `Main` class

## * User Interface

The application displays:
- Real-time camera feed
- Three virtual buttons at the bottom
- Hand detection highlighted with contours

## * Project Structure

```
📁 project/
├── 📁 src/
│   ├── Main.java                # Entry point
│   ├── ArduinoController.java   # Arduino communication
│   ├── ButtonController.java    # Virtual button handling
│   └── HandGestureController.java # Gesture detection
├── 📁 lib/                      # External libraries
└── README.md                    # This file
```

---

<img src="https://cdn-icons-png.flaticon.com/512/197/197397.png" width="20" height="20"> **Building software with ♥ from Mexico to the world.**

---
