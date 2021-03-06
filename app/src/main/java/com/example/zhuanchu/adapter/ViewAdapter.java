package com.example.zhuanchu.adapter;

import android.content.Context;
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

import com.example.zhuanchu.R;
import com.example.zhuanchu.bean.PackFile;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewViewHolder> {

    Context context;
    JSONArray lists = new JSONArray();

    private  OnItemClickListener mOnItemClickListener;
    private  SItemClickListener sOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void selectOnItemClickListener(SItemClickListener listener){
        this.sOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void tClick(int i);
    }

    public interface SItemClickListener{
        void sClick(int i);
    }

    public ViewAdapter( @NonNull Context context, JSONArray lists ){
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public ViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fileviewlist, viewGroup, false);
        return new ViewAdapter.ViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewViewHolder viewViewHolder, final int i) {
        try {
            viewViewHolder.textView.setText( lists.getJSONObject(i).getString("name") );
            viewViewHolder.fileTime.setText( lists.getJSONObject(i).getString("time") );
            if( lists.getJSONObject(i).getString("type") == "1" ){
                viewViewHolder.imageView.setImageResource(R.drawable.wenjianjia);
            }else{
                viewViewHolder.imageView.setImageResource(R.drawable.wenjian);
            }
            if( lists.getJSONObject(i).getBoolean("check") ){
                viewViewHolder.checkBox.setVisibility(View.VISIBLE);
            }else{
                viewViewHolder.checkBox.setVisibility(View.GONE);
            }
            if( lists.getJSONObject(i).getString("select").equals("0") ){
                viewViewHolder.checkBox.setChecked( false );
            }else {
                viewViewHolder.checkBox.setChecked( true );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViewGroup.LayoutParams layoutParams = viewViewHolder.itemView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        viewViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println( mOnItemClickListener );

                if( mOnItemClickListener != null ){
                    mOnItemClickListener.tClick( i );
                }
            }
        });

        viewViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( sOnItemClickListener != null ){
                    sOnItemClickListener.sClick( i );
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.length();
    }

    public class ViewViewHolder extends RecyclerView.ViewHolder {

        TextView textView, fileTime;
        ImageView imageView;
        CheckBox checkBox;

        public ViewViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.fileName);
            fileTime = itemView.findViewById(R.id.filetime);
            imageView = itemView.findViewById(R.id.fileimg);
            checkBox = itemView.findViewById(R.id.removecheck);
        }
    }
}
