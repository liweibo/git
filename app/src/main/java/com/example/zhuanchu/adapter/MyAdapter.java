package com.example.zhuanchu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhuanchu.ItemChooseData;
import com.example.zhuanchu.R;
import com.example.zhuanchu.selectFileActivity;
import com.example.zhuanchu.service.FtpUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dwb on 2018/1/3.
 * describe1:
 * describe2:
 * email:wolfking0608@163.com
 */

public class MyAdapter extends BaseAdapter {
    // 填充数据的list
    private List<FtpUtils.wxhFile> list;
    // 用来控制CheckBox的选中状况
    public static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public MyAdapter(List<FtpUtils.wxhFile> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();


    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
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
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_list, null);
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
            holder.im = (ImageView) convertView.findViewById(R.id.file_icon);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.arro = (ImageView) convertView.findViewById(R.id.arrow);

            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置list中TextView的显示
        holder.tv.setText(list.get(position).filename);

        if (list.get(position).isFile) {
            holder.im.setImageResource(R.mipmap.document);
        } else {
            holder.im.setImageResource(R.mipmap.folder);
            holder.cb.setVisibility(View.INVISIBLE);
            holder.arro.setVisibility(View.VISIBLE);
        }


        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                     if (!b) {
                                                         System.out.println("监测。。。");
                                                         //没选中的item，则从记录中删除对应的index
                                                         if (selectFileActivity.currentFiles.get(position).isFile) {
                                                             ItemChooseData.removeIndex(position);
                                                             ItemChooseData.removeFilePath(position);
                                                             ItemChooseData.removeFileName(position);

                                                         }
                                                         System.out.println("q1:" + ItemChooseData.getFilePath().toString());
                                                         System.out.println("位置1：" + position);
                                                     } else {//选中的item，则记录他的index
                                                         //记录前，先判断选择的是否是文件，是则记录。非文件不记录
                                                         if (selectFileActivity.currentFiles.get(position).isFile) {
                                                             ItemChooseData.addIndex(position);
                                                             ItemChooseData.addFilePath(selectFileActivity.currentFiles.get(position).filePath);
                                                             ItemChooseData.addFileName(selectFileActivity.currentFiles.get(position).filename);

                                                         }


                                                         //当选中的是文件夹时：
//                                                         if (selectFileActivity.currentFiles.get(position).isParent){
//                                                           new   selectFileActivity().whileDir(position);
//                                                         }

                                                         System.out.println("位置2：" + position);
                                                         System.out.println("q2:" + ItemChooseData.getFilePath().toString());
                                                         System.out.println("q2222name:" + ItemChooseData.getFileName().toString());


                                                     }
                                                 }
                                             }
        );

        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tv;
        public CheckBox cb;
        public ImageView im;
        public ImageView arro;

    }
}