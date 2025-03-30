#define LED_ROJO 3
#define LED_VERDE 5
#define LED_AZUL 6

void setup() {
    Serial.begin(9600);
    pinMode(LED_ROJO, OUTPUT);
    pinMode(LED_VERDE, OUTPUT);
    pinMode(LED_AZUL, OUTPUT);
}

void loop() {
    if (Serial.available()) {
        char command = Serial.read();

        digitalWrite(LED_ROJO, LOW);
        digitalWrite(LED_VERDE, LOW);
        digitalWrite(LED_AZUL, LOW);

        if (command == 'R') {
            digitalWrite(LED_ROJO, HIGH);
        } else if (command == 'G') {
            digitalWrite(LED_VERDE, HIGH);
        } else if (command == 'B') {
            digitalWrite(LED_AZUL, HIGH);
        }
    }
}
