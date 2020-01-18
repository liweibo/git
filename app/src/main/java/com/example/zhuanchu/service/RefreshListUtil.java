package com.example.zhuanchu.service;


import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhuanchu.R;
import com.example.zhuanchu.RingProgressBar;
import com.example.zhuanchu.bean.DownloadindBean;

import java.util.List;
import java.util.Map;

public class RefreshListUtil {

    /**
     * 更新对应view的内容
     *
     * @param position 要更新的位置
     */
    public static void updateSingle(int position, ListView listView,
                                    List<FtpUtils.wxhFile> list, Map<String,
            DownloadindBean> downloadingMap) {
        /**第一个可见的位置**/
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = listView.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = listView.getChildAt(position - firstVisiblePosition);
            RingProgressBar ringProgressBar = view.findViewById(R.id.ringProgressBarAdapter);
            TextView tvSuc = view.findViewById(R.id.item_tv_sucfail);

            for (String key : downloadingMap.keySet()) {
                if (list.get(position).filename.equals(key)) {
//                System.out.println("打印key："+key+"对应list："+list.get(position).filename);

                    ringProgressBar.setVisibility(View.VISIBLE);
                    ringProgressBar.setProgress(downloadingMap.get(key).current);
                    if (downloadingMap.get(key).current >= 100) {
                        tvSuc.setText("已下载");//----下载过程中----设置
                        tvSuc.setVisibility(View.VISIBLE);
                    } else if (downloadingMap.get(key).current >= 0 && downloadingMap.get(key).current < 100) {
                        tvSuc.setText("下载中..");//----下载过程中----设置
                        tvSuc.setVisibility(View.VISIBLE);
                    }

                }
            }

        }
    }
}
