package com.ghl.wuhan.secondhand.me_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.ghl.wuhan.secondhand.R;

public class me_aboutUsActivity extends AppCompatActivity {

    //属性定义
    private ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_about_us_activity);

        //初始化控件
        init();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    //初始化控件
    public void init(){

        iv_back = (ImageView) findViewById(R.id.iv_back);
    }
}
