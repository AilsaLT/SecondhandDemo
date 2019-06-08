package com.ghl.wuhan.secondhand.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.me_activity.me_user_login;
import com.ghl.wuhan.secondhand.me_activity.me_user_set;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;


public class me_fragment extends Fragment {
    //private ImageView iv_deng;
    private RelativeLayout rl_deng;
    private RelativeLayout rl_deng1;
    private ImageView iv_set;//设置
    private TextView tv_deng1;
    private SharedPreferences pref;
    private String TAG = "TAG";

    private boolean login;//是否登录成功的标志
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.me_fragment,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //取数据
        pref = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        String uname = pref.getString("uname","");
        Log.i(TAG,"me_fragment中的uname--->"+uname);
        //初始化
        rl_deng=(RelativeLayout)getActivity().findViewById(R.id.rl_deng);
        iv_set = (ImageView)getActivity().findViewById(R.id.iv_set);
        rl_deng1 = getActivity().findViewById(R.id.rl_deng1);
        tv_deng1 = getActivity().findViewById(R.id.tv_deng1);

        //VISIBLE:0  意思是可见的
        //INVISIBLE:4 意思是不可见的，但还占着原来的空间
        //GONE:8  意思是不可见的，不占用原来的布局空间
        rl_deng.setVisibility(GONE);
        rl_deng1.setVisibility(GONE);

        String extra = getArguments().getString("extra");

        if(isEmpty(extra)){
            rl_deng.setVisibility(View.VISIBLE);
        }else{

            rl_deng1.setVisibility(View.VISIBLE);
            tv_deng1.setText(uname);
        }

        //当进入APP后，如果之前没有退出登录状态，则一直显示你的用户名
        boolean login = pref.getBoolean("login",false);
        Log.i(TAG,"me_fragment中login--->"+login);
        if(login == true ){
            rl_deng1.setVisibility(View.VISIBLE);
            tv_deng1.setText(uname);
            rl_deng.setVisibility(GONE);
        }else {
            rl_deng.setVisibility(View.VISIBLE);
            rl_deng1.setVisibility(GONE);
        }

        //登录
        rl_deng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),me_user_login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //设置
        iv_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),me_user_set.class);
                startActivity(intent);
            }
        });


    }
}
