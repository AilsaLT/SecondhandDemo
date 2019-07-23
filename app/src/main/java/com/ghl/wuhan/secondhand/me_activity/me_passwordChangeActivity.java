package com.ghl.wuhan.secondhand.me_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.DO.User;
import com.ghl.wuhan.secondhand.MainActivity;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class me_passwordChangeActivity extends AppCompatActivity implements View.OnClickListener{
    //属性定义
    private ImageView iv_back;
    private EditText et_password;
    private Button button;//保存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_password_change_activity);

        //初始化控件
        init();


        //设置事件的监听
        iv_back.setOnClickListener(this);
        button.setOnClickListener(this);




    }

    //初始化控件
    public void init(){
        iv_back = (ImageView) findViewById(R.id.iv_back);
        button = (Button) findViewById(R.id.button);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.button:
                getData();
                Intent intent = new Intent(me_passwordChangeActivity.this, MainActivity.class);
                startActivity(intent);
            default:
                break;
        }

    }

    private void getData() {

        //获取EdiText上的值
       String password = et_password.getText().toString().trim();
        //获取用户的userid
        SharedPreferences preferences = getSharedPreferences("userinfo",MODE_PRIVATE);
        String userid = preferences.getString("userid","");
        Log.i("TAG","me_passwordChangeActivity中userid--->"+userid);

        User user = new User();
        user.setUpassword(password);
        user.setUserid(userid);


        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String passwordJsonStr = gson.toJson(user, User.class);
        Log.i("TAG", "me_passwordChangeActivity中的passwordJsonStr is :" + passwordJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/userUpdatePwd";
        HttpUtils.sendOkHttpRequest(url, passwordJsonStr, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "获取数据失败了" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d("TAG", "获取数据成功了");
                    Log.d("TAG", "me_passwordChangeActivity中的response.code()==" + response.code());

                    final String jsonData = response.body().string();
                    Log.d("TAG", "me_passwordChangeActivity中的response.body().string()==" + jsonData);

                    Gson gson = new Gson();
                    Log.i("TAG", "开始解析jsonData");
                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
                    Log.i("TAG", "结束解析jsonData");
                    Log.i("TAG", "结束解析responseBuy:" + responseBuy);
                    final int flag = responseBuy.getFlag();
                    Log.i("TAG", "flag==" + flag);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 200) {
                                Log.i("TAG", "run: success");
                                Toast.makeText(me_passwordChangeActivity.this, "您的密码已修改成功！", Toast.LENGTH_SHORT).show();
                            }  else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(me_passwordChangeActivity.this, "密码修改失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}
