package com.ghl.wuhan.secondhand.find_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.SaleBO;
import com.ghl.wuhan.secondhand.DO.UserVO;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.me_activity.me_user_login;
import com.ghl.wuhan.secondhand.util.DialogUIUtils;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

public class find_buy_activity extends AppCompatActivity {
    //属性定义
    private String TAG = "TAG";
    private ImageView iv_back;//返回
    private TextView tv_back;//返回
    private Button btn_submit;//点击提交


    //销售商品
    private int opType = 90004;//操作类型
    private String goodsID;//ID
    private int goodsType;//商品所属类
    private String goodsName;//商品名
    private float price = 0.1f;// 价格
    private String unit; //单位
    private float quality = 1.0f;//数量
    private String userid;//发布人ID
    private String token;
    private String uname;

    private EditText et_goodsName,et_unit, et_quality;
    private Spinner getGoodsType;//获取商品类型
    private Dialog progressDialog;//进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_buy_activity);

        //初始化部分
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_back = (TextView) findViewById(R.id.tv_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_goodsName = (EditText) findViewById(R.id.et_goodsName);
        et_unit = (EditText) findViewById(R.id.et_unit);
        et_quality = (EditText) findViewById(R.id.et_quality);
        getGoodsType = (Spinner) findViewById(R.id.goods_Type);


        //取消求购
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //spanner
        getGoodsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goodsType = position + 1;


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //点击发布商品
        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //设置进度条
                progressDialog = DialogUIUtils.showLoadingDialog(find_buy_activity.this,"正在发布......");
                progressDialog.show();
                //点击物理返回键是否可取消dialog
                progressDialog.setCancelable(true);
                //点击dialog之外 是否可取消
                progressDialog.setCanceledOnTouchOutside(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取用户名和密码参数
                        String goodsName = et_goodsName.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                        Log.i(TAG, "成功获取goodsName==" + goodsName);
                        //将存储在sp中的token拿到
                        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                        String token = pref.getString("token", "");
                        String uname = pref.getString("uname", "");
                        userid = uname;
                        Log.i(TAG, "成功获取token==" + token);
                        Log.i(TAG, "成功获取uname==" + uname);

                        //获取EditText上的商品单位和数量
                        String unit = et_unit.getText().toString().trim();
                        String quality = String.valueOf(et_quality.getText());

                        sale(opType, goodsID, goodsType, goodsName,unit, Float.parseFloat(quality), userid, uname, token);
                    }
                }).start();
            }
        });
    }


    //将传入的参数转换成Json串使用
    private void sale(int opType, String goodsID, int goodsType, String goodsName,
                      String unit, float quality, String userid,
                      String uname,String token) {

        //        //获取图片的byte[]
        //        Bitmap bitmap = BitmapFactory.decodeFile(filePath.toString());
        //        byte [] goodsImg  = Bitmap2Bytes(bitmap);
        //        Log.i(TAG,"me_user_register中的bytes--->"+goodsImg);


        SaleBO userBO = new SaleBO();
        String uuid = UUID.randomUUID().toString();
        userBO.setUid(uuid);
        userBO.setOpType(opType);
        userBO.setGoodsID(goodsID);
        userBO.setGoodsType(goodsType);
        userBO.setGoodsName(goodsName);
        userBO.setPrice(price);
        userBO.setUnit(unit);
        userBO.setQuality(quality);
        userBO.setUserid(userid);
        userBO.setUname(uname);
        userBO.setToken(token);


        Gson gson = new Gson();
        String userJsonStr = gson.toJson(userBO, SaleBO.class);
        Log.i(TAG, "jsonStr is :" + userJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/buy";
        HttpUtils.sendOkHttpRequest(url,userJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(find_buy_activity.this,"目前网络不佳！",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String s = response.body().string();
                    Log.d(TAG, "response.body().string()==" + s);

                    //将response.code()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();


                    if (flag == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(find_buy_activity.this, "求购商品信息已发布成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(find_buy_activity.this, me_user_login.class);
                        startActivity(intent);
                    }
                    if (flag == 40001) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(find_buy_activity.this, "照片过大，商品发布失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (flag == 4000) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(find_buy_activity.this, "SALE_GOODS_FAILED_PARAMS", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }




                }
            }
        });


    }
}
