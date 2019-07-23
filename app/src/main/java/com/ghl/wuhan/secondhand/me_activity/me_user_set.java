package com.ghl.wuhan.secondhand.me_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.MainActivity;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.personal_information.personal_Activity;

public class me_user_set extends AppCompatActivity  {
    //属性定义
    private TextView login_out,tv_personal;
    private SharedPreferences pref;//取
    private SharedPreferences.Editor editor;//存
    private boolean login;//是否处于登录状态标志
    private TextView tv_password_change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_user_set);
        // 方法：初始化View
        initView();

        pref = getSharedPreferences("userinfo",MODE_PRIVATE);
        boolean login = pref.getBoolean("login",false);
        if(login == true){
            login_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //退出登录提醒
                    AlertDialog alert=new AlertDialog.Builder(me_user_set.this).create();
                    alert.setTitle("退出？");
                    alert.setMessage("真的要退出本软件吗？");
                    //添加取消按钮
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE,"不",new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    //添加"确定"按钮
                    alert.setButton(DialogInterface.BUTTON_POSITIVE,"是的", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //                        finish();

                            //确定退出后并将登录标志为false
                            editor = getSharedPreferences("userinfo", MODE_PRIVATE).edit();
                            editor.putBoolean("login",false);
                            editor.commit();
                            Intent intent = new Intent(me_user_set.this, MainActivity.class);
                            intent.putExtra("EXIT_TAG", "SINGLETASK");
                            startActivity(intent);


                        }
                    });
                    alert.show();

                }
            });
        }else if(login == false){
            login_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(me_user_set.this,"您未登录！",Toast.LENGTH_SHORT).show();
                }
            });

        }

        //点击个人信息，进行个人信息的修改
        tv_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(me_user_set.this,personal_Activity.class);
                startActivity(intent);
            }
        });

        tv_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(me_user_set.this,me_passwordChangeActivity.class);
                startActivity(intent);
            }
        });


    }

    // 方法：初始化View
    public void initView() {
        login_out = (TextView) findViewById(R.id.login_out);
        tv_personal = (TextView) findViewById(R.id.tv_personal);
        tv_password_change = (TextView) findViewById(R.id.tv_password_change);

    }







}