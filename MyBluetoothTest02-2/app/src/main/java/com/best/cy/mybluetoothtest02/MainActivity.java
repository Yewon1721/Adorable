package com.best.cy.mybluetoothtest02;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.media.AudioManager;
import android.media.SoundPool;

public class MainActivity extends AppCompatActivity {
    SoundPool soundPool;
    SoundManager soundManager;
    Button button;
    boolean play;
    int playSoundId;

    Button b1, b2, b3, b4, b5, b6;
    Button btnAllOn, btnAllOff;
    TextView receiveData;
    Boolean connecting = false;

    static final int REQUEST_ENABLE_BT = 10;
    BluetoothAdapter mBluetoothAdapter;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    Thread mWorkerThread = null;

    byte[] readBuffer;
    int bufferPosition;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // 블루투스가 활성 상태로 변경됨
                    selectPairedDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    // 블루투스가 비활성 상태임
                    Toast.makeText(getApplicationContext(), "블루투스가 비활성 상태임!", Toast.LENGTH_SHORT);
                    finish();    // 어플리케이션 종료
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void activateBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않습니다!", Toast.LENGTH_SHORT);
            //finish();
        } else {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {


                selectPairedDevice();
            }
        }

    }

    void deactivateBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    void selectPairedDevice() {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = pairedDevices.size();


        if (mPairedDeviceCount == 0) {

            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다!", Toast.LENGTH_SHORT);
            finish();        // 액티비티 종료
        }


        final List<String> listDevices = new ArrayList<String>();
        for (BluetoothDevice device : pairedDevices) {
            listDevices.add(device.getName());
        }

        ArrayAdapter mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listDevices);
        final ListView listView = findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

       final String[] items = listDevices.toArray(new String[listDevices.size()]);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (connecting == false)
                    connectToBluetoothDevice(items[position]);
                connecting = true;
                findViewById(R.id.selectBT).setVisibility(View.INVISIBLE);
                findViewById(R.id.listview).setVisibility(View.INVISIBLE);
            }
        });

    }

    void receiveData() {
        final Handler handler = new Handler();

        readBuffer = new byte[1024];    // 수신 버퍼
        bufferPosition = 0;        // 버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {

                    try {
                        int bytesAvailable = mInputStream.available();    // 수신 데이터 크기를 bytesAvailable에 저장

                        if (bytesAvailable > 0) {     //수신한 데이터가 있으면
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes); //스트림을 상요하여 packetBytes 에 데이터 넣기

                            int i = 0;
                            while (i < bytesAvailable) {

                                if (packetBytes[i] == '\n') {
                                    final String data = new String(readBuffer, "US-ASCII");
                                    bufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            receiveData.setText(data);
                                        }

                                    });
                                } else {
                                    readBuffer[bufferPosition++] = packetBytes[i];
                                }

                                i += 1;
                            }   //end of for
                        }
                    } catch (IOException ex) {
                        // 데이터 수신 중 오류 발생
                        Toast.makeText(getApplicationContext(), "데이터 수신 중 오류 발생!", Toast.LENGTH_SHORT);
                        finish();
                    }
                }
            }
        });

        mWorkerThread.start();
    }

    void transmitData(String msg) {
        msg += "\n";

        try {
            mOutputStream.write(msg.getBytes());        // 문자열 전송
        } catch (Exception e) {
            // 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "문자열 전송 오류가 발생한 경우!", Toast.LENGTH_SHORT);
            finish();        // 액티비티 종료
        }
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : pairedDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }

        return selectedDevice;
    }

    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt();
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    void connectToBluetoothDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);

            mSocket.connect();

            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            receiveData();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "connect error", Toast.LENGTH_SHORT).show();
           finish();        // 앱 종료
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveData = findViewById(R.id.receiveData);

        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);

        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);

        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);

        btnAllOn = findViewById(R.id.btnAllOn);
        btnAllOff= findViewById(R.id.btnAllOff);


        //button = findViewById(R.id.button); //롤리팝 이상 버전일 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              soundPool = new SoundPool.Builder().build();
        } else {
            //롤리팝 이하 버전일 경우 //new SoundPool(1번,2번,3번)
            // 1번 - 음악 파일 갯수 //2번 - 스트림 타입 //3번 - 음질
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        }
        soundManager = new SoundManager(this,soundPool);
        soundManager.addSound(0, R.raw.first);
        soundManager.addSound(1, R.raw.ch2);
        soundManager.addSound(2, R.raw.nopark);
        soundManager.addSound(3, R.raw.driving);

        btnAllOn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("7");
                //Toast.makeText(getApplicationContext(), "7", Toast.LENGTH_SHORT).show();
                playSoundId = soundManager.playSound(2);
            }
        });

        btnAllOff.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("8");
                //Toast.makeText(getApplicationContext(), "8", Toast.LENGTH_SHORT).show();

                    playSoundId = soundManager.playSound(0);
            }
        });

        b1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("1");
                //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
                playSoundId = soundManager.playSound(3);

            }
        });

        b2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("2");
                //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("3");
                //Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
                playSoundId = soundManager.playSound(1);
            }
        });

        b4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("4");
                //Toast.makeText(getApplicationContext(), "4", Toast.LENGTH_SHORT).show();
            }
        });

        b5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("5");
                //Toast.makeText(getApplicationContext(), "5", Toast.LENGTH_SHORT).show();
                deactivateBluetooth();
                finish();
            }
        });

        b6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                transmitData("6");
                //Toast.makeText(getApplicationContext(), "6", Toast.LENGTH_SHORT).show();
            }
        });
        activateBluetooth();
    }

}