#include<SPI.h>
#include <Adafruit_NeoPixel.h>
#include "nRF24L01.h"
#include "RF24.h"
#define CE_PIN 7
#define CSN_PIN 8
#define PIN 6                      // 디지털핀 어디에 연결했는지 입력
#define LEDNUM 12                  // 연결된 네오픽셀의 숫자입력
#define BRIGHTNESS 30              // 네오픽셀의 밝기를 설정합니다. (0~255)
RF24 radio(CE_PIN, CSN_PIN);
byte adress[6] = {"00001"};
char send_text[2] = {'0', '1'};
Adafruit_NeoPixel strip = Adafruit_NeoPixel(LEDNUM, PIN, NEO_GRBW + NEO_KHZ800);
void setup() {
  Serial.begin(9600);
  // put your setup code here, to run once
  pinMode(6, OUTPUT);
  pinMode(5, OUTPUT);
  radio.begin();
  radio.setPALevel(RF24_PA_MIN);
  radio.openReadingPipe(0, adress);
  radio.startListening();
  strip.setBrightness(BRIGHTNESS);
}

void loop() {
  strip.begin();  // 네오픽셀 제어시작
  strip.show();// 네오픽셀 초기화
  if(radio.available()){
    char text;
    radio.read(&text, sizeof(text));
    switch(text)
    {
      case '0':
        colorWipe(strip.Color(255, 200, 0), 30);
        colorWipe(strip.Color(255, 100, 0), 30);
        //Serial.println(text);
        break;
        
      case '1':
        colorWipe(strip.Color(255, 0, 0), 30);
        colorWipe(strip.Color(0, 0, 0), 30);
        colorWipe(strip.Color(255, 0, 0), 30);
        colorWipe(strip.Color(0, 0, 0), 30);
        colorWipe(strip.Color(255, 0, 0), 30);
        colorWipe(strip.Color(0, 0, 0), 30);
        //Serial.println(text);
        break;
    }
  }
}
void colorWipe(uint32_t c, uint8_t wait) {
  for(uint16_t i=0; i<strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
      strip.show();
      delay(wait);
  }
}
