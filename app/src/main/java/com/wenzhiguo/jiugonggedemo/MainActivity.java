package com.wenzhiguo.jiugonggedemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Custom mCustom = (Custom) findViewById(R.id.custom);
        final TextView mTextView = (TextView) findViewById(R.id.textview);
        //记录密码
        final SharedPreferences wzg = getSharedPreferences("wzg", MODE_PRIVATE);
        String key = wzg.getString("key", null);
        if (key == null) {
            mTextView.setText("请设置初始密码");
        } else {
            mTextView.setText("请解锁密码");
        }
        mCustom.setOnClickCustomSuccess(new Custom.CustomOnClickListen() {
            @Override
            public void setOnCustomSuccess(String password) {
                String key = wzg.getString("key", null);
                //如果之前没有初始密码,那么将本次设置的密码存到配置文件中
                if (key == null) {
                    SharedPreferences.Editor edit = wzg.edit();
                    edit.putString("key", password);
                    edit.commit();
                    //如果之前有密码  但输入密码跟之前不一致   显示数据错误
                } else if (key != null && !key.equals(password)) {
                    Toast.makeText(MainActivity.this, "密码输入错误", Toast.LENGTH_SHORT).show();
                    //如果输入成功 则给个提示
                } else if (key != null && key.equals(password)) {
                    Toast.makeText(MainActivity.this, "--成功--", Toast.LENGTH_SHORT).show();
                    mTextView.setText("解锁成功");
                }
            }

            @Override
            public void setOnCustomError() {
                mTextView.setText("请解锁密码");
            }
        });
    }
}
