package com.ghl.wuhan.secondhand.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.MyApplication;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.adapter.GoodsItemAdapter;
import com.ghl.wuhan.secondhand.find_activity.find_buy_activity;
import com.ghl.wuhan.secondhand.find_activity.find_sale_activity;
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

import static android.view.View.VISIBLE;


public class find_fragment extends Fragment {
    private TextView tv_tan,tv_qiu;
    private ImageView image_ti;
    private String TAG = "TAG";
    private SharedPreferences preferences;
    private int opType = 90004;//操作类型

    //查询列表中的属性
    RecyclerView recyclerView;
    List<Goods> resultGoodsList = new ArrayList<Goods>();
    List<Goods> allGoodsList = new ArrayList<Goods>();
    List<Goods> saleGoodsList = new ArrayList<Goods>();
    List<Goods> sale_allGoodsList = new ArrayList<Goods>();
    List<Goods> buyGoodsList = new ArrayList<Goods>();
    List<Goods> buy_allGoodsList = new ArrayList<Goods>();
    private SpringView springView;//下拉刷新，上拉加载的控件
    public int page = 1;//页数
    protected int checkType = 1;//查询方式 1---上拉加载更多  2---下拉刷新
    public int pageSize = 5;//数据条数
    public int flagType = 1;//1--摊位，2--求购,3--收藏列表(me_collectActivity)
    public int formType = 1;//1--出售商品，2--购买商品
    private ImageView iv_networkbad;//无网络
    private boolean networkState;//网络状态




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_fragment,container,false);

        //初始化部分
        tv_tan=(TextView)view.findViewById(R.id.tv_tan);
        tv_qiu=(TextView) view.findViewById(R.id.tv_qiu);
        image_ti=(ImageView)view.findViewById(R.id.image_ti);
        iv_networkbad = (ImageView) view.findViewById(R.id.iv_networkbad);//无网络
        //默认状态是不可见
        iv_networkbad.setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        springView = (SpringView) view.findViewById(R.id.springView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        //设置默认状态白底蓝字
        tv_tan.setEnabled(false);
        tv_tan.setTextColor(Color.parseColor("#0895e7"));


        getData( opType);
        //摊位
        tv_tan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagType = 1;//表示方式为摊位
                Log.i(TAG,"摊位flagType--->"+flagType);
                getData( opType);
                formType = 1;//表示出售商品
                tv_tan.setEnabled(false);
                tv_tan.setTextColor(Color.parseColor("#0895e7"));
                tv_qiu.setEnabled(true);
                tv_qiu.setTextColor(Color.parseColor("#ffffff"));
            }
        });
        //求购
        tv_qiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flagType = 2;//表示方式为求购
                Log.i(TAG,"求购flagType--->"+flagType);
                getData( opType);
                formType = 2;//表示购买商品
                tv_tan.setEnabled(true);
                tv_tan.setTextColor(Color.parseColor("#ffffff"));
                tv_qiu.setEnabled(false);
                tv_qiu.setTextColor(Color.parseColor("#0895e7"));

            }
        });


        springView.setHeader(new DefaultHeader(getActivity()));
        springView.setFooter(new DefaultFooter(getActivity()));

        springView.setListener(new SpringView.OnFreshListener() {
            //刷新
            @Override
            public void onRefresh() {
                page = 1;
                checkType = 2;
                Log.i(TAG, "find_fragment中onRefresh: page is " + page);
                //getData();
                //
                getData(opType);
                springView.onFinishFreshAndLoad();
            }

            //加载更多
            @Override
            public void onLoadmore() {
                page++;
                checkType = 1;
                Log.i(TAG, "find_fragment中onRefresh: page is " + page);
                /*********/
                //getData();
                getData(opType);
                /*********/
                springView.onFinishFreshAndLoad();
            }
        });

        image_ti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formType == 1){//出售商品
                    Intent intent = new Intent(getActivity(),find_sale_activity.class);
                    startActivity(intent);
                }else if(formType == 2){//购买商品
                    Intent intent = new Intent(getActivity(),find_buy_activity.class);
                    startActivity(intent);
                }
            }
        });
        return view;

    }

    private void getData(int opType) {

        Goods goods = new Goods();
        goods.setOpType(opType);

        goods.setCheckType(checkType);
        goods.setPage(page);
        goods.setPageSize(pageSize);
        goods.setFlagType(flagType);

        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String userJsonStr = gson.toJson(goods, Goods.class);

        Log.i(TAG, "find_fragment中查询商品中userJsonStr is :" + userJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/findshow";

        //false
        MyApplication application = (MyApplication)getActivity().getApplication();
        application.setFlag(false);
        HttpUtils.sendOkHttpRequest(url, userJsonStr, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //true
                MyApplication application = (MyApplication)getActivity().getApplication();
                application.setFlag(true);
                Log.d(TAG, "获取数据失败了" + e.toString());
                networkState = NetworkStateUtils.isNetworkConnected(getContext());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(networkState == true ){
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(VISIBLE);
                            Toast.makeText(getActivity(),"你的服务器在开小差哦",Toast.LENGTH_SHORT).show();

                        }else{
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(VISIBLE);
                            Toast.makeText(getActivity(),"你的网络在开小差哦！",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    //true
                    MyApplication application = (MyApplication)getActivity().getApplication();
                    application.setFlag(true);
                    Log.d(TAG, "find_fragment中response.code()==" + response.code());

                    final String jsonData = response.body().string();
                    Log.d(TAG, "find_fragment中查询商品中的response.body().string()==" + jsonData);

                    Gson gson = new Gson();
                    Log.i(TAG, "开始解析jsonData");
                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
                    Log.i(TAG, "结束解析jsonData");
                    Log.i(TAG, "结束解析responseBuy:" + responseBuy);
                    //Log.i(TAG,"查询商品的列表："+ responseBuy.getGoodList().get(0));
                    final int flag = responseBuy.getFlag();
                    Log.i(TAG, "flag==" + flag);

                    final int flagType = responseBuy.getFlagType();
                    Log.i(TAG, "find_fragment中flagType---> "+flagType);
                    resultGoodsList = responseBuy.getGoodsList();


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            iv_networkbad.setVisibility(View.GONE);
                            if (flag == 200) {
                                Log.i(TAG, "run: success");


                                //摊位的列表展示
                                if(flagType == 1){
                                    saleGoodsList = resultGoodsList;
                                    if(saleGoodsList == null){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "您的登录信息已过期，请重新登录！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        return;
                                    }

                                    if (saleGoodsList.size() == 0) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "已经到达底线了哦！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        //将上一次的resultGoodsList加进来显示
                                        for (int i = 0; i < saleGoodsList.size(); i++) {
                                            boolean repeat = false;//判断加入新的List中的getGoodsID是否与旧的List中的getGoodsID是否一样一样则不重复加载
                                            for(int j = 0 ;j<sale_allGoodsList.size();j++){

                                                if(sale_allGoodsList.get(j).getGoodsID().equals(saleGoodsList.get(i).getGoodsID())){
                                                    repeat = true;
                                                }

                                            }
                                            if (repeat == false){
                                                sale_allGoodsList.add(saleGoodsList.get(i));
                                                Log.i(TAG, "home_fragment中sale_allGoodsList.size() " + sale_allGoodsList.size());
                                            }
                                        }
                                    }
                                    if(allGoodsList.size() == 0){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"暂时没有新的数据哦！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }

                                //求购列表展示
                                if(flagType == 2){
                                    buyGoodsList = resultGoodsList;
                                    if(buyGoodsList == null){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "您的登录信息已过期，请重新登录！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        return;
                                    }

                                    if (buyGoodsList.size() == 0) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "已经到达底线了哦！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        //将上一次的resultGoodsList加进来显示
                                        for (int i = 0; i < buyGoodsList.size(); i++) {
                                            boolean repeat = false;//判断加入新的List中的getGoodsID是否与旧的List中的getGoodsID是否一样一样则不重复加载
                                            for(int j = 0 ;j<buy_allGoodsList.size();j++){

                                                if(buy_allGoodsList.get(j).getGoodsID().equals(buyGoodsList.get(i).getGoodsID())){
                                                    repeat = true;
                                                }

                                            }
                                            if (repeat == false){
                                                buy_allGoodsList.add(buyGoodsList.get(i));
                                                Log.i(TAG, "home_fragment中buy_allGoodsList.size() " + buy_allGoodsList.size());
                                            }
                                        }
                                    }

                                    if(allGoodsList.size() == 0){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"暂时没有新的数据哦！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }


                                }

                                //摊位列表适配
                                if(flagType == 1){
                                    GoodsItemAdapter adapter = new GoodsItemAdapter(getContext(),sale_allGoodsList);
                                    recyclerView.setAdapter(adapter);
                                }
                                //求购列表适配
                                if(flagType == 2){
                                    GoodsItemAdapter adapter = new GoodsItemAdapter(getContext(),buy_allGoodsList);
                                    recyclerView.setAdapter(adapter);
                                }

//                                Toast.makeText(getActivity(), "查询成功！", Toast.LENGTH_SHORT).show();

                            } else if (flag == 30001) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "登录信息已失效,请再次登录", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "查询失败！", Toast.LENGTH_SHORT).show();
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
