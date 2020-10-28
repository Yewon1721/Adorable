#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include <SoftwareSerial.h>
#define CE_PIN 7
#define CSN_PIN 8
//SoftwareSerial mySerial(0, 1);
int motion = 4;

RF24 radio(CE_PIN, CSN_PIN);
byte address[6] = {"00001"};
char send_text[2]={'0', '1'};
void setup()
{
//  mySerial.begin(2400);
  pinMode(motion, INPUT);
  Serial.begin(9600);
  radio.begin();
  radio.setPALevel(RF24_PA_MIN);//LOW,MAX
  radio.openReadingPipe(0,address);
  radio.stopListening();  
}

void loop()
{
  int hsensor = digitalRead(motion);
 // Serial.println(hsensor);
    radio.stopListening();
    radio.openWritingPipe(address);
    int ret;
    
  if(hsensor == HIGH){
    ret=radio.write(&send_text[1], sizeof(send_text[1]));
    Serial.println('1');
    //delay(1000);
  }
  
  else {
    ret=radio.write(&send_text[0], sizeof(send_text[0]));
    Serial.println('0');
    //delay(1000);
  }
    delay(600);
  }
