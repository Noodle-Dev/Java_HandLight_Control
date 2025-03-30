# README - Control por Gestos con OpenCV y Arduino

## 📌 Descripción del Proyecto

Este proyecto implementa un sistema de control por gestos que detecta la mano del usuario a través de una cámara y permite interactuar con botones virtuales en pantalla. Al tocar estos botones, se envían comandos a un Arduino a través de comunicación serial.

## 🛠 Tecnologías Utilizadas

- **OpenCV**: Para procesamiento de imágenes y detección de la mano
- **jSerialComm**: Para comunicación serial con Arduino
- **Java**: Lenguaje principal del proyecto
- **Arduino**: Microcontrolador que recibe los comandos

## 📋 Requisitos del Sistema

### Hardware
- Cámara web
- Placa Arduino conectada por USB
- Computador con Java instalado

### Software
- JDK 8 o superior
- OpenCV para Java
- Biblioteca jSerialComm
- IDE para Java (IntelliJ, Eclipse, etc.)

## 🔌 Configuración de Arduino

1. Conecta tu Arduino al puerto USB
2. Carga el siguiente sketch básico:

```arduino
void setup() {
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
  if (Serial.available() > 0) {
    char command = Serial.read();
    
    switch(command) {
      case 'R':
        // Acción para botón rojo
        digitalWrite(LED_BUILTIN, HIGH);
        break;
      case 'G':
        // Acción para botón verde
        digitalWrite(LED_BUILTIN, LOW);
        break;
      case 'B':
        // Acción para botón azul
        // Implementa tu lógica aquí
        break;
    }
  }
}
```

## 🚀 Instalación y Ejecución

1. Clona el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   ```

2. Configura las dependencias:
   - Asegúrate de tener OpenCV correctamente instalado y configurado en tu proyecto
   - Agrega la biblioteca jSerialComm a tu classpath

3. Modifica la configuración:
   - Edita el puerto COM en `Main.java` según tu configuración
   ```java
   ArduinoController arduinoController = new ArduinoController("COM3");
   ```

4. Ejecuta el proyecto:
   - Compila y ejecuta la clase `Main`

## 🖥 Interfaz de Usuario

La aplicación muestra:
- Vista de la cámara en tiempo real
- Tres botones virtuales en la parte inferior
- Detección de la mano resaltada con contornos

## 🎨 Estructura del Proyecto

```
📁 proyecto/
├── 📁 src/
│   ├── Main.java                # Punto de entrada
│   ├── ArduinoController.java   # Comunicación con Arduino
│   ├── ButtonController.java    # Manejo de botones virtuales
│   └── HandGestureController.java # Detección de gestos
├── 📁 lib/                      # Bibliotecas externas
└── README.md                    # Este archivo
```

## ⚙️ Personalización

Puedes modificar:
- Colores de los botones en `ButtonController.java`
- Umbrales de detección de color en `HandGestureController.java`
- Comandos enviados a Arduino en `ButtonController.java`

## 🤝 Contribución

Si deseas contribuir al proyecto:
1. Haz un fork del repositorio
2. Crea una rama con tu feature (`git checkout -b feature/awesome-feature`)
3. Haz commit de tus cambios (`git commit -m 'Add awesome feature'`)
4. Haz push a la rama (`git push origin feature/awesome-feature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo licencia MIT. Consulta el archivo LICENSE para más detalles.
