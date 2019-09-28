package com.example.zhuanchu.service;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.example.zhuanchu.MyApplication;

import java.io.IOException;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.*;

public class ChangeIp {
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

    public static String getBeforeThteeIp(String sbIP){//传入设备ip 或者网关ip都行
        int indexLastDot = sbIP.lastIndexOf(".");
        char[] arrIP = sbIP.toCharArray();
        String twoDotIp = "";//前三位的ip 去掉了最后一位
        for (int i = 0; i < indexLastDot; i++) {
            twoDotIp += arrIP[i];
        }
        return twoDotIp;
    }

    public void socketSend2ParaTEst(Context context, String command, String para) {
        String gateWayIp = getGateWayIp(context);//网关地址
        byte[] data1 = seachCommand.getBytes();//String seachCommand = "www.usr.cn";
        //创建数据报，包含发送的信息
        DatagramPacket datagramPacket = null;
        try {
            datagramPacket = new DatagramPacket
                    (data1, data1.length, InetAddress.getByName(gateWayIp), 48899);
            //发送连接数据包
            datagramSocket.send(datagramPacket);
            datagramSocket.setSoTimeout(1000);

            //receive
//            byte[] data2s = new byte[1024];
//            DatagramPacket receive = new DatagramPacket(data2s, data2s.length);
//            //接收服务器响应的数据
//            try {
//                datagramSocket.receive(receive);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            //读取数据
//            byte[] dataRe = receive.getData();
//            String serverData = new String(dataRe, 0, receive.getLength(), "UTF-8");
//            System.out.println("这里是客户端，服务器端发来的消息：--" + serverData);

            //发送+ok 没有返回数据的
            byte[] datas = connectCommand.getBytes();//String connectCommand = "+ok";
            datagramPacket = new DatagramPacket
                    (datas, datas.length, InetAddress.getByName(gateWayIp), 48899);
            datagramSocket.send(datagramPacket);
            datagramSocket.setSoTimeout(1000);


            if (para != "") {//发指令和参数 其实就是设置值
                byte[] data = ("AT+" + command + "=" + para + "\r").getBytes();
                datagramPacket = new DatagramPacket
                        (data, data.length, InetAddress.getByName(gateWayIp), 48899);
                datagramSocket.send(datagramPacket);
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//非首次发送修改指令，就不用发连接指令与+ok指令了。
    public void socketSend2ParaTEstSecond(Context context, String command, String para) {
        String gateWayIp = getGateWayIp(context);//网关地址
        DatagramPacket datagramPacket = null;
        try {
            if (para != "") {//发指令和参数 其实就是设置值
                byte[] data = ("AT+" + command + "=" + para + "\r").getBytes();
                datagramPacket = new DatagramPacket
                        (data, data.length, InetAddress.getByName(gateWayIp), 48899);
                datagramSocket.send(datagramPacket);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//发送重启指令
    public void socketSendQueryCommChongqi( Context context) {
        DatagramPacket datagramPacket = null;
        try {
                byte[] data = ("AT+" + "Z" + "\r").getBytes();
                datagramPacket = new DatagramPacket
                        (data, data.length, InetAddress.getByName(getGateWayIp(context)), 48899);
                datagramSocket.send(datagramPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //发送查询指令
    public void socketSendQueryComm(String comm, Context context) {
        DatagramPacket datagramPacket = null;
        try {
            if (comm != null && comm != "") {
                byte[] data = ("AT+" + comm + "\r").getBytes();
                datagramPacket = new DatagramPacket
                        (data, data.length, InetAddress.getByName(getGateWayIp(context)), 48899);
                datagramSocket.send(datagramPacket);
                datagramSocket.setSoTimeout(1000);


                //receive
                byte[] data2s = new byte[1024];
                DatagramPacket receive = new DatagramPacket(data2s, data2s.length);
                //接收服务器响应的数据
                try {
                    datagramSocket.receive(receive);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //读取数据
                byte[] dataRe = receive.getData();
                String serverData = new String(dataRe, 0, receive.getLength(), "UTF-8");
                System.out.println("这里是客户端，服务器端发来的消息，查询值：--------" + serverData);

            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String getGateWayIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        DhcpInfo info = wifiManager.getDhcpInfo();
        int gateway = info.gateway;
        return intToIp(gateway);
    }

    /**
     * int值转换为ip
     *
     * @param addr
     * @return
     */
    public static String intToIp(int addr) {
        return ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }


}
