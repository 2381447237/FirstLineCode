package com.example.liutao.mycustomview.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liutao.mycustomview.Activity2;
import com.example.liutao.mycustomview.Activity3;
import com.example.liutao.mycustomview.MainActivity;
import com.example.liutao.mycustomview.R;

/**
 * Created by liutao on 2017/5/5.
 */

public class TitleView extends LinearLayout{

    public TitleView(Context context) {
        super(context);
    }

    public TitleView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title,this);

        Button btn= (Button) findViewById(R.id.title_btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //((Activity)getContext()).finish();
                ((Activity)context).finish();
            }
        });

        TextView tv= (TextView) findViewById(R.id.title_tv);
        if(context instanceof MainActivity){
            tv.setText("我是activity1的标题");
        }else if(context instanceof Activity2){
            tv.setText("我是activity2的标题");
        }else if(context instanceof Activity3){
            tv.setText("我是activity3的标题");
        }
    }
}
