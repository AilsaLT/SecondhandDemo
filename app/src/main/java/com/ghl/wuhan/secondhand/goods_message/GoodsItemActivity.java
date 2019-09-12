package com.ghl.wuhan.secondhand.goods_message;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.DO.CollectBO;
import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.DO.UserVO;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class GoodsItemActivity extends AppCompatActivity {
    //属性定义
    private String TAG = "TAG";
    private ImageView iv_back;//返回
    private ImageView iv_image;
    private TextView tv_goodsId, tv_goodsName, tv_goodsPrice, tv_goodsUnit, tv_goodsQuantity,tv_goodsQq;
//    private ImageView iv_collect;
    private FloatingActionButton fab;
    private int collectFlag = 2;//收藏标志  0---取消收藏，1---添加收藏, 2--查询
    private int state = 0;     //记录当前状态
    private int lastState = 0;//记录上次状态
    private String token,userid,goodsID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_message_activity);

        //初始化控件
        init();
        Log.i(TAG, "你已执行到GoodsItemActivity......");

        //获取从GoodsItemAdapter传过来的商品数据
        Intent intent = getIntent();
        //从intent取出bundle
        Bundle bundle = intent.getExtras();
        Goods goods = (Goods) bundle.getSerializable("goods");

        //将正在展示的商品的goodID存储起来，供收藏商品使用
        SharedPreferences.Editor editor = getSharedPreferences("collect",MODE_PRIVATE).edit();
        editor.putString("goodsID",goods.getGoodsID());
        editor.commit();
        //判断从GoodsItemAdapter接收到的数据是否为空
        if (goods == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GoodsItemActivity.this, "暂时没有数据哦！", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //将对应的item显示在对应的位置
        show(goods);
        CollectBO collectBO = new CollectBO();
        SharedPreferences preferences = getSharedPreferences("userinfo",MODE_PRIVATE);
        token = preferences.getString("token","");
        Log.i("TAG","GoodsItemActivity中token--->"+token);
        userid = preferences.getString("userid","");
        Log.i("TAG", "GoodsItemActivity中userid--->"+userid);
        SharedPreferences pref = getSharedPreferences("collect",MODE_PRIVATE);
        goodsID = pref.getString("goodsID","");
        Log.i(TAG, "GoodsItemActivity中goodsID--->"+goodsID);

        collectBO.setCollectFlag(collectFlag);
        collectBO.setToken(token);
        collectBO.setUserid(userid);
        collectBO.setGoodsID(goodsID);

        //发送OkHttp请求
        isCollectGoods(collectBO);

        //收藏
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastState == 0) {//未收藏
                    collectFlag = 1;//进入商品详情前，未收藏，然后点击则去做收藏
                }
                if(lastState == 1){//已收藏
                    collectFlag = 0;//进入详情前，已收藏，然后点金则去做取消收藏
                }
                CollectBO collectBO = new CollectBO();
                collectBO.setCollectFlag(collectFlag);
                collectBO.setToken(token);
                collectBO.setUserid(userid);
                collectBO.setGoodsID(goodsID);
                collectGoods(collectBO);
            }
        });
        //返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //商品详情展示
    public void show(Goods goods) {

        Log.i(TAG, "GoodsItemActivity从goods_item传来的数据goods--->" + goods.toString());

        //将对应的item显示在对应的位置
        String pictureUrl = goods.getPictureUrl();
        //若图片Url为空
        if(pictureUrl == null){
            iv_image.setImageResource(R.drawable.loading);

        }else{
            Glide.with(this).load(pictureUrl).into(iv_image);
        }
        tv_goodsId.setText(goods.getGoodsID());
        tv_goodsName.setText(goods.getGoodsName());
        tv_goodsPrice.setText(String.valueOf(goods.getPrice()));
        tv_goodsUnit.setText(goods.getUnit());
        tv_goodsQuantity.setText(String.valueOf(goods.getQuality()));
        tv_goodsQq.setText(goods.getQq());

    }

    //初始化控件
    public void init() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        tv_goodsId = (TextView) findViewById(R.id.tv_goodsId);
        tv_goodsName = (TextView) findViewById(R.id.tv_goodsName);
        tv_goodsPrice = (TextView) findViewById(R.id.tv_goodsPrice);
        tv_goodsUnit = (TextView) findViewById(R.id.tv_goodsUnit);
        tv_goodsQuantity = (TextView) findViewById(R.id.tv_goodsQuantity);
        tv_goodsQq = (TextView) findViewById(R.id.tv_goodsQq);

//        iv_collect = (ImageView) findViewById(R.id.iv_collect);//收藏
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }
    //发送OkHttp请求
    private void collectGoods(CollectBO collectBO) {

        Gson gson = new Gson();
        String collectJsonStr = gson.toJson(collectBO, CollectBO.class);
        Log.i(TAG, "GoodsItemActivity中需要传到后台的collectJsonStr is :" + collectJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/collectGoods";
        HttpUtils.sendOkHttpRequest(url, collectJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GoodsItemActivity.this, "目前网络不佳！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());
                    final String collectJsonStr = response.body().string();
                    Log.d(TAG, "GoodsItemActivity中collectJsonStr--->" + collectJsonStr);
                    //将response.code()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(collectJsonStr, UserVO.class);
                    final int flag = userVO.getFlag();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flag == 200){
                                    if(collectFlag == 1){
                                        Toast.makeText(GoodsItemActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                                        state = 1;
//                                        iv_collect.setImageResource(R.drawable.goods_collect_yellow);
                                        fab.setImageResource(R.drawable.goods_collect_yellow);

                                    }else{
                                        Toast.makeText(GoodsItemActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                                        state = 0;
//                                        iv_collect.setImageResource(R.drawable.goods_collect_gray);
                                        fab.setImageResource(R.drawable.goods_collect_gray);
                                    }
                                    lastState = state;
                                }
                                if (flag == 201){
                                    Toast.makeText(GoodsItemActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }

    //发送OkHttp请求
    private void isCollectGoods(CollectBO collectBO) {

        Gson gson = new Gson();
        String collectJsonStr = gson.toJson(collectBO, CollectBO.class);
        Log.i(TAG, "GoodsItemActivity中需要传到后台的collectJsonStr is :" + collectJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/collectGoods";
        HttpUtils.sendOkHttpRequest(url, collectJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GoodsItemActivity.this, "目前网络不佳！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());
                    final String collectJsonStr = response.body().string();
                    Log.d(TAG, "GoodsItemActivity中collectJsonStr--->" + collectJsonStr);

                    //将response.code()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(collectJsonStr, UserVO.class);
                    final int flag = userVO.getFlag();
                    collectFlag = userVO.getCollectFlag();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 40001){//商品已被收藏
//                                iv_collect.setImageResource(R.drawable.goods_collect_yellow);
                                fab.setImageResource(R.drawable.goods_collect_yellow);
                                state = 1;
                                lastState = state;
                            }else {//未被收藏
//                                iv_collect.setImageResource(R.drawable.goods_collect_gray);
                                fab.setImageResource(R.drawable.goods_collect_gray);
                                state = 0;
                                lastState = state;
                            }
                        }
                    });
                }
            }
        });

    }
}
