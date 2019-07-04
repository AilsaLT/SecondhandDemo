package com.ghl.wuhan.secondhand.sort_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.adapter.Goods_Adapter;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.google.gson.Gson;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class sort_classify_activity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView text_commdity_sort;
    private String Commoditytype;


    private String TAG = "TAG";
    private String token;
    private int opType = 90004;//操作类型
    //查询列表中的属性
    RecyclerView recyclerView;
    List<Goods> resultGoodsList = new ArrayList<Goods>();

    private SpringView springView;//下拉刷新，上拉加载的控件
    public int page = 1;//页数
    protected int checkType = 1;//查询方式 1---上拉加载更多  2---下拉刷新
    public int pageSize = 5;//数据条数

    private int goodsType;//商品所属类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_classify_activity);

        //初始化
        text_commdity_sort = (TextView) findViewById(R.id.tv_1);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        springView = (SpringView) findViewById( R.id.springView );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( sort_classify_activity.this,LinearLayoutManager.VERTICAL,false );
        recyclerView.setLayoutManager( linearLayoutManager );

        Intent intent = getIntent();
        goodsType = Integer.parseInt(intent.getStringExtra("classifyType"));
        Log.i(TAG,"goodsType--->"+goodsType);
        Commoditytype = intent.getStringExtra("Commoditype");
        Log.i(TAG,"Commoditytype--->"+Commoditytype);

        if (null != Commoditytype && !Commoditytype.equals("")) {
            text_commdity_sort.setText(Commoditytype);
        }

        //获取用户的token
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        final String token = preferences.getString("token", "");
        Log.i(TAG, "从sp获取到的token==" + token);

        //getData();
        getData(token,opType);

        springView.setHeader( new DefaultHeader( this ) );
        springView.setFooter( new DefaultFooter( this ) );
        springView.setListener(new SpringView.OnFreshListener() {
            //刷新
            @Override
            public void onRefresh() {
                page = 1 ;
                checkType = 2;
                Log.i( TAG, "onRefresh: page is " + page );
                //getData();
                getData(token,opType);
                springView.onFinishFreshAndLoad();
            }
            //加载更多
            @Override
            public void onLoadmore() {
                page ++;
                checkType = 1;
                Log.i( TAG, "onRefresh: page is " + page );
                /*********/
                //getData();
                getData(token,opType);
                /*********/
                springView.onFinishFreshAndLoad();
            }
        });


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }

    private void getData(String token, int opType) {
        Goods goods = new Goods();
        goods.setToken(token);
        goods.setOpType(opType);

        goods.setCheckType(checkType);
        goods.setPage(page);
        goods.setPageSize(pageSize);
        goods.setGoodsType(String.valueOf(goodsType));

        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(goods, Goods.class);
        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/classify";
        HttpUtils.sendOkHttpRequest(url,buyJsonStr, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
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
                    Log.i(TAG, "resultGoodsList==" + resultGoodsList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 200) {
                                Log.i( TAG, "run: success" );
                                Goods_Adapter adapter = new Goods_Adapter(resultGoodsList);
                                recyclerView.setAdapter(adapter);
                                Toast.makeText( sort_classify_activity.this,"查询成功！",Toast.LENGTH_SHORT ).show();

                            } else if(flag == 30001){
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText( sort_classify_activity.this,"登录信息已失效,请再次登录",Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            }
                            else{
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText( sort_classify_activity.this,"查询失败！",Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            }
                        }
                    });
                }
            }
        });


    }

}

