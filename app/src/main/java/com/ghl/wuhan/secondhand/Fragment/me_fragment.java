package com.ghl.wuhan.secondhand.Fragment;

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

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.me_activity.me_aboutUsActivity;
import com.ghl.wuhan.secondhand.me_activity.me_boothActivity;
import com.ghl.wuhan.secondhand.me_activity.me_buyActivity;
import com.ghl.wuhan.secondhand.me_activity.me_collectActivity;
import com.ghl.wuhan.secondhand.me_activity.me_user_login;
import com.ghl.wuhan.secondhand.me_activity.me_user_set;
import com.ghl.wuhan.secondhand.personal_information.personal_changeActivity;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;


public class me_fragment extends Fragment {
    //private ImageView iv_deng;
    private RelativeLayout rl_deng,rl_deng1;
    private RelativeLayout rl_booth,rl_buy,rl_collection,rl_me;
    private ImageView iv_set,iv_deng,iv_deng1;//设置
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
        pref = getActivity().getSharedPreferences("userinfo", MODE_PRIVATE);
        String uname = pref.getString("uname","");
        String pictureUrl = pref.getString("pictureUrl","");

        Log.i(TAG,"me_fragment中的uname--->"+uname);
        Log.i(TAG,"me_fragment中pictureUrl--->"+pictureUrl);

        //SharedPreferences pref2 = getActivity().getSharedPreferences("userinfo",MODE_PRIVATE);

//        byte [] bts_uimages = uimages.getBytes();
//        byte [] bts_uimages = Base64.decode(uimages.getBytes(),Base64.DEFAULT);
//        Log.i(TAG,"me_fragment中的bts_uimages--->"+bts_uimages);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bts_uimages,0,bts_uimages.length,null);
//        Log.i(TAG,"me_fragment中的bitmap形式的uimages--->"+bitmap);

        //初始化
        init();

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
//            iv_deng1.setImageBitmap(bitmap);
            Glide.with(this).load(pictureUrl).error(R.drawable.avatar_loading_fail).into(iv_deng1);
        }

        //当进入APP后，如果之前没有退出登录状态，则一直显示你的用户名
        boolean login = pref.getBoolean("login",false);
        Log.i(TAG,"me_fragment中login--->"+login);
        if(login == true ){
            rl_deng1.setVisibility(View.VISIBLE);
            tv_deng1.setText(uname);
//            iv_deng1.setImageBitmap(bitmap);
            Glide.with(this).load(pictureUrl).error(R.drawable.avatar_loading_fail).into(iv_deng1);
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
//                getActivity().finish();
            }
        });

        //我的摊位
        rl_booth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),me_boothActivity.class);
                startActivity(intent);
            }
        });
        //我的求购
        rl_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),me_buyActivity.class);
                startActivity(intent);
            }
        });
        //我的收藏
        rl_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),me_collectActivity.class);
                startActivity(intent);
            }
        });
        //关于我们
        rl_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity(),me_aboutUsActivity.class);
                startActivity(intent);
            }
        });

        //点击用户名跳转到个人信息修改
        tv_deng1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),personal_changeActivity.class);
                startActivity(intent);
            }
        });

        //点击头像跳转到个人信息修改
        iv_deng1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),personal_changeActivity.class);
                startActivity(intent);
            }
        });



    }

    //初始化控件
    public void init(){
        rl_deng=(RelativeLayout)getActivity().findViewById(R.id.rl_deng);
        iv_set = (ImageView)getActivity().findViewById(R.id.iv_set);

        rl_deng1 = getActivity().findViewById(R.id.rl_deng1);
        tv_deng1 = getActivity().findViewById(R.id.tv_deng1);
        iv_deng = getActivity().findViewById(R.id.iv_deng);
        iv_deng1 = getActivity().findViewById(R.id.iv_deng1);

        rl_booth = getActivity().findViewById(R.id.rl_booth);
        rl_buy = getActivity().findViewById(R.id.rl_buy);
        rl_collection = getActivity().findViewById(R.id.rl_colection);
        rl_me = getActivity().findViewById(R.id.rl_me);


    }
}
