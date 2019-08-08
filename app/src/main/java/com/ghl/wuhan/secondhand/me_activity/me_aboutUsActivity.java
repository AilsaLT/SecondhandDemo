package com.ghl.wuhan.secondhand.me_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.R;

public class me_aboutUsActivity extends AppCompatActivity {

    //属性定义
    private ImageView iv_back;
    private TextView tv_us,tv_mainbody1,tv_mainbody2,tv_mainbody3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_about_us_activity);

        //初始化控件
        init();

        String st_us = "简介：这里主要介绍一下三个问题：第一，为什么要做这个项目--二手市场，而不做别的市场？第二，在做这个项目时个人觉得遇到的最难的问题是什么？第三，我在做这个项目时的一些感受和想法。";
        String st_mainbody1 = "     第一，做二手市场的灵感：这灵感主要还是来自学校，每次快到毕业季，就会有跳蚤市场，各学长学姐就会把自己的东西拿出来卖，那时候就在想自己能不能做一个撮合交易的APP，所以就做了一个二手市场，正好检验一下自己所学到的知识。";
        String st_mainbody2 = "     第二，在做项目时遇到的比较难的问题：①连接前后台的纽带；②图片的存储：刚刚开始是使用的本地存储，将拍照得到的图片转换成byte数组，然后存储到数据库，但是这样有个问题就是，当你的图片很多，从数据库拿图片的速度就很慢；" +
                "后面发现腾讯云的对象存储不错，存储图片只需要获取一个图片的地址，将图片地址存储起来就行，速度明显快了很多。";

        String st_mainbody3 = "     第三，做项目时的一些想法和感受：************************************************************************************************************************************************";
        SpannableString spannableString  = new SpannableString(st_us);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#04c1f5"));//设置字体颜色
        spannableString.setSpan(colorSpan,0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_us.setText(spannableString);
        tv_us.append("\n");//换行

        SpannableString spannableString_mainbody1 = new SpannableString(st_mainbody1);
        ForegroundColorSpan colorSpan_mainbody1 = new ForegroundColorSpan(Color.parseColor("#f4c806"));//设置字体颜色
        spannableString_mainbody1.setSpan(colorSpan_mainbody1,0,8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_mainbody1.setText(spannableString_mainbody1);
        tv_mainbody1.append("\n");

        SpannableString spannableString_mainbody2 = new SpannableString(st_mainbody2);
        ForegroundColorSpan colorSpan_mainbody2 = new ForegroundColorSpan(Color.parseColor("#f4c806"));//设置字体颜色
        spannableString_mainbody2.setSpan(colorSpan_mainbody2,0,8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_mainbody2.setText(spannableString_mainbody2);
        tv_mainbody2.append("\n");

        SpannableString spannableString_mainbody3 = new SpannableString(st_mainbody3);
        ForegroundColorSpan colorSpan_mainbody3 = new ForegroundColorSpan(Color.parseColor("#f4c806"));//设置字体颜色
        spannableString_mainbody3.setSpan(colorSpan_mainbody3,0,8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_mainbody3.setText(spannableString_mainbody3);
        tv_mainbody3.append("\n");




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
        tv_us = (TextView) findViewById(R.id.tv_us);
        tv_mainbody1 =(TextView) findViewById(R.id.tv_mainbody1);
        tv_mainbody2 =(TextView) findViewById(R.id.tv_mainbody2);
        tv_mainbody3 = (TextView) findViewById(R.id.tv_mainbody3);
    }
}
