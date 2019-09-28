package com.example.zhuanchu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ThemedSpinnerAdapter;

import com.example.zhuanchu.service.ChangeIp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class IpActivity extends AppCompatActivity {
    static Socket server;
    private static DatagramSocket datagramSocket;

    static {
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        final ChangeIp ipChange = new ChangeIp();
        final String chuankou = "115200" + "," + "8" + "," + "1" + "," + "NONE" + "," + "NFC";
        final String ipForchange = "10.0.1.130";
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置串口
                ipChange.socketSend2ParaTEst(IpActivity.this, "UART", chuankou);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //修改网关IP
                ipChange.socketSend2ParaTEstSecond(IpActivity.this, "LANN", ipForchange);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发重启指令
                ipChange.socketSendQueryCommChongqi(IpActivity.this);
            }
        }).start();
    }


}
