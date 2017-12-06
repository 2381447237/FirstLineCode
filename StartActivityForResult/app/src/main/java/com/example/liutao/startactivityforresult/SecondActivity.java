package com.example.liutao.startactivityforresult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by liutao on 2017/5/5.
 */

public class SecondActivity extends Activity{

    private Button btn;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        tv= (TextView) findViewById(R.id.tv2);
        btn= (Button) findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.putExtra("data_return","我是返回来的数据");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
