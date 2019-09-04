package com.example.zhuanchu.service;

import com.alibaba.fastjson.JSON;
import com.example.zhuanchu.bean.pojo15.CMDitem;
import com.example.zhuanchu.bean.pojo15.JsonRootBean15;

import java.util.ArrayList;
import java.util.List;

public class GetInfoForMultiList {
    //            机车 动车 城轨 下拉列表数据获取
    public static List<String> jiDongCheng(String json, Class<JsonRootBean15> clazz) {
        JsonRootBean15
                jr = JSON.parseObject(json, clazz);
        List<String> JuTi = new ArrayList<>();
        JuTi.add(jr.getDatalist().getLocomotive().getName());
        JuTi.add(jr.getDatalist().getMotorTrain().getName());
        JuTi.add(jr.getDatalist().getUrbanrail().getName());
        System.out.println("车型：" + JuTi.toString());
        return JuTi;
    }

    //            机车 动车 城轨 分别对应的具体车型下拉列表数据获取
    public static List<String> jiDongChengCheXing(String json, Class<JsonRootBean15> clazz, String cheXing) {//需要传入车型  根据用户选中的 机车或 动车 城轨 车型

        JsonRootBean15
                jr = JSON.parseObject(json, clazz);
        List<String> JuTiCheXing = new ArrayList<>();
        JuTiCheXing.clear();
        if (cheXing.equals("机车") ){
            int jutiChexingSize = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < jutiChexingSize; i++) {
                String nameChe = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getName();
                if (!nameChe.equals(" ")) {
                    JuTiCheXing.add(nameChe);
                }
            }

        } else if (cheXing.equals("动车") ){
            int jutiChexingSize = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < jutiChexingSize; i++) {
                String nameChe = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getName();
                if (!nameChe.equals(" ")) {
                    JuTiCheXing.add(nameChe);
                }
            }

        } else if (cheXing.equals("城轨") ){
            int jutiChexingSize = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < jutiChexingSize; i++) {
                String nameChe = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getName();
                if (!nameChe.equals(" ")) {
                    JuTiCheXing.add(nameChe);
                }
            }
        }
        System.out.println("具体车型：" + JuTiCheXing.toString());

        return JuTiCheXing;
    }


    //            某个具体车型下的 ABC或者车号 列表...如：HXD1C->A
    public static List<String> jiDongChengCheXingCheHao(String json, Class<JsonRootBean15> clazz, String cheXing, String JuTicheXing) {
        JsonRootBean15
                jr = JSON.parseObject(json, clazz);
        List<String> chehaolist = new ArrayList<>();
        if (cheXing.equals("机车") ){
            int getTrainTypeInfo = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String jutichehaostr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().get(j).getName();
//                        if (!jutichehaostr.equals(" ")) {
                            chehaolist.add(jutichehaostr);//得到具体车型的所有车号的 列表
//                        }
                    }
                }
            }
        } else if (cheXing.equals("动车") ){
            int getTrainTypeInfo = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String jutichehaostr = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().get(j).getName();
                        if (!jutichehaostr.equals(" ")) {
                            chehaolist.add(jutichehaostr);//得到具体车型的所有车号的 列表
                        }
                    }
                }
            }
        } else if (cheXing.equals("城轨") ){
            int getTrainTypeInfo = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String jutichehaostr = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().get(j).getName();
                        if (!jutichehaostr.equals(" ")) {
                            chehaolist.add(jutichehaostr);//得到具体车型的所有车号的 列表
                        }
                    }
                }
            }
        }
        System.out.println("对应的车号：" + chehaolist.toString());
        System.out.println("对应的车号：" + chehaolist.size());

        return chehaolist;
    }


    //            某个具体车型下的ABC或者车号de 设备列表...如：HXD1C->A->  ERM...

    //当没有车号时  如何处理
    public static List<String> jiDongChengCheXingCheHaoSheBei(String json, Class<JsonRootBean15> clazz, String cheXing, String JuTicheXing, String CheHao) {
        JsonRootBean15
                jr = JSON.parseObject(json, clazz);
        List<String> chehaoShebeilist = new ArrayList<>();

        if (cheXing.equals("机车") ){
            int getTrainTypeInfo = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (!CheHao.equals(" ") && carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (!sheBeiStr.equals(" ")) {
                                    chehaoShebeilist.add(sheBeiStr);
                                }
                            }
                        } else if (CheHao.equals(" ")) {

                            int sizeItem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (!sheBeiStr.equals(" ")) {
                                    chehaoShebeilist.add(sheBeiStr);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else if (cheXing.equals("动车") ){
            int getTrainTypeInfo = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (!sheBeiStr.equals(" ")) {
                                    chehaoShebeilist.add(sheBeiStr);
                                }
                            }
                        }
                    }
                }
            }
        } else if (cheXing.equals("城轨") ){
            int getTrainTypeInfo = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (!sheBeiStr.equals(" ")) {
                                    chehaoShebeilist.add(sheBeiStr);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("对应的车号中的设备列表：" + chehaoShebeilist.toString());

        return chehaoShebeilist;
    }






    //            某个具体车型下的ABC或者车号de 设备的新，老CMD或者没有...如：HXD1C->A->  ERM->cmd...

    public static List<String> jiDongChengCheXingCheHaoSheBeiCMD(String json, Class<JsonRootBean15> clazz,
                                                                 String cheXing, String JuTicheXing, String CheHao, String shebei) {
        JsonRootBean15
                jr = JSON.parseObject(json, clazz);
        List<String> chehaoShebeilistCmd = new ArrayList<>();

        if (cheXing.equals("机车") ){
            int getTrainTypeInfo = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (!CheHao.equals(" ") && carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        String cmdName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l).getName();
                                        if (!cmdName.equals(" ")) {
                                            chehaoShebeilistCmd.add(cmdName);
                                        }
                                    }
                                }
                            }
                        } else if (CheHao.equals(" ")) {
                            //车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();///////////get(0)//////////
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        String cmdName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(0).getDevices().getItem().get(k).getCMDitem().get(l).getName();

                                        if (!cmdName.equals(" ")) {
                                            chehaoShebeilistCmd.add(cmdName);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else if (cheXing.equals("动车") ){
            int getTrainTypeInfo = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (!CheHao.equals(" ") && carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        String cmdName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l).getName();
                                        if (!cmdName.equals(" ")) {
                                            chehaoShebeilistCmd.add(cmdName);
                                        }
                                    }
                                }
                            }
                        } else if (CheHao.equals(" ")) {
                            //车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();///////////get(0)//////////
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        String cmdName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(0).getDevices().getItem().get(k).getCMDitem().get(l).getName();

                                        if (!cmdName.equals(" ")) {
                                            chehaoShebeilistCmd.add(cmdName);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else if (cheXing.equals("城轨") ){
            int getTrainTypeInfo = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        String cmdName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l).getName();
                                        if (!cmdName.equals(" ")) {
                                            chehaoShebeilistCmd.add(cmdName);
                                        }
                                    }
                                }
                            }
                        } else if (CheHao.equals(" ")) {
                            //车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();///////////get(0)//////////
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        String cmdName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(0).getDevices().getItem().get(k).getCMDitem().get(l).getName();

                                        if (!cmdName.equals(" ")) {
                                            chehaoShebeilistCmd.add(cmdName);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("对应的车号中的设备列表CMD：" + chehaoShebeilistCmd.toString());
        System.out.println("对应的车号中的设备列表CMD SIZE：" + chehaoShebeilistCmd.size());

        return chehaoShebeilistCmd;

    }


    //            某个具体车型下的ABC或者车号de 设备的新，老CMD或者没有...如：HXD1C->A->  ERM->cmd...

    public static List<String> jiDongChengCheXingCheHaoSheBeiCMDipUserPsw(String json, Class<JsonRootBean15> clazz,
                                                                          String cheXing, String JuTicheXing, String CheHao, String shebei, String cmd) {
        JsonRootBean15
                jr = JSON.parseObject(json, clazz);
        List<String> chehaoShebeilistCmdipuserpsw = new ArrayList<>();


        if (cheXing.equals("机车") ){
            int getTrainTypeInfo = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();

                        if (!CheHao.equals(" ") && carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {
                                        CMDitem cmDitem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(0);
                                        CMDitem cmDitem2 = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l);
                                        if (cmd.equals(" ") || cmd.equals(null)) {//传入的cmd信息为空，表示cmditem为两个 且第二个cmditem为空值
                                            String ip = cmDitem.getIp();
                                            String user = cmDitem.getUser();
                                            String psw = cmDitem.getPsw();
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            chehaoShebeilistCmdipuserpsw.add(ip);
                                            chehaoShebeilistCmdipuserpsw.add(user);
                                            chehaoShebeilistCmdipuserpsw.add(psw);
                                            break;

                                        } else {
                                            //cmd信息不为空，判断是哪个具体cmd信息。
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            if (cmDitem2.getName().equals(cmd)) {//只拿到对应cmd的ip user信息
                                                String ip = cmDitem2.getIp();
                                                String user = cmDitem2.getUser();
                                                String psw = cmDitem2.getPsw();
                                                chehaoShebeilistCmdipuserpsw.add(ip);
                                                chehaoShebeilistCmdipuserpsw.add(user);
                                                chehaoShebeilistCmdipuserpsw.add(psw);

                                                break;//重要
                                            }
                                        }


                                    }
                                }
                            }
                        } else if (CheHao.equals(" ")) {
                            System.out.println("come解决");
                            //车号对比
                            int sizeItem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();///////////get(0)//////////
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    System.out.println("测试设备：" + sheBeiStr);
                                    System.out.println("测试sizeCmditem：" + sizeCmd);

                                    for (int l = 0; l < sizeCmd; l++) {


                                        CMDitem cmDitem = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(0);
                                        CMDitem cmDitem2 = jr.getDatalist().getLocomotive().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l);
                                        if (cmd.equals(" ") || cmd.equals(null)) {//传入的cmd信息为空，表示cmditem为两个 且第二个cmditem为空值
                                            String ip = cmDitem.getIp();
                                            String user = cmDitem.getUser();
                                            String psw = cmDitem.getPsw();
                                            System.out.println("测试sizeCmditem ip：" + ip);

                                            chehaoShebeilistCmdipuserpsw.clear();
                                            chehaoShebeilistCmdipuserpsw.add(ip);
                                            chehaoShebeilistCmdipuserpsw.add(user);
                                            chehaoShebeilistCmdipuserpsw.add(psw);
                                            break;

                                        } else {
                                            //cmd信息不为空，判断是哪个具体cmd信息。
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            if (cmDitem2.getName().equals(cmd)) {//只拿到对应cmd的ip user信息
                                                String ip = cmDitem2.getIp();
                                                String user = cmDitem2.getUser();
                                                String psw = cmDitem2.getPsw();
                                                System.out.println("测试sizeCmditem ip hava：" + ip);

                                                chehaoShebeilistCmdipuserpsw.add(ip);
                                                chehaoShebeilistCmdipuserpsw.add(user);
                                                chehaoShebeilistCmdipuserpsw.add(psw);
                                                System.out.println("测试sizeCmditem list：" + chehaoShebeilistCmdipuserpsw.toString());


                                                break;//重要


                                            }
                                        }


                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else if (cheXing.equals("动车") ){
            int getTrainTypeInfo = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (!CheHao.equals(" ") && carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {

                                        CMDitem cmDitem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(0);
                                        CMDitem cmDitem2 = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l);
                                        if (cmd.equals(" ") || cmd.equals(null)) {//传入的cmd信息为空，表示cmditem为两个 且第二个cmditem为空值
                                            String ip = cmDitem.getIp();
                                            String user = cmDitem.getUser();
                                            String psw = cmDitem.getPsw();
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            chehaoShebeilistCmdipuserpsw.add(ip);
                                            chehaoShebeilistCmdipuserpsw.add(user);
                                            chehaoShebeilistCmdipuserpsw.add(psw);
                                            break;

                                        } else {
                                            //cmd信息不为空，判断是哪个具体cmd信息。
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            if (cmDitem2.getName().equals(cmd)) {//只拿到对应cmd的ip user信息
                                                String ip = cmDitem2.getIp();
                                                String user = cmDitem2.getUser();
                                                String psw = cmDitem2.getPsw();
                                                chehaoShebeilistCmdipuserpsw.add(ip);
                                                chehaoShebeilistCmdipuserpsw.add(user);
                                                chehaoShebeilistCmdipuserpsw.add(psw);

                                                break;//重要
                                            }
                                        }

                                    }
                                }
                            }
                        } else if (CheHao.equals(" ")) {
                            //车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();///////////get(0)//////////
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {


                                        CMDitem cmDitem = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(0);
                                        CMDitem cmDitem2 = jr.getDatalist().getMotorTrain().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l);
                                        if (cmd.equals(" ") || cmd.equals(null)) {//传入的cmd信息为空，表示cmditem为两个 且第二个cmditem为空值
                                            String ip = cmDitem.getIp();
                                            String user = cmDitem.getUser();
                                            String psw = cmDitem.getPsw();
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            chehaoShebeilistCmdipuserpsw.add(ip);
                                            chehaoShebeilistCmdipuserpsw.add(user);
                                            chehaoShebeilistCmdipuserpsw.add(psw);
                                            break;

                                        } else {
                                            //cmd信息不为空，判断是哪个具体cmd信息。
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            if (cmDitem2.getName().equals(cmd)) {//只拿到对应cmd的ip user信息
                                                String ip = cmDitem2.getIp();
                                                String user = cmDitem2.getUser();
                                                String psw = cmDitem2.getPsw();
                                                chehaoShebeilistCmdipuserpsw.add(ip);
                                                chehaoShebeilistCmdipuserpsw.add(user);
                                                chehaoShebeilistCmdipuserpsw.add(psw);

                                                break;//重要
                                            }
                                        }

                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } else if (cheXing.equals("城轨") ){
            int getTrainTypeInfo = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().size();
            for (int i = 0; i < getTrainTypeInfo; i++) {
                String juticheName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getName();//na到具体车型列表 如CHR1..CHR2....
                if (juticheName.equals(JuTicheXing)) {//判断是否为选中的具体车型 如CHR1
                    int chehaosize = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).getMultiCar().getCar().size();//得到具体车型的所有车号
                    for (int j = 0; j < chehaosize; j++) {
                        String carName = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                getMultiCar().getCar().get(j).getName();
                        if (carName.equals(CheHao)) {//车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(j).getDevices().getItem().size();
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(j).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {


                                        CMDitem cmDitem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(0);
                                        CMDitem cmDitem2 = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l);
                                        if (cmd.equals(" ") || cmd.equals(null)) {//传入的cmd信息为空，表示cmditem为两个 且第二个cmditem为空值
                                            String ip = cmDitem.getIp();
                                            String user = cmDitem.getUser();
                                            String psw = cmDitem.getPsw();
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            chehaoShebeilistCmdipuserpsw.add(ip);
                                            chehaoShebeilistCmdipuserpsw.add(user);
                                            chehaoShebeilistCmdipuserpsw.add(psw);
                                            break;

                                        } else {
                                            //cmd信息不为空，判断是哪个具体cmd信息。
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            if (cmDitem2.getName().equals(cmd)) {//只拿到对应cmd的ip user信息
                                                String ip = cmDitem2.getIp();
                                                String user = cmDitem2.getUser();
                                                String psw = cmDitem2.getPsw();
                                                chehaoShebeilistCmdipuserpsw.add(ip);
                                                chehaoShebeilistCmdipuserpsw.add(user);
                                                chehaoShebeilistCmdipuserpsw.add(psw);

                                                break;//重要
                                            }
                                        }


                                    }
                                }
                            }
                        } else if (CheHao.equals(" ")) {
                            //车号对比 一样则访问其中的设备列表
                            int sizeItem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                    getMultiCar().getCar().get(0).getDevices().getItem().size();///////////get(0)//////////
                            for (int k = 0; k < sizeItem; k++) {
                                String sheBeiStr = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                        getMultiCar().getCar().get(0).getDevices().getItem().get(k).getName();
                                if (sheBeiStr.equals(shebei)) {
                                    int sizeCmd = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                            getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().size();
                                    for (int l = 0; l < sizeCmd; l++) {


                                        CMDitem cmDitem = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(0);
                                        CMDitem cmDitem2 = jr.getDatalist().getUrbanrail().getTrainType().getTrainTypeInfo().get(i).
                                                getMultiCar().getCar().get(j).getDevices().getItem().get(k).getCMDitem().get(l);
                                        if (cmd.equals(" ") || cmd.equals(null)) {//传入的cmd信息为空，表示cmditem为两个 且第二个cmditem为空值
                                            String ip = cmDitem.getIp();
                                            String user = cmDitem.getUser();
                                            String psw = cmDitem.getPsw();
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            chehaoShebeilistCmdipuserpsw.add(ip);
                                            chehaoShebeilistCmdipuserpsw.add(user);
                                            chehaoShebeilistCmdipuserpsw.add(psw);
                                            break;
                                        } else {
                                            //cmd信息不为空，判断是哪个具体cmd信息。
                                            chehaoShebeilistCmdipuserpsw.clear();
                                            if (cmDitem2.getName().equals(cmd)) {//只拿到对应cmd的ip user信息
                                                String ip = cmDitem2.getIp();
                                                String user = cmDitem2.getUser();
                                                String psw = cmDitem2.getPsw();
                                                chehaoShebeilistCmdipuserpsw.add(ip);
                                                chehaoShebeilistCmdipuserpsw.add(user);
                                                chehaoShebeilistCmdipuserpsw.add(psw);

                                                break;//重要
                                            }
                                        }

                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        System.out.println(
                "最终的数据：" + chehaoShebeilistCmdipuserpsw.toString()
        );

        return chehaoShebeilistCmdipuserpsw;
    }
}
