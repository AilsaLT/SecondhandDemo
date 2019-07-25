package com.ghl.wuhan.secondhand.home_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.adapter.GoodsItemAdapter;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.ghl.wuhan.secondhand.util.NetworkStateUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class home_search extends AppCompatActivity {
    //属性定义
    private String TAG = "TAG";
    private EditText et_content;
    private Button bt_cancel, bt_query, bt_clear;
    private int opType = 90004;//操作类型
    private String goodsName;
    //查询列表中的属性
    RecyclerView recyclerView;
    List<Goods> resultGoodsList = new ArrayList<Goods>();

    private ImageView iv_networkbad;//无网络
    private boolean networkState;//网络状态

    private ImageView iv_no_goods;//无商品时



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_search_activity);

        //初始化
        recyclerView = (RecyclerView) findViewById(R.id.search_tag_recycler);
        bt_query = (Button) findViewById(R.id.bt_query);
        et_content = (EditText) findViewById(R.id.et_content);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        iv_networkbad = (ImageView) findViewById(R.id.iv_networkbad);//无网络
        //默认状态是不可见
        iv_networkbad.setVisibility(View.GONE);

        iv_no_goods = (ImageView) findViewById(R.id.iv_no_goods);
        //默认状态为不可见
        iv_no_goods.setVisibility(View.GONE);


        //点击搜索
        bt_query.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getData(opType);
                    }
                }).start();

            }
        });

        //将EditText中的内容清空
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_content.setText("");
            }
        });



        //点击取消则finish
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getData(int opType) {

        //获取搜索框中的内容
        String goodsName = et_content.getText().toString().trim();
        Log.i(TAG, "home_search中获取到的goodsName--->" + goodsName);

        Goods goods = new Goods();
        goods.setOpType(opType);

        goods.setGoodsName(goodsName);
         //将获取的对象转换成Json串
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(goods, Goods.class);
        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/search";
        HttpUtils.sendOkHttpRequest(url, buyJsonStr, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                networkState = NetworkStateUtils.isNetworkConnected(home_search.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(networkState == true ){
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(View.VISIBLE);
                            iv_no_goods.setVisibility(View.GONE);
                            Toast.makeText(home_search.this,"你的服务器在开小差哦",Toast.LENGTH_SHORT).show();

                        }else{
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(View.VISIBLE);
                            iv_no_goods.setVisibility(View.GONE);
                            Toast.makeText(home_search.this,"你的网络在开小差哦！",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String jsonData = response.body().string();
                    Log.d(TAG, "查询商品中的response.body().string()==" + jsonData);

                    Gson gson = new Gson();
                    Log.i(TAG, "开始解析jsonData");
                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
                    Log.i(TAG, "结束解析jsonData");
                    Log.i(TAG, "结束解析responseBuy:" + responseBuy);
                    //Log.i(TAG,"查询商品的列表："+ responseBuy.getGoodList().get(0));
                    final int flag = responseBuy.getFlag();
                    Log.i(TAG, "flag==" + flag);
                    resultGoodsList = responseBuy.getGoodsList();
                    Log.i("resultGoodsList", "resultGoodsList==" + resultGoodsList.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            iv_networkbad.setVisibility(View.GONE);
                            iv_no_goods.setVisibility(View.GONE);



                            if (flag == 200) {
                                Log.i(TAG, "run: success");
                                //为RecyclerView的item指定其布局为线性布局
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(home_search.this, LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(linearLayoutManager);
                                //绑定适配器
                                GoodsItemAdapter adapter = new GoodsItemAdapter(home_search.this,resultGoodsList);
                                recyclerView.setAdapter(adapter);
                                if(resultGoodsList.size() == 0){
                                    iv_no_goods.setVisibility(View.VISIBLE);
                                }
//                                Toast.makeText(home_search.this, "查询成功！", Toast.LENGTH_SHORT).show();

                            } else if (flag == 30001) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(home_search.this, "登录信息已失效,请再次登录", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(home_search.this, "查询失败！", Toast.LENGTH_SHORT).show();
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
