package com.example.zhuanchu;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


@SuppressLint("ValidFragment")
class SimpleCardFragmentYishangchuan extends Fragment {
    private String mTitle;

    public static SimpleCardFragmentYishangchuan getInstance(String title) {
        SimpleCardFragmentYishangchuan sf = new SimpleCardFragmentYishangchuan();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_simple_card_fragment_yishagchuan, null);
//        TextView card_title_tv = (TextView) v.findViewById(R.id.card_title_tv);
//        card_title_tv.setText(mTitle);

        return v;
    }
}