package com.example.liutao.testbroadcast;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    private EditText accountEdit,passwordEdit;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountEdit= (EditText) findViewById(R.id.account);
        passwordEdit= (EditText) findViewById(R.id.password);
        login= (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=accountEdit.getText().toString();
                String password=passwordEdit.getText().toString();

                //如果账号是admin同时密码是123456就登录成功

                if(TextUtils.equals(account,"admin")&&TextUtils.equals(password,"123456")){
                    Intent intent=new Intent(MainActivity.this,TwoActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this,"账号是admin,密码是123456",Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }
}
