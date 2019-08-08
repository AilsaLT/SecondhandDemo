package com.ghl.wuhan.secondhand.me_activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.adapter.GoodsItemAdapter;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.ghl.wuhan.secondhand.util.NetworkStateUtils;
import com.google.gson.Gson;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class me_buyActivity extends AppCompatActivity {
    //属性定义
    private ImageView iv_back;
    private String TAG ="TAG";
    private SharedPreferences preferences;

    private int opType = 90004;//操作类型
    //查询列表中的属性
    RecyclerView recyclerView;
    List<Goods> newResultGoodsList = new ArrayList<Goods>();
    List<Goods> allGoodsList = new ArrayList<Goods>();
    private SpringView springView;//下拉刷新，上拉加载的控件
    private int pageRefresh;//刷新页数
    private int pageMore = 1;//加载更多页数
    protected int checkType = 1;//查询方式 1---上拉加载更多  2---下拉刷新
    public int pageSize = 5;//数据条数

    private ImageView iv_networkbad;//无网络
    private boolean networkState;//网络状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_buy_activity);
        //初始化控件
        init();

        //获取用户的token
        SharedPreferences preferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        final String token = preferences.getString("token", "");
        Log.i(TAG, "从sp获取到的token==" + token);

        //获取数据
        getData(token, opType);

        springView.setHeader(new DefaultHeader(this));
        springView.setFooter(new DefaultFooter(this));

        springView.setListener(new SpringView.OnFreshListener() {
            //刷新
            @Override
            public void onRefresh() {
                pageRefresh = 1;
                checkType = 2;
                Log.i(TAG, "onRefresh: page is " + pageRefresh);
                //getData();
                getData(token, opType);
                springView.onFinishFreshAndLoad();
            }

            //加载更多
            @Override
            public void onLoadmore() {
                pageMore++;
                checkType = 1;
                Log.i(TAG, "onRefresh: page is " + pageMore);
                /*********/
                //getData();
                getData(token, opType);
                /*********/
                springView.onFinishFreshAndLoad();
            }
        });

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
        //初始化部分
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        springView = (SpringView) findViewById(R.id.springView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(me_buyActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        iv_networkbad = (ImageView) findViewById(R.id.iv_networkbad);//无网络
        //默认状态是不可见
        iv_networkbad.setVisibility(View.GONE);
    }


    private void getData(String token, int opType) {

        //获取userid
        preferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        String userid = preferences.getString("userid","");

        Goods goods = new Goods();
        goods.setToken(token);
        goods.setOpType(opType);
        goods.setUserid(userid);

        goods.setCheckType(checkType);
        if (checkType == 1){//加载更多
            goods.setPage(pageMore);
        }else {
            goods.setPage(pageRefresh);
        }
        goods.setPageSize(pageSize);

        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(goods, Goods.class);
        Log.i(TAG, "查询商品中buyJsonStr is :" + buyJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/buyshow";
        HttpUtils.sendOkHttpRequest(url, buyJsonStr, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                networkState = NetworkStateUtils.isNetworkConnected(me_buyActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(networkState == true ){
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(View.VISIBLE);
                            Toast.makeText(me_buyActivity.this,"你的服务器在开小差哦",Toast.LENGTH_SHORT).show();

                        }else{
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(View.VISIBLE);
                            Toast.makeText(me_buyActivity.this,"你的网络在开小差哦！",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "me_buyActivity中response.code()==" + response.code());

                    final String jsonData = response.body().string();
                    Log.d(TAG, "me_buyActivity中查询商品中的response.body().string()==" + jsonData);
                    Gson gson = new Gson();
                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
                    final int flag = responseBuy.getFlag();
                    Log.i(TAG, "flag==" + flag);
                    newResultGoodsList = responseBuy.getGoodsList();
                    //Log.i(TAG, "resultGoodsList==" + resultGoodsList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            iv_networkbad.setVisibility(View.GONE);
                            if (flag == 200) {
                                Log.i(TAG, "run: success");
                                //如果一直上拉刷新，当newResultGoodsList返回为null，说明数据库里已经没有更多数据了
                                if (newResultGoodsList.size() <=0) {
                                    Toast.makeText(me_buyActivity.this, "没有更多内容了哦！", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //上拉加载
                                if (checkType == 1) {
                                    //将newResultGoodsList加到allGoodsList的下面显示，使其按顺序排序
                                    for (int i = 0; i < newResultGoodsList.size(); i++) {
                                        boolean repeat = false;//判断加入新的List中的getGoodsID是否与旧的List中的getGoodsID是否一样一样则不重复加载
                                        for (int j = 0; j < allGoodsList.size(); j++) {
                                            if (allGoodsList.get(j).getGoodsID().equals(newResultGoodsList.get(i).getGoodsID())) {
                                                repeat = true;
                                                break;
                                            }
                                        }
                                        if (repeat == false) {
                                            allGoodsList.add(newResultGoodsList.get(i));
                                            Log.i(TAG, "me_buyActivity中allGoodsList.size() " + allGoodsList.size());
                                        }
                                    }
                                    GoodsItemAdapter adapter = new GoodsItemAdapter(me_buyActivity.this, allGoodsList);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);//自动滑动到底部
                                }
                                //下拉刷新
                                if (checkType == 2) {
                                    //将allGoodsList的内容加到newResultGoodsList下面显示，使得最新的展示在第一页
                                    for (int i = 0; i < allGoodsList.size(); i++) {
                                        boolean repeat = false;//判断加入新的List中的getGoodsID是否与旧的List中的getGoodsID是否一样一样则不重复加载
                                        for (int j = 0; j < newResultGoodsList.size(); j++) {
                                            if (newResultGoodsList.get(j).getGoodsID().equals(allGoodsList.get(i).getGoodsID())) {
                                                repeat = true;
                                                break;
                                            }
                                        }
                                        if (repeat == false) {
                                            newResultGoodsList.add(allGoodsList.get(i));
                                            Log.i(TAG, "me_buyActivity中allGoodsList.size() " + allGoodsList.size());
                                        }
                                    }
                                    allGoodsList = newResultGoodsList;
                                    GoodsItemAdapter adapter = new GoodsItemAdapter(me_buyActivity.this, allGoodsList);
                                    recyclerView.setAdapter(adapter);
                                    //recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }
                            } else if (flag == 30001) {
                                Toast.makeText(me_buyActivity.this, "登录信息已失效,请再次登录", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(me_buyActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}
