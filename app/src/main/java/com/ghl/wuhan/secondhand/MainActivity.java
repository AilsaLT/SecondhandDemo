package com.ghl.wuhan.secondhand;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.Fragment.find_fragment;
import com.ghl.wuhan.secondhand.Fragment.home_fragment;
import com.ghl.wuhan.secondhand.Fragment.me_fragment;
import com.ghl.wuhan.secondhand.Fragment.sort_fragment;

import static android.text.TextUtils.isEmpty;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private BottomNavigationView bottomNavigationView;
    private home_fragment home_fragment;
    private sort_fragment sort_fragment;
    private find_fragment find_fragment;
    private me_fragment me_fragment;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment

    private String TAG = "TAG";

    //初始化fragment和fragment数组
    private void initFragment() {

        Intent intent = getIntent();
        String extra = intent.getStringExtra("extra");
        Bundle bundle = new Bundle();//该类用作携带数据
        bundle.putString("extra",extra);
        Log.i(TAG,"main中的extra："+extra);


        home_fragment = new home_fragment();
        sort_fragment = new sort_fragment();
        find_fragment = new find_fragment();
        me_fragment = new me_fragment();
        fragments = new Fragment[]{home_fragment, sort_fragment, find_fragment, me_fragment};
        lastfragment = 0;

        getSupportFragmentManager().beginTransaction().replace(R.id.mainview, home_fragment).show(home_fragment).commit();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        // 通过反射机制实现超过3图标时，不能显示文字的问题
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        View tab = menuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
        //加载我们的角标View，新创建的一个布局
        View badge = LayoutInflater.from(this).inflate(R.layout.im_badge, menuView, false);
        //添加到Tab上
        itemView.addView(badge);
        TextView textView = badge.findViewById(R.id.texT);
        textView.setText(String.valueOf(9));
        //无消息时可以将它隐藏即可
        textView.setVisibility(View.INVISIBLE);


        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
        me_fragment.setArguments(bundle);
    }

    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.i("test", "item.getItemId() is :" + item.getItemId());
            MyApplication application = (MyApplication) getApplication();//全局变量

            if(application.isFlag() == true){//只有当前的fragment的网络请求结束后才能切换到下一个fragment
                switch (item.getItemId()) {
                    case R.id.navigation_home: {


                        if (lastfragment != 0) {
                            switchFragment(lastfragment, 0);
                            lastfragment = 0;

                        }

                        return true;
                    }
                    case R.id.navigation_dashboard: {
                        if (lastfragment != 1) {
                            switchFragment(lastfragment, 1);
                            lastfragment = 1;

                        }

                        return true;
                    }
                    case R.id.navigation_notifications: {
                        if (lastfragment != 2) {
                            switchFragment(lastfragment, 2);
                            lastfragment = 2;

                        }

                        return true;
                    }
                    case R.id.navigation_me: {
                        if (lastfragment != 3) {
                            switchFragment(lastfragment, 3);
                            lastfragment = 3;

                        }

                        return true;
                    }

                }

            }


            return false;
        }

        private void switchFragment(int lastfragment, int index) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
            if (fragments[index].isAdded() == false) {
                transaction.add(R.id.mainview, fragments[index]);

            }
            transaction.show(fragments[index]).commitAllowingStateLoss();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化用这个方法
        initFragment();

        }

    //退出登录

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tag = intent.getStringExtra("EXIT_TAG");
        if (tag != null&& !isEmpty(tag)) {
            if ("SINGLETASK".equals(tag)) {//退出程序
                finish();
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
    }
}









