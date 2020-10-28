#include <LedControl.h>
#include <SoftwareSerial.h>
#include <nRF24L01.h>
#include <RF24.h>
SoftwareSerial BTSerial(3, 2);
RF24 radio(7, 4);
  byte buffer[1024];
  int bufferPosition;
LedControl dot = LedControl(12, 11, 10, 1);
const byte address[6] = "00001"; //주소값을 5가지 문자열로 변경할 수 있으며, 송신기와 수신기가 동일한 주소로 해야됨.
int hum= 8;
int hsensor;
int f=0;
int sc=0;
bool ran = 0;
int ra = 0;
byte child1[]={
  B00111100,
  B00111100,
  B00011000,
  B01111110,
  B01011010,
  B00011000,
  B00111100,
  B00100100
};  
byte child2[]={
  B00111100,
  B10111100,
  B01011000,
  B00111100,
  B00011010,
  B00011001,
  B01111110,
  B01000010
};
byte child3[]={
  B00111100,
  B00111101,
  B00011010,
  B00111100,
  B01011000,  
  B10011000,
  B01111110,
  B01000010
};
byte drive1[]={
  B00111100,
  B01000010,
  B10000001,
  B11111111,
  B10011001,  
  B10011001,
  B01011010,
  B00111100
};
byte drive2[]={
  B00111100,
  B01000110,
  B10001111,
  B10011001,
  B10111001,
  B11100101,
  B01100010,
  B00111100
};
byte drive3[]={
  B00111100,
  B01100010,
  B11110001,
  B10011001,
  B10011001,
  B10100111,
  B01000110,
  B00111100
};
byte park[]={
  B00111100,
  B01111010,
  B10100101,
  B10100101,
  B10111001,
  B10100001,
  B01100010,
  B00111100
};
byte call1[]={
  B01100000,
  B11110000,
  B11100000,
  B01000000,
  B00100010,
  B00010111,
  B00001111,
  B00000110
};
byte call2[]={
  B00111100,
  B01100110,
  B11100111,
  B01011010,
  B00111100,
  B01100110,
  B11111111,
  B11111111
};
byte heart1[]={
  B00000000,
  B00000000,
  B10000000,
  B10000000,
  B11000000,
  B01100000,
  B00110000,
  B00010000
};
byte heart2[]={
  B00000000,
  B00000000,
  B11000000,
  B11100000,
  B11110000,
  B01111000,
  B00111100,
  B00011000
};
byte heart3[]={
  B00000000,
  B01100000,
  B11111000,
  B11111100,
  B11111110,
  B01111110,
  B00111100,
  B00011000
};
byte heart4 []={
  B00000000,
  B01100110,
  B11111111,
  B11111111,
  B11111111,
  B01111110,
  B00111100,
  B00011000
};
byte normal[]={
  B00000000,
  B00000000,
  B00000000,
  B00000000,
  B00000000,
  B00000000,
  B00000000,
  B00000000
};
byte sz[]={
  B00111100,
  B01011010,
  B10100101,
  B10010001,
  B10001001,
  B10100101,
  B01011010,
  B00111100
};

void setup() {
  // put your setup code here, to run once:
  pinMode(hum, INPUT);
  BTSerial.begin(9600);
  bufferPosition = 0;
  dot.shutdown(0, false);
  dot.setIntensity(0, 10);
  dot.clearDisplay(0);
  /*radio.begin();
  radio.openWritingPipe(address); // 데이터를 보낼 주소 설정
  radio.stopListening(); // Listening을 멈춤
  radio.setPALevel(RF24_PA_LOW);*/
} 

void loop() {
  // put your main code here, to run repeatedly:
  Serial.begin(9600);
  if(Serial.available()){
    char Data = (char)Serial.read();
    if(Data == '1'){
      for(int j=0; j<2; j++){
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, child1[i]);
          }
          delay(500);
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, child2[i]);
          }
          delay(500); 
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, child3[i]);
          }
          delay(500);
      }
   }
  }
    else if(BTSerial.available()){
      byte data = BTSerial.read();
      if(data == '1') {
        for(int j=0; j<5; j++){
          dot.clearDisplay(0);
          delay(200);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, heart1[i]);
          }
          delay(100);
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, heart2[i]);
          }
          delay(100); 
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, heart3[i]);
          }
          delay(100);
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, heart4[i]);
          }
          delay(300);
        }
      }
      if(data == '2'){
        dot.clearDisplay(0);
        sc = 0;
      }
      if(data == '3'){
        sc=1;
        for(int j=0; j<5; j++){
          dot.clearDisplay(0);
          delay(200);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, sz[i]);
          }
          delay(500);
        }
      }
      if(data == '4'){
        for(int j=0; j<5; j++){
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, call1[i]);
          }
          delay(500);
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, call2[i]);
          }
          delay(500); 
        }
      }
      if(data == '5'){
        
      }
      if(data == '6'){
        dot.clearDisplay(0);
      }
      if(data == '7'){
        for(int j=0; j<5; j++){
          dot.clearDisplay(0);
          delay(200);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, park[i]);
          }
          delay(500);
        }
      }
      if(data == '8'){
        for(int j=0; j<5; j++){
          dot.clearDisplay(0);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, drive1[i]);
          }
          delay(500);
          dot.clearDisplay(0);
          delay(60);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, drive2[i]);
          }
          delay(500); 
          dot.clearDisplay(0);
          delay(60);
          for(int i=0; i<8; i++){
            dot.setRow(0, i, drive3[i]);
          }
          delay(500);
        }
      }
       buffer[bufferPosition++] = data;     
 
     if(data == '\n'){     // 문자열 종료 표시
      buffer[bufferPosition] = '\0';

      // 스마트폰으로 전송할 문자열을 시리얼 모니터에 출력
      Serial.print("안드로이드로 전송한 값 : "); 
      BTSerial.write(buffer, bufferPosition);
    //   BTSerial.write(Serial.read()); 
      bufferPosition = 0;
    }
   }
}
