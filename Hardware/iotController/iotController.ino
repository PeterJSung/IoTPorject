#include <SoftwareSerial.h>

SoftwareSerial BTSerial(2, 3); 
byte buffer[1024]; // 데이터를 수신 받을 버퍼

const int COUNT_MAX = 100;
int purify_count = 0;

void setup() {
  // put your setup code here, to run once:
  BTSerial.begin(9600);
  Serial.begin(9600);
  Serial.println("BLUETOOTH INIT!");  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (BTSerial.available()) { 
    byte data = BTSerial.read(); // 수신 받은 데이터 저장
    Serial.println((char)data);
      
    if((char)data == 'E'){
      int sub = 0;
      int subLen = 0;
      char emotionValue[9] = {0,};
      for(int i = 0 ; i < 10; i++){
        sub += analogRead(A1);
      }
      sub = (int)((double)sub/10);
      
      while(sub){
        int splitedData = sub%10;
        emotionValue[subLen] = (char)(splitedData + '0');
        sub /= 10;
        subLen++;
      }
      
      char passData[9] = {'e', 0 };
      int writeIndex = 1;
      for(int idx = subLen -1; idx>=0; idx--,writeIndex++){
        passData[writeIndex] = emotionValue[idx];
        
      }
      passData[writeIndex] = '#';
      passData[writeIndex + 1] = 0;
      BTSerial.write(passData);
    } else if((char)data == 'W'){
      Serial.println("W MATCHING");
      purify_count = COUNT_MAX;
    }
  }
  
  if(purify_count > 0){
    analogWrite(5, 255);
    purify_count--;
    Serial.println(purify_count);
  } else {
    analogWrite(5, 0);
  }
  
  //int soil = analogRead(A1);
  //Serial.println(soil);
  //analogWrite(5, 255);
}
