package com.example.zhuanchu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhuanchu.R;
import com.example.zhuanchu.WifiActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder>{

    Context context;
    JSONArray lists = new JSONArray();

    public WifiAdapter(@NonNull Context context, JSONArray lists) {
        this.context = context;
        this.lists = lists;
    }

    private  OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void tClick(int i);
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.wifilist, viewGroup, false);
        //view.setOnClickListener((View.OnClickListener) this);
        return new WifiAdapter.WifiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder wifiViewHolder, final int i) {
        ViewGroup.LayoutParams layoutParams = wifiViewHolder.itemView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        try {
            wifiViewHolder.textView.setText( lists.getJSONObject(i).getString("ssid") );
            if( lists.getJSONObject(i).getBoolean("check") == false ){
                wifiViewHolder.imageView.setVisibility(View.INVISIBLE);
            }else{
                wifiViewHolder.imageView.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        wifiViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mOnItemClickListener != null ){
                    mOnItemClickListener.tClick( i );
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.length();
    }

//    @Override
//    public void onClick(View view) {        //根据RecyclerView获得当前View的位置
//        //int position = recyclerView.getChildAdapterPosition(view);        //程序执行到此，会去执行具体实现的onItemClick()方法
//        if (mOnItemClickListener!=null){
//            mOnItemClickListener.onItemClick(view, 5);
//        }
//    }

    public class WifiViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;


        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.wifiname);
            imageView = itemView.findViewById(R.id.wifigou);
        }
    }
}
