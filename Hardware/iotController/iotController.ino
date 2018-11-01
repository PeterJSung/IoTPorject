#include <SoftwareSerial.h>

SoftwareSerial BTSerial(2, 3); 
byte buffer[1024]; // 데이터를 수신 받을 버퍼
int bufferPosition; // 버퍼에 데이타를 저장할 때 기록할 위치

void setup() {
  // put your setup code here, to run once:
  BTSerial.begin(9600);
  Serial.begin(9600);
  bufferPosition = 0;
  Serial.println("BLUETOOTH INIT!");
  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (BTSerial.available()) { 
    byte data = BTSerial.read(); // 수신 받은 데이터 저장
    if((char)data == 'C'){
      char passData[3] = {'c','#',0};
      BTSerial.write(passData);
    } 
    
    Serial.write(data);
    //Serial.write(data); // 수신된 데이터 시리얼 모니터로 출력
    //buffer[bufferPosition++] = data; 
  }
  int soil = analogRead(A1);
  Serial.println(soil);
}
