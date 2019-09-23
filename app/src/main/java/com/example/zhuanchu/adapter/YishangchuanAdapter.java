package com.example.zhuanchu.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhuanchu.FilelistActivity;
import com.example.zhuanchu.MyApplication;
import com.example.zhuanchu.R;
import com.example.zhuanchu.bean.PackFile;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YishangchuanAdapter extends RecyclerView.Adapter<YishangchuanAdapter.VerticalViewHolder> {

    Context context;
    //    List<String> lists = new ArrayList<>();
    JSONArray lists = new JSONArray();
    List<PackFile> mData;

    public YishangchuanAdapter(Context context) {
        this.context = context;
        System.out.println("进来了啊111");
    }

    @Override
    public VerticalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.yishangchuanitem, viewGroup, false);
        System.out.println("onCreateViewHolder1111111进来了：" + view);

        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, int i) {
        holder.textView.setText("testFilename");
        holder.fileTime.setText("testfileTime");
        holder.checkBox.setChecked(true);
    }



    @Override
    public int getItemCount() {
        return 1;
    }

    public static class VerticalViewHolder extends RecyclerView.ViewHolder {

        TextView textView, fileTime;
        CheckBox checkBox;

        public VerticalViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.fileNameuploadyi);
            fileTime = itemView.findViewById(R.id.filetimeuploadyi);
            checkBox = itemView.findViewById(R.id.filecheckuploadyi);
        }
    }


}
