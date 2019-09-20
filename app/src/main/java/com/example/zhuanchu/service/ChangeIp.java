package com.example.zhuanchu.service;

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

    //获取本机IP
    public String getIp() {
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();
            String localname = ia.getHostName();
            String localip = ia.getHostAddress();
            System.out.println("本机名称是：" + localname);
            System.out.println("本机的ip是 ：" + localip);
            System.out.println("localHost打印：" + ia);
            return localip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public void socketSend2Para(String comm, String para) {
        int port = 8001;//本地的端口
        byte[] data1 = seachCommand.getBytes();
        for (int i = 0; i <data1.length; i++) {
            System.out.println("数组打印："+i+"--"+data1[i]);
        }
        //创建数据报，包含发送的信息
        DatagramPacket datagramPacket = null;
        try {
            String ip1 = getIp();

//            System.out.println("ip前半部分：" + ip3);
            String ip4 = "10.0.255.255";
            datagramPacket = new DatagramPacket
                    (data1, data1.length, InetAddress.getByName(ip4), 48899);
//            datagramSocket.bind(new InetSocketAddress(InetAddress.getByName(getIp()), port));
            //发送连接数据包
            datagramSocket.send(datagramPacket);
            datagramSocket.setSoTimeout(200);


            byte[] datas = connectCommand.getBytes();
            datagramPacket = new DatagramPacket
                    (datas, datas.length, InetAddress.getByName(ip4), 48899);
            datagramSocket.send(datagramPacket);
            ////发送UDP数据包
            datagramSocket.setSoTimeout(200);


            if (para != null) {
                byte[] data = ("AT+" + comm + "=" + para + "\r").getBytes();
                datagramPacket = new DatagramPacket
                        (data, data.length, InetAddress.getByName(ip4), 48899);
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


    public void socketSend1Para(String comm) {
        byte[] data1 = seachCommand.getBytes();
        DatagramPacket datagramPacket = null;
        try {
            String ip4 = "10.0.255.255";
            datagramPacket = new DatagramPacket
                    (data1, data1.length, InetAddress.getByName(ip4), 48899);
            datagramSocket.send(datagramPacket);
            datagramSocket.setSoTimeout(200);

            byte[] datas = connectCommand.getBytes();
            datagramPacket = new DatagramPacket
                    (datas, datas.length, InetAddress.getByName(ip4), 48899);
            datagramSocket.send(datagramPacket);
            ////发送UDP数据包
            if (comm != null) {
                byte[] data = ("AT+" + comm + "\r").getBytes();
                datagramPacket = new DatagramPacket
                        (data, data.length, InetAddress.getByName(ip4), 48899);
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








}
