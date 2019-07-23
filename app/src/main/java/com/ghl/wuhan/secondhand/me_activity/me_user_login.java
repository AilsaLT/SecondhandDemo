package com.ghl.wuhan.secondhand.me_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.ResponseBO;
import com.ghl.wuhan.secondhand.DO.UserBO;
import com.ghl.wuhan.secondhand.MainActivity;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.util.DialogUIUtils;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

import static com.ghl.wuhan.secondhand.util.DialogUIUtils.dismiss;

public class me_user_login extends AppCompatActivity {
    private String TAG = "TAG";
    private final int opType = 90002;
    private EditText et_uname, et_password;//用户名,密码
    private TextView tv_register;
    private TextView tv_reset;
    private TextView tv_login;


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private Dialog progressDialog;//进度条
    private boolean login;//登录成功的标志


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_user_login);


        //初始化
        init();

        //记住密码
        pref = getSharedPreferences("data", MODE_PRIVATE);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            //将账号和密码都设置到文本框中
            String uname = pref.getString("account", "");
            String upassword = pref.getString("password", "");
            et_uname.setText(uname);
            et_password.setText(upassword);
            rememberPass.setChecked(true);
        }


        //登录
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "运行到tv_logon的点击事件中********");
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                String upassword = et_password.getText().toString().trim();

                Log.i(TAG, "uname is :" + uname);
                Log.i(TAG, "upassword is :" + upassword);

                String uuid = UUID.randomUUID().toString();
                UserBO userBO = new UserBO();
                userBO.setUname(uname);
                userBO.setUpassword(upassword);
                userBO.setOpType(opType);
                userBO.setUid(uuid);

                if (uname.isEmpty() && upassword.isEmpty()) {
                    Toast.makeText(me_user_login.this, "请输入登录信息", Toast.LENGTH_SHORT).show();
                } else if (uname.isEmpty()) {
                    Toast.makeText(me_user_login.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (upassword.isEmpty()) {
                    Toast.makeText(me_user_login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //设置进度条
                    progressDialog = DialogUIUtils.showLoadingDialog(me_user_login.this, "正在登录......");
                    progressDialog.show();
                    //点击物理返回键是否可取消dialog
                    progressDialog.setCancelable(true);
                    //点击dialog之外 是否可取消
                    progressDialog.setCanceledOnTouchOutside(false);

                    //是否记住密码
                    editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    if (rememberPass.isChecked()) {
                        Log.i("TAG", "开始保存密码");
                        editor.putString("account", uname);
                        editor.putString("password", upassword);
                        editor.putBoolean("remember_password", true);
                    } else {
                        editor.clear();
                    }
                    editor.commit();

                    login(userBO);
                }
            }
        });


        //注册
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(me_user_login.this, me_user_register.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //初始化
    private void init() {
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_login = (TextView) findViewById(R.id.tv_login);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    //将对象转换成json串
    private void login(UserBO userBO) {

        Gson gson = new Gson();
        String userJsonStr = gson.toJson(userBO, UserBO.class);
        Log.i(TAG, "登录中loginJsonStr is :" + userJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/login";
        //        sendRequest(url, userJsonStr);
        HttpUtils.sendOkHttpRequest(url, userJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String loginJstr = response.body().string();
                    Log.d(TAG, "登录中返回的loginJstr" + loginJstr);


                    //解析s获取flag
                    ResponseBO responseBO = new ResponseBO();
                    Gson gson1 = new Gson();
                    responseBO = gson1.fromJson(loginJstr, ResponseBO.class);

                    //后台返回的图片
                    //                    byte[] image = responseBO.getImage();
                    //                    Log.i(TAG, "image: " + image);
                    //                    String uimage = new String(Base64.encode(image, Base64.DEFAULT));
                    //                    Log.i(TAG, "uimage: " + uimage);

                    String pictureUrl = responseBO.getPictureUrl();

                    int flag = responseBO.getFlag();
                    Log.i(TAG, "登录中成功获取flag==" + flag);

                    String token = responseBO.getToken();
                    Log.i(TAG, "登陆中成功获取token==" + token);

                    String userid = responseBO.getUserid();
                    Log.i(TAG, "me_user_login中的userid--->" + userid);

                    String uname = et_uname.getText().toString();
                    String upassword = et_password.getText().toString();


                    if (flag == 200) {

                        //登录成功的标志
                        login = true;
                        editor = getSharedPreferences("userinfo", MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.putString("uname", uname);
                        editor.putBoolean("login", login);
                        editor.putString("pictureUrl",pictureUrl);
                        editor.putString("userid", userid);
                        editor.commit();

                        Log.i(TAG, "登录中成功存储token--->" + token);
                        Log.i(TAG, "user_login中的login--->" + login);
                        Log.i(TAG, "user_login中的uname--->" + uname);
                        Log.i(TAG, "user_login中的userid--->" + userid);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_login.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                dismiss(progressDialog);
                            }
                        });

                        Intent intent = new Intent(me_user_login.this, MainActivity.class);
                        intent.putExtra("extra", "登录成功");
                        startActivity(intent);
                        finish();
                    }

                    if (flag == 20001) {
                        //SharedPreferences.Editor.clear()方法是把之前commit后保存的所有信息全部进行清空。
                        login = false;
                        editor = getSharedPreferences("userinfo", MODE_PRIVATE).edit();
                        editor.putBoolean("login", login);
                        editor.commit();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_login.this, "用户名不能空，登录失败！", Toast.LENGTH_SHORT).show();
                                dismiss(progressDialog);
                            }
                        });

                    }
                    if (flag == 20002) {
                        //SharedPreferences.Editor.clear()方法是把之前commit后保存的所有信息全部进行清空。
                        login = false;
                        editor = getSharedPreferences("userinfo", MODE_PRIVATE).edit();
                        editor.putBoolean("login", login);
                        editor.commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_login.this, "密码不能为空，登录失败！", Toast.LENGTH_SHORT).show();
                                dismiss(progressDialog);
                            }
                        });

                    }
                }
            }
        });

    }


}
