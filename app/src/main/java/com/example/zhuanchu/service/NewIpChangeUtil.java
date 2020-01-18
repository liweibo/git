package com.example.zhuanchu.service;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NewIpChangeUtil {
    private static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static DatagramSocket datagramSocket;

    //16进制查询指令
    public static String hexSearch = "5a4c000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    /**
     * byte[] to 16进制
     */
    public static String bytesToHex(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for (byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

    /**
     * 将16进制字符串转换为byte[]
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    //发查询指令  rerurn：工装设备返回的数据。
    //如果返回的格式不对  把返回值换成 receiveData 试试。
    public static String sendSearchInFo(Context context) {
        String gateWayIp = getGateWayIp(context);//网关地址
        DatagramPacket datagramPacket = null;
        byte[] searchTyte = toBytes(hexSearch);
        try {
            datagramPacket = new DatagramPacket
                    (searchTyte, searchTyte.length, InetAddress.getByName(gateWayIp), 1092);
            //发送连接数据包
            datagramSocket.send(datagramPacket);
            datagramSocket.setSoTimeout(2000);

            //接受工装返回的信息
            byte[] dataReceive = new byte[1024];
            DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
            datagramSocket.receive(receive);
            //读取数据
            byte[] dataRe = receive.getData();
            String hexReceive = bytesToHex(dataRe);
            String receiveData = new String(dataRe, 0, receive.getLength(), "UTF-8");

            System.out.println("16进制的返回值：" + hexReceive);
            System.out.println("未转换的返回值：" + receiveData);

            return hexReceive;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }


    public static String getGateWayIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        DhcpInfo info = wifiManager.getDhcpInfo();
        int gateway = info.gateway;
        return intToIp(gateway);
    }

    /**
     * int值转换为ip
     */
    public static String intToIp(int addr) {
        return ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

}
