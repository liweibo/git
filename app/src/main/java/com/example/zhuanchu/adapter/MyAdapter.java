package com.example.zhuanchu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhuanchu.ItemChooseData;
import com.example.zhuanchu.MyApplication;
import com.example.zhuanchu.R;
import com.example.zhuanchu.RingProgressBar;
import com.example.zhuanchu.bean.DownloadindBean;
import com.example.zhuanchu.selectFileActivity;
import com.example.zhuanchu.service.FtpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MyAdapter extends BaseAdapter {
    MyApplication globalvaradapter = null;
    // 填充数据的list
    private List<FtpUtils.wxhFile> list;
    private List<FtpUtils.ItemFresh> listItemFresh = null;
    // 用来控制CheckBox的选中状况
    public static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    private static boolean[] checks; //用于保存checkBox的选择状态
    ViewHolder holder = new ViewHolder();
    ItemChooseData itemChooseData = null;
    public int jindu = 0;
    Map<String, DownloadindBean> downloadingMap = new ConcurrentHashMap<>();

    // 构造器
    public MyAdapter(List<FtpUtils.wxhFile> list, List<FtpUtils.ItemFresh> listItemFresh,
                     Context context,
                     int downloadJindu, MyApplication globalvaradapter,
                     Map<String, DownloadindBean> downloadingMap) {
        this.context = context;
        this.list = list;
        this.jindu = downloadJindu;
        this.globalvaradapter = globalvaradapter;
        this.listItemFresh = listItemFresh;
        this.downloadingMap = downloadingMap;

        inflater = LayoutInflater.from(context);
        checks = new boolean[list.size()];
        isSelected = new HashMap<Integer, Boolean>();
        itemChooseData = new ItemChooseData();
        // 初始化数据
        initDate();


    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static void clearBoolList() {
        for (int i = 0; i < checks.length; i++) {
            if (checks[i]) {
                checks[i] = !checks[i];
            }
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_list, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            holder.tvSize = (TextView) convertView.findViewById(R.id.item_size);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_time);
            holder.tvSucFail = (TextView) convertView.findViewById(R.id.item_tv_sucfail);
            holder.progress = (RingProgressBar) convertView.findViewById(R.id.ringProgressBarAdapter);
            holder.im = (ImageView) convertView.findViewById(R.id.file_icon);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.arro = (ImageView) convertView.findViewById(R.id.arrow);
            holder.ll_sizeandtime = convertView.findViewById(R.id.ll_sizeandtime);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }


        // 设置list中TextView的显示
        holder.tv.setText(list.get(position).filename);//文件名
        holder.tvSize.setText(list.get(position).filesize);//文件大小
        holder.tvTime.setText(list.get(position).filetime);//文件创建时间
        if (list.get(position).filetime.equals("") || list.get(position).filesize.equals("")) {
            holder.ll_sizeandtime.setVisibility(View.GONE);
        } else {
            holder.ll_sizeandtime.setVisibility(View.VISIBLE);
        }
        //记录当前页面 文件夹的个数
        int dirCount = 0;
        final int pos = position; //pos必须声明为final
        if (list.get(position).isFile) {
            holder.im.setImageResource(R.mipmap.document);
            holder.cb.setVisibility(View.VISIBLE);
            holder.arro.setVisibility(View.INVISIBLE);
        } else {
            dirCount++;
            holder.im.setImageResource(R.mipmap.folder);
            holder.cb.setVisibility(View.INVISIBLE);
            holder.arro.setVisibility(View.VISIBLE);
        }

        //设置“已下载”字样时，应该首先确认是哪个item即position
        if (!listItemFresh.get(position).getSucfail().equals("")) {
            holder.tvSucFail.setVisibility(View.VISIBLE);
            holder.tvSucFail.setText(listItemFresh.get(position).getSucfail());

        } else {
            holder.tvSucFail.setVisibility(View.INVISIBLE);
        }
        for (String key : downloadingMap.keySet()) {
            if (list.get(position).filename.equals(key)) {
//                System.out.println("打印key："+key+"对应list："+list.get(position).filename);

                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.setProgress(downloadingMap.get(key).current);
                if (downloadingMap.get(key).current >= 100) {
                    holder.tvSucFail.setText("已下载");//----下载过程中----设置
                    holder.tvSucFail.setVisibility(View.VISIBLE);
                } else if (downloadingMap.get(key).current >0 && downloadingMap.get(key).current < 100) {
                    holder.tvSucFail.setText("下载中..");//----下载过程中----设置
                    holder.tvSucFail.setVisibility(View.VISIBLE);
                }

            }
        }

        // 全选：根据isSelected来设置checkbox的选中状况
//        holder.cb.setChecked(getIsSelected().get(position));
//        System.out.println("adapter中的getIsSelected().get(position)：" + position);

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalvaradapter.setCheck(-1);
            }
        });

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                     checks[pos] = b;
                                                     if (!b) {
                                                         //没选中的item，则从记录中删除对应
                                                         if (selectFileActivity.currentFiles.get(position).isFile &&
                                                                 selectFileActivity.currentFiles.size() > 0) {
                                                             itemChooseData.removeFilePath(position);
                                                             itemChooseData.removeFileName(position);

                                                         }
                                                     } else {//选中的item，则记录他的index
                                                         //记录前，先判断选择的是否是文件，是则记录。非文件不记录
                                                         if (selectFileActivity.currentFiles.get(position).isFile) {
                                                             System.out.println("测试路径的个数：" + selectFileActivity.currentFiles.get(position).filePath);
                                                             itemChooseData.addFilePath(selectFileActivity.currentFiles.get(position).filePath);
                                                             itemChooseData.addFileName(selectFileActivity.currentFiles.get(position).filename);

                                                         }
                                                     }
                                                 }
                                             }
        );

        int checkInt = globalvaradapter.getCheck();
        if (checkInt == 0) {//未选中
            holder.cb.setChecked(false);
            for (int i = 0; i < checks.length; i++) {
                checks[i] = false;

                if (selectFileActivity.currentFiles.get(i).isFile &&
                        selectFileActivity.currentFiles.size() > 0) {
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            itemChooseData.removeFilePath(finalI);
                            itemChooseData.removeFileName(finalI);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }
            }
        }
        if (checkInt == 1) {//全选
            holder.cb.setChecked(true);
            for (int i = 0; i < checks.length; i++) {
                checks[i] = true;
//                System.out.println("测试待下载路径：" + selectFileActivity.currentFiles.get(i).filePath);
                if (selectFileActivity.currentFiles.get(i).isFile) {
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            itemChooseData.addFilePath(selectFileActivity.currentFiles.get(finalI).filePath);
                            itemChooseData.addFileName(selectFileActivity.currentFiles.get(finalI).filename);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }
            }
//            System.out.println("添加的下载文件名：" + ItemChooseData.getFilePath().toString());
//            System.out.println("添加的文件总数："+ ItemChooseData.getFilePath().size());
        }
        if (checkInt == -1) {//
            holder.cb.setChecked(checks[pos]);
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public LinearLayout ll_sizeandtime;
        public TextView tv;
        public TextView tvSucFail;
        public TextView tvSize;
        public TextView tvTime;
        public CheckBox cb;
        public ImageView im;
        public ImageView arro;
        public RingProgressBar progress;

    }


}