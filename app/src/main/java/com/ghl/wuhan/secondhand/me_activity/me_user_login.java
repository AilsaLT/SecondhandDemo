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

import com.ghl.wuhan.secondhand.DO.UserBO;
import com.ghl.wuhan.secondhand.DO.UserVO;
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

    //    private TextView response_Text;
    private EditText et_uname, et_password;//用户名,密码
    private TextView tv_register;
    private TextView tv_reset;
    //    private ImageView iv_back;
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

        //        pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref = getSharedPreferences("data",MODE_PRIVATE);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);

        //初始化
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_reset = (TextView) findViewById(R.id.tv_reset);


        tv_login = (TextView) findViewById(R.id.tv_login);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_password = (EditText) findViewById(R.id.et_password);




        //
        //        //记住密码
        boolean isRemember = pref.getBoolean("remember_password",false);
        if(isRemember) {
            //将账号和密码都设置到文本框中
            String uname = pref.getString("uname","");
            String upassword = pref.getString("upassword","");
            et_uname.setText(uname);
            et_password.setText(upassword);
            rememberPass.setChecked(true);
        }

        //注册
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(me_user_login.this, me_user_register.class);
                startActivity(intent);
            }
        });


        //登录
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "运行到tv_logon的点击事件中********");
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                String upassword = et_password.getText().toString().trim();

                Log.i(TAG, "uname is :" + uname);
                Log.i(TAG, "upassword is :" + upassword);

                if(uname.isEmpty()&&upassword.isEmpty()){
                    Toast.makeText(me_user_login.this,"请输入登录信息",Toast.LENGTH_SHORT).show();
                }else if(uname.isEmpty()){
                    Toast.makeText(me_user_login.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(upassword.isEmpty()){
                    Toast.makeText(me_user_login.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    //设置进度条
                    progressDialog = DialogUIUtils.showLoadingDialog(me_user_login.this,"正在登录......");
                    progressDialog.show();
                    //点击物理返回键是否可取消dialog
                    progressDialog.setCancelable(true);
                    //点击dialog之外 是否可取消
                    progressDialog.setCanceledOnTouchOutside(false);
                    login(opType, uname, upassword.toString());
                }




            }
        });


    }

    //将对象转换成json串
    private void login(int opType, String uname, String upassword) {

        UserBO userBO = new UserBO();
        String uuid = UUID.randomUUID().toString();
        userBO.setUid(uuid);
        userBO.setUname(uname);
        userBO.setUpassword(upassword);
        userBO.setOpType(opType);
        //        userBO.setUimage(uimages);

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

                    final String s = response.body().string();
                    Log.d(TAG, "response.body().string()==" + s);

                    //将获取的token解析成对象
                    UserBO userBO = new UserBO();
                    Gson gson = new Gson();
                    userBO = gson.fromJson(s, UserBO.class);
                    String token = userBO.getToken();
                    Log.i(TAG, "登陆中成功获取token==" + token);


                    //解析s获取flag
                    UserVO userVO = new UserVO();
                    Gson gson1 = new Gson();
                    userVO = gson1.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();
                    Log.i(TAG, "登录中成功获取flag==" + flag);
                    //flag=200登录成功，将token进行存储
                    String uname = et_uname.getText().toString();
                    String upassword = et_password.getText().toString();


                    if (flag == 200) {

                        //登录成功的标志
                        login = true;

                        //如果选中了记住密码，则把用户名密码保存
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        if (rememberPass.isChecked()) {
                            Log.i("TAG", "开始保存密码");
                            editor.putString("account", uname);
                            editor.putString("password", upassword);
                            editor.putBoolean("remember_password", true);
                        } else {
                            editor.clear();
                            editor.putBoolean("login",true);
                            editor.putString("uname",uname);
                            editor.putString("token", token);
                        }
                        editor.commit();

                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.putString("uname",uname);
                        editor.putBoolean("login",login);
                        editor.commit();
                        Log.i(TAG, "登录中成功存储token--->" + token);
                        Log.i(TAG,"user_login中的login--->"+login);
                        Log.i(TAG,"user_login中的uname--->"+uname);

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



                    //记住密码
                    if(rememberPass.isChecked()){//检查复选框是否被选中
                        editor.putBoolean("remember_password",true);
                        editor.putString("uname",uname);
                        editor.putString("upassword",upassword);
                    }else{
                        editor.clear();
                        editor.putBoolean("login",true);
                        editor.putString("uname",uname);
                        editor.putString("token", token);
                    }
                    editor.commit();


                    if (flag == 20001) {
                        //SharedPreferences.Editor.clear()方法是把之前commit后保存的所有信息全部进行清空。
                        login = false;
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putBoolean("login",login);
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
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putBoolean("login",login);
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
