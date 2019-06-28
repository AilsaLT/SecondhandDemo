package com.ghl.wuhan.secondhand.find_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class find_buy_activity extends AppCompatActivity {
    //属性定义
    private String TAG = "TAG";
    private ImageView iv_back;//返回
    private Button btn_submit;//点击提交
    private String token;
    private int opType = 90004;//操作类型

   // private Dialog progressDialog;//进度条


    //查询列表中的属性
    //    ListView lv_showGoods;
    RecyclerView recyclerView;
    List<Goods> resultGoodsList = new ArrayList<Goods>();

    private SpringView springView;//下拉刷新，上拉加载的控件
    public int page = 1;
    protected int checkType = 1;
    public int pageSize = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_buy_activity);

        //初始化部分
        //        lv_showGoods = (ListView) findViewById(R.id.lv_showGoods);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        springView = (SpringView) findViewById( R.id.springView );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( find_buy_activity.this,LinearLayoutManager.VERTICAL,false );
        recyclerView.setLayoutManager( linearLayoutManager );

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

        //下拉刷新
        //        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        //取消求购
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



//        //点击查询
//        btn_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //设置进度条
//                progressDialog = DialogUIUtils.showLoadingDialog(find_buy_activity.this,"正在查询中，请耐心等待......");
//                progressDialog.show();
//                //点击物理返回键是否可取消dialog
//                progressDialog.setCancelable(true);
//                //点击dialog之外 是否可取消
//                progressDialog.setCanceledOnTouchOutside(false);
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
//                        String token = preferences.getString("token", "");
//                        Log.i(TAG, "从sp获取到的token==" + token);
//                        purchase(token, opType,page,checkType,pageSize);
//                    }
//                }).start();
//            }
//        });


    }

    //将对象转换成json串
//    private void purchase(String token, int opType,int page,int checkType,int pageSize) {
//        Goods goods = new Goods();
//        goods.setToken(token);
//        goods.setOpType(opType);
//
//        goods.setCheckType(checkType);
//        goods.setPage(page);
//        goods.setPageSize(pageSize);
//
//        //将获取的对象转换成Json串
//        Gson gson = new Gson();
//        String buyJsonStr = gson.toJson(goods, Goods.class);
//        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
//        String url = "http://47.105.183.54:8080/Proj20/buy";
//        //        sendRequest(url, buyJsonStr);
//        HttpUtils.sendOkHttpRequest(url,buyJsonStr, new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d(TAG, "获取数据失败了" + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {//回调的方法执行在子线程。
//                    Log.d(TAG, "获取数据成功了");
//                    Log.d(TAG, "response.code()==" + response.code());
//
//                    final String jsonData = response.body().string();
//                    Log.d(TAG, "查询商品中的response.body().string()==" + jsonData);
//
//                    Gson gson = new Gson();
//                    Log.i(TAG, "开始解析jsonData");
//                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
//                    Log.i(TAG, "结束解析jsonData");
//                    Log.i(TAG, "结束解析responseBuy:" + responseBuy);
//                    //Log.i(TAG,"查询商品的列表："+ responseBuy.getGoodList().get(0));
//                    final int flag = responseBuy.getFlag();
//                    Log.i(TAG, "flag==" + flag);
//                    resultGoodsList = responseBuy.getGoodsList();
//                    Log.i(TAG, "resultGoodsList==" + resultGoodsList);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (flag == 200) {
//
//                                // ArrayAdapter<Goods> adapter = new GoodsAdapter(buy.this, R.layout.goods_item, resultGoodsList);
//                                // lv_showGoods.setAdapter(adapter);
//                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(find_buy_activity.this);
//                                recyclerView.setLayoutManager(linearLayoutManager);
//                                Goods_Adapter adapter = new Goods_Adapter(resultGoodsList);
//                                recyclerView.setAdapter(adapter);
//
//                                dismiss(progressDialog);
//
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//
//    }


//获取数据

        private void getData(String token, int opType) {
        Goods goods = new Goods();
        goods.setToken(token);
        goods.setOpType(opType);

        goods.setCheckType(checkType);
        goods.setPage(page);
        goods.setPageSize(pageSize);

        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(goods, Goods.class);
        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/buy";
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
                                Toast.makeText( find_buy_activity.this,"查询成功！",Toast.LENGTH_SHORT ).show();

                            } else if(flag == 30001){
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText( find_buy_activity.this,"登录信息已失效,请再次登录",Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            }
                            else{
                                runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText( find_buy_activity.this,"查询失败！",Toast.LENGTH_SHORT ).show();
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
