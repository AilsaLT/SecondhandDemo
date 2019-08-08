package com.ghl.wuhan.secondhand.Fragment;

import android.content.Intent;
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
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.GlideImageLoader;
import com.ghl.wuhan.secondhand.MyApplication;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.adapter.GoodsItemAdapter;
import com.ghl.wuhan.secondhand.home_activity.home_search;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.ghl.wuhan.secondhand.util.NetworkStateUtils;
import com.google.gson.Gson;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.view.View.VISIBLE;

public class home_fragment extends Fragment implements OnBannerListener {
    private String TAG = "TAG";
    private Banner banner;
    private ArrayList<Integer> list_path;
    private ArrayList<String> list_title;
    private ImageView image_back, iv_networkbad;
    private int opType = 90004;//操作类型
    RecyclerView recyclerView;//查询列表中的属性
    List<Goods> newResultGoodsList = new ArrayList<Goods>();
    List<Goods> allGoodsList = new ArrayList<Goods>();
    private SpringView springView;//下拉刷新，上拉加载的控件
    private int pageRefresh;//刷新页数
    private int pageMore = 1;//加载更多页数
    protected int checkType = 1;//查询方式 1---上拉加载更多  2---下拉刷新
    public int pageSize = 5;//数据条数
    private boolean networkState;//网络状态

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        //初始化视图界面
        Banner banner = (Banner) view.findViewById(R.id.banner);
        ImageView image_back = (ImageView) view.findViewById(R.id.image_back);
        iv_networkbad = (ImageView) view.findViewById(R.id.iv_networkbad);
        iv_networkbad.setVisibility(View.GONE); //默认状态是不可见
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        springView = (SpringView) view.findViewById(R.id.springView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getData(opType);
        springView.setHeader(new DefaultHeader(getActivity()));
        springView.setFooter(new DefaultFooter(getActivity()));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() { //刷新
                pageRefresh = 1;
                checkType = 2;
                Log.i(TAG, "onRefresh: pageRefresh is " + pageRefresh);
                getData(opType);
                springView.onFinishFreshAndLoad();
            }

            @Override
            public void onLoadmore() { //加载更多
                pageMore++;
                checkType = 1;
                Log.i(TAG, "loadPage: pageMore is " + pageMore);
                getData(opType);
                springView.onFinishFreshAndLoad();
            }
        });
        list_path = new ArrayList<>();
        list_title = new ArrayList<>();//放标题的集合
        list_path.add(R.drawable.navigation_one);
        list_path.add(R.drawable.navigation_two);
        list_path.add(R.drawable.navigation_three);
        list_path.add(R.drawable.navigation_four);
        list_path.add(R.drawable.navigation_five);
        list_path.add(R.drawable.navigation_six);
        list_title.add("质量好");
        list_title.add("交易方便");
        list_title.add("便宜");
        list_title.add("是你想要的那个它！");
        list_title.add("是你想要的那个它！");
        list_title.add("是你想要的那个它！");
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器，图片加载器在下方
        banner.setImageLoader(new GlideImageLoader());
        //设置图片网址或地址的集合
        banner.setImages(list_path);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        banner.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
        banner.setBannerTitles(list_title);
        //设置轮播间隔时间
        banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        banner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        banner.setIndicatorGravity(BannerConfig.CENTER)
                //以上内容都可写成链式布局，这是轮播图的监听。比较重要。方法在下面。
                .setOnBannerListener(this)
                //必须最后调用的方法，启动轮播图。
                .start();

        //实现界面的跳转,跳到搜索界面
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), home_search.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void OnBannerClick(int position) {
        //        Log.i("tag", "你点了第" + position + "张轮播图");
    }

    private void getData(int opType) {
        Goods goods = new Goods();
        goods.setOpType(opType);
        goods.setCheckType(checkType);
        if (checkType == 1){//加载更多
            goods.setPage(pageMore);
        }else {
            goods.setPage(pageRefresh);
        }
        goods.setPageSize(pageSize);
        Gson gson = new Gson();
        String recommendJsonStr = gson.toJson(goods, Goods.class);
//        Log.i(TAG, "home_fragment-->recommendJsonStr is :-->" + recommendJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/recommend";
        MyApplication application = (MyApplication) getActivity().getApplication();
        application.setFlag(false);//false
        HttpUtils.sendOkHttpRequest(url, recommendJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                MyApplication application = (MyApplication) getActivity().getApplication();
                application.setFlag(true); //true
                Log.d(TAG, "获取数据失败了" + e.toString());
                networkState = NetworkStateUtils.isNetworkConnected(getContext()); //判断网络状态
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (networkState == true) {
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(VISIBLE);
                            Toast.makeText(getActivity(), "你的服务器在开小差哦", Toast.LENGTH_SHORT).show();
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            iv_networkbad.setVisibility(VISIBLE);
                            Toast.makeText(getActivity(), "你的网络在开小差哦！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
//                    Log.d(TAG, "获取数据成功了");
                    MyApplication application = (MyApplication) getActivity().getApplication();
                    application.setFlag(true);//true
//                    Log.d(TAG, "response.code()==" + response.code());
                    final String resultJsonData = response.body().string();//后台返回数据
//                    Log.d(TAG, "home_fragment-->resultJsonData: " + resultJsonData);
                    Gson gson = new Gson();
                    ResponseBuy responseBuy = gson.fromJson(resultJsonData, ResponseBuy.class);//解析
//                    Log.i(TAG, "结束解析responseBuy:" + responseBuy);
                    final int flag = responseBuy.getFlag();
                    Log.i(TAG, "flag==" + flag);
                    newResultGoodsList = responseBuy.getGoodsList();//获取的商品列表
                    Log.i(TAG, "home_fragement--newResultGoodsList"+newResultGoodsList);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            iv_networkbad.setVisibility(View.GONE);
                            if (flag == 200) {
                                Log.i(TAG, "run: success");
                                //如果一直上拉刷新，当newResultGoodsList返回为null，说明数据库里已经没有更多数据了
                                if (newResultGoodsList.size() <=0) {
                                    Toast.makeText(getActivity(), "没有更多内容了哦！", Toast.LENGTH_SHORT).show();
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
                                            Log.i(TAG, "home_fragment中allGoodsList.size() " + allGoodsList.size());
                                        }
                                    }
                                    GoodsItemAdapter adapter = new GoodsItemAdapter(getContext(), allGoodsList);
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
                                            Log.i(TAG, "home_fragment中allGoodsList.size() " + allGoodsList.size());
                                        }
                                    }
                                    allGoodsList = newResultGoodsList;
                                    GoodsItemAdapter adapter = new GoodsItemAdapter(getContext(), allGoodsList);
                                    recyclerView.setAdapter(adapter);
//                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }

                            } else if (flag == 30001) {
                                Toast.makeText(getActivity(), "登录信息已失效,请再次登录", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "查询失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
