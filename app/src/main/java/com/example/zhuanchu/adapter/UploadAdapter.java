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

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.VerticalViewHolder> {

    Context context;
    //    List<String> lists = new ArrayList<>();
    JSONArray lists = new JSONArray();
    List<PackFile> mData;
    List<Integer> listYi = new ArrayList<>();

    public UploadAdapter(Context context, JSONArray lists, List<Integer> yishangchuan) {
        this.context = context;
        this.lists = lists;
        listYi.clear();
        listYi.addAll(yishangchuan);
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.uploadfile, viewGroup, false);
        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VerticalViewHolder holder, final int i) {
        System.out.println(i + "----" + 6555);
        try {
            holder.textView.setText(lists.getJSONObject(i).getString("name"));
            holder.fileTime.setText(lists.getJSONObject(i).getString("time"));
            holder.checkBox.setChecked(lists.getJSONObject(i).getBoolean("check"));

            for (int j = 0; j < listYi.size(); j++) {
                //哪个item的已上传进行标记？
                if (listYi.get(j)==i){
                    holder.shangchuanstate.setVisibility(View.VISIBLE);
                }else
                {
                    holder.shangchuanstate.setVisibility(View.INVISIBLE);

                }
            }
            if (listYi.size()==0){
                holder.shangchuanstate.setVisibility(View.INVISIBLE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) return;
                try {

                    System.out.println(i + "---" + isChecked);
                    lists.getJSONObject(i).put("check", isChecked);
                    //lists.getJSONObject(i).put("check", true);

                } catch (Exception e) {

                }
                //刷新适配器
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.length();
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {

        TextView textView, fileTime, shangchuanstate;
        CheckBox checkBox;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.fileNameupload);
            fileTime = itemView.findViewById(R.id.filetimeupload);
            shangchuanstate = itemView.findViewById(R.id.shangchuanstate);

            checkBox = itemView.findViewById(R.id.filecheckupload);
        }
    }

    /**
     * 时间戳转换成字符窜
     *
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
