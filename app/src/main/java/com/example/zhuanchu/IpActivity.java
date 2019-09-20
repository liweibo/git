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
    private String seachCommand = "www.usr.cn";
    private String connectCommand = "+ok";
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
        final String para1 = "10.0.1.108";
        final String para2 = "255.0.0.0";
        //设置串口参数
        System.out.println("ip进来了");
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时器");
//                byte [] data2s = new byte[1024];
//                DatagramPacket datagramPacket1s =new DatagramPacket(data2s ,data2s.length);
//                //2、接收服务器响应的数据
//                try {
//                    datagramSocket.receive(datagramPacket1s);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                //3、读取数据
//                String reply = new String(data2s,0,datagramPacket1s.getLength());
//                System.out.println("这里是客户端，服务器端发来的消息：--"+ reply);
            }
        };
        timer.schedule(timerTask, 0,1000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ipChange.socketSend2Para("UART", chuankou);
                System.out.println("测试串口");
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //修改lann
                System.out.println("测试lann1");

                ipChange.socketSend2Para("LANN", para1 + "," + para2);
                System.out.println("测试lann2");

                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ipChange.socketSend1Para("Z");
            }
        }).start();
    }


}
