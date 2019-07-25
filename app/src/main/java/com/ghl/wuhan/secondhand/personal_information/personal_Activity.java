package com.ghl.wuhan.secondhand.personal_information;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class personal_Activity extends AppCompatActivity implements View.OnClickListener{
   //属性定义
    private ImageView iv_avatar, iv_back,iv_nickname,iv_qq,iv_wichat,iv_phone,iv_sex;
    private TextView tv_nickname_show,tv_qq_show,tv_wichat_show,tv_phone_show,tv_sex_show;
    private CircleImageView icon_image;
    private SharedPreferences preferences;
    private String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity);

        //初始化控件
        init();
        //



//        byte [] bts_uimages = Base64.decode(avatar.getBytes(),Base64.DEFAULT);
//        Log.i(TAG,"personal_changeActivity中的bts_uimages--->"+bts_uimages);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bts_uimages,0,bts_uimages.length,null);
//        Log.i(TAG,"personal_changeActivity中的bitmap形式的uimages--->"+bitmap);
        preferences = getSharedPreferences("userinfo",MODE_PRIVATE);
        boolean login = preferences.getBoolean("login",false);
        Log.i(TAG,"me_fragment中login--->"+login);

        if(login == true){
            //将用户的个人信息显示出来

            //昵称
            String uname = preferences.getString("uname","");
            if(uname != null){
                tv_nickname_show.setText(uname);
            }else {
                return;
            }
            //SharedPreferences preferences = getSharedPreferences("userinfo",MODE_PRIVATE);
//            icon_image.setImageBitmap(bitmap);
//            Glide.with(this).load(pictureUrl).into(icon_image);

            //头像
            String pictureUrl = preferences.getString("pictureUrl","");
            Log.i(TAG,"personal_changeActivity中pictureUrl--->"+pictureUrl);
            Glide.with(this).load(pictureUrl).error(R.drawable.avatar_loading_fail).into(icon_image);
            //QQ号
            String qq = preferences.getString("qq","");
            tv_qq_show.setText(qq);
            //微信号
            String wichat = preferences.getString("weixin","");
            tv_wichat_show.setText(wichat);
            //电话
            String phone = preferences.getString("phone","");
            tv_phone_show.setText(phone);
            //性别
            int sex = preferences.getInt("sex", 0);
            if(sex == 0){
                tv_sex_show.setText("女");
            }else if(sex == 1){
                tv_sex_show.setText("男");
            }
        }



        //1.注：此时setOnClickListener传入的是this
        iv_back.setOnClickListener(this);
        iv_nickname.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
        iv_wichat.setOnClickListener(this);
        iv_phone.setOnClickListener(this);
        iv_sex.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);


    }

    //初始化控件
    public void init(){
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        icon_image = (CircleImageView) findViewById(R.id.icon_image);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_nickname = (ImageView) findViewById(R.id.iv_nickname);
        iv_qq = (ImageView) findViewById(R.id.iv_qq);
        iv_wichat = (ImageView) findViewById(R.id.iv_wichat);
        iv_phone = (ImageView) findViewById(R.id.iv_phone);
        iv_sex = (ImageView) findViewById(R.id.iv_sex);
        tv_nickname_show =(TextView) findViewById(R.id.tv_nickname_show);
        tv_qq_show = (TextView) findViewById(R.id.tv_qq_show);
        tv_wichat_show = (TextView) findViewById(R.id.tv_wichat_show);
        tv_phone_show = (TextView) findViewById(R.id.tv_phone_show);
        tv_sex_show = (TextView) findViewById(R.id.tv_sex_show);
    }


    //2.实现OnClickListener接口中的onClick方法
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
//                Intent intent =new Intent(personal_Activity.this,personal_changeActivity.class);
//                startActivity(intent);
                break;
            case R.id.iv_avatar:
                Intent intent_avatar = new Intent(personal_Activity.this,personal_changeActivity.class);
                startActivity(intent_avatar);
                finish();
                break;
            case R.id.iv_nickname:
                Intent intent_nick = new Intent(personal_Activity.this,personal_changeActivity.class);
                startActivity(intent_nick);
                finish();
                break;
            case R.id.iv_qq:
                Intent intent_password = new Intent(personal_Activity.this,personal_changeActivity.class);
                startActivity(intent_password);
                finish();
                break;
            case R.id.iv_wichat:
                Intent intent_wichat = new Intent(personal_Activity.this,personal_changeActivity.class);
                startActivity(intent_wichat);
                finish();
                break;
            case R.id.iv_phone:
                Intent intent_phone = new Intent(personal_Activity.this,personal_changeActivity.class);
                startActivity(intent_phone);
                finish();
                break;
            case R.id.iv_sex:
                Intent intent_sex = new Intent(personal_Activity.this,personal_changeActivity.class);
                startActivity(intent_sex);
                finish();
                break;
            default:
                break;
        }
    }
}
