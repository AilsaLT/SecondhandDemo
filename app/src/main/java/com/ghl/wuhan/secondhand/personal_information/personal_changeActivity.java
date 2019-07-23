package com.ghl.wuhan.secondhand.personal_information;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.DO.ResponseBuy;
import com.ghl.wuhan.secondhand.DO.User;
import com.ghl.wuhan.secondhand.MainActivity;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.util.COSPictureUtils;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.ghl.wuhan.secondhand.util.ImageUtils;
import com.google.gson.Gson;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class personal_changeActivity extends AppCompatActivity implements View.OnClickListener{
    //属性定义
    private ImageView iv_back;
    private String TAG = "TAG";
    private EditText et_nickname_show,et_qq_show,et_wichat_show,et_phone_show,et_sex_show;

    private Button button;//确定修改
    private SharedPreferences preferences;
    private int opType;
    private int sex;
    private byte [] bts_uimages;
    //拍照
    private CircleImageView icon_image;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_IMAGE = 3;
    private File filePath;
    private String pictureUrl;//图片Url



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片的照片显示出来
                    //需要对拍摄的照片进行处理编辑
                    //拍照成功的话，就调用BitmapFactory的decodeStream()方法把图片解析成Bitmap对象，然后显示
                    Log.i( TAG, "onActivityResult TakePhoto : "+imageUri );
                    //设置照片存储文件及剪切图片
                    File saveFile = ImageUtils.getTempFile();
                    filePath = ImageUtils.getTempFile();
                    startImageCrop( saveFile,imageUri );
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //选中相册照片显示
                    Log.i( TAG, "onActivityResult: 执行到打开相册了" );
                    try {
                        imageUri = data.getData(); //获取系统返回的照片的Uri
                        Log.i( TAG, "onActivityResult: uriImage is " +imageUri );
                        //设置照片存储文件及剪切图片
                        File saveFile = ImageUtils.setTempFile(personal_changeActivity.this );
                        filePath = ImageUtils.getTempFile();
                        startImageCrop( saveFile,imageUri );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_IMAGE:
                if(resultCode == RESULT_OK){
                    Log.i( TAG, "onActivityResult: CROP_IMAGE" + "进入了CROP");

                    Bitmap bitmap = BitmapFactory.decodeFile( filePath.toString() );
                    //把裁剪后的图片展示出来
                    icon_image.setImageBitmap( bitmap );
                    //上传图片对象
                    COSPictureUtils.initCOS(personal_changeActivity.this);
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_change_activity);

        //初始化控件
        init();

        //获取该用户的信息并显示其中的信息
        preferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        //昵称
        String uname = preferences.getString("uname","");

        if(uname != null){
            et_nickname_show.setText(uname);
        }else {
            return;
        }

        //头像
        String pictureUrl = preferences.getString("pictureUrl","");
        Log.i(TAG,"personal_changeActivity中pictureUrl--->"+pictureUrl);
        boolean login = preferences.getBoolean("login",false);
        Log.i(TAG,"me_fragment中login--->"+login);
        if(login == true){
            Glide.with(this).load(pictureUrl).error(R.drawable.avatar_loading_fail).into(icon_image);
        }

        SharedPreferences preferences = getSharedPreferences("userinfo",MODE_PRIVATE);

        //QQ号
        String qq = preferences.getString("qq","");
        et_qq_show.setText(qq);
        //微信号
        String wichat = preferences.getString("weixin","");
        et_wichat_show.setText(wichat);
        //电话
        String phone = preferences.getString("phone","");
        et_phone_show.setText(phone);
        //性别
        int sex = preferences.getInt("sex", 0);
        if(sex == 0){
            et_sex_show.setText("女");
        }else if(sex == 1){
            et_sex_show.setText("男");
        }


        //1.注：此时setOnClickListener传入的是this
        iv_back.setOnClickListener(this);
        button.setOnClickListener(this);
        icon_image.setOnClickListener(this);
    }

    //初始化控件
    public void init(){
        iv_back = (ImageView) findViewById(R.id.iv_back);
        icon_image = (CircleImageView) findViewById(R.id.icon_image);
        et_nickname_show = (EditText) findViewById(R.id.et_nickname_show);
        et_qq_show = (EditText) findViewById(R.id.et_qq_show);
        et_wichat_show = (EditText) findViewById(R.id.et_wichat_show);
        et_phone_show = (EditText) findViewById(R.id.et_phone_show);
        et_sex_show = (EditText) findViewById(R.id.et_sex_show);
        button = (Button) findViewById(R.id.button);//确定修改

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.icon_image:
                //具体实现部分
                List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(personal_changeActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //选择调用手机相机拍照或在相册中选择照片的逻辑实现部分
                        switch (position){
                            case 0:
                                //选择调用手机相机
                                Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                imageUri = ImageUtils.getImageUri(personal_changeActivity.this);
                                Log.i(TAG,"find_sale_activity中的imageUri--->"+imageUri);
                                //putExtra()指定图片的输出地址，填入之前获得的Uri对象
                                photo.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                startActivityForResult(photo,TAKE_PHOTO);
                                //底部弹框消失
                                optionBottomDialog.dismiss();

                                break;
                            case 1:
                                //选择相册
                                Intent picsIn = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult( picsIn, CHOOSE_PHOTO );
                                //底部弹框消失
                                optionBottomDialog.dismiss();

                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case R.id.button:
                    getData(opType);
            default:
                break;
        }

    }

    private void getData(int opType) {

        //获取EdiText上的值
        String nickName = et_nickname_show.getText().toString().trim();//昵称
        String qqNumber = et_qq_show.getText().toString().trim();//QQ号
        String wichatNumber = et_wichat_show.getText().toString().trim();//微信号
        String phoneNumber = et_phone_show.getText().toString().trim();//电话号码
        String gender = et_sex_show.getText().toString().trim();//性别
        if(gender.equals("女")){
            sex = 0;

        }else if(gender.equals("男")){
            sex = 1;
        }

        //获取已登录用户的token
        String token = preferences.getString("token","");
        Log.i(TAG,"personal_change_activity中的token--->"+token);
        String userid = preferences.getString("userid","");
        Log.i(TAG,"personal_change_activity中的userid--->"+userid);
        pictureUrl = COSPictureUtils.getPitureUrl();
        Log.i(TAG, "personal_changeActivity中pictureUrl--->"+pictureUrl);

        User user = new User();
        user.setToken(token);
        user.setUserid(userid);
        user.setOpType(opType);
        user.setUname(nickName);
        user.setQq(qqNumber);
        user.setWeixin(wichatNumber);
        user.setSex(sex);
//        user.setUimage(bts_uimages);
        user.setUphone(phoneNumber);
        user.setPictureUrl(pictureUrl);



        //将获取的对象转换成Json串
        Gson gson = new Gson();
        String userJsonStr = gson.toJson(user, User.class);
        Log.i(TAG, "personal_change_activity中的userJsonStr is :" + userJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/userupdate";
        HttpUtils.sendOkHttpRequest(url, userJsonStr, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "personal_change_activity中的response.code()==" + response.code());

                    final String jsonData = response.body().string();
                    Log.d(TAG, "personal_change_activity中的response.body().string()==" + jsonData);

                    Gson gson = new Gson();
                    Log.i(TAG, "开始解析jsonData");
                    ResponseBuy responseBuy = gson.fromJson(jsonData, ResponseBuy.class);
                    Log.i(TAG, "结束解析jsonData");
                    Log.i(TAG, "结束解析responseBuy:" + responseBuy);
                    //Log.i(TAG,"查询商品的列表："+ responseBuy.getGoodList().get(0));
                    final int flag = responseBuy.getFlag();
                    Log.i(TAG, "flag==" + flag);

                    final String qq = responseBuy.getQq();
                    final String weixin = responseBuy.getWeixin();
                    final String phone = responseBuy.getUphone();
                    final int sex = responseBuy.getSex();
                    final String uname = responseBuy.getUname();




                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 200) {
                                Log.i(TAG, "run: success");

                                SharedPreferences.Editor editor = getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                                editor.putString("qq",qq);
                                editor.putString("weixin",weixin);
                                editor.putString("phone",phone);
                                editor.putInt("sex",sex);
                                editor.putString("pictureUrl",pictureUrl);
                                editor.putString("uname",uname);
                                editor.commit();

                                Toast.makeText(personal_changeActivity.this, "您的信息已修改成功！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(personal_changeActivity.this, MainActivity.class);
                                startActivity(intent);
                            }  else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(personal_changeActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     *  图片裁剪
     * */
    private void startImageCrop(File saveToFile, Uri uri) {
        if(uri == null){
            return ;
        }
        Intent intent = new Intent( "com.android.camera.action.CROP" );
        Log.i( TAG, "startImageCrop: " + "执行到压缩图片了" + "uri is " + uri );
        intent.setDataAndType( uri, "image/*" );//设置Uri及类型
        //uri权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra( "crop", "true" );//
        intent.putExtra( "aspectX", 1 );//X方向上的比例
        intent.putExtra( "aspectY", 1 );//Y方向上的比例
        intent.putExtra( "outputX", 150 );//裁剪区的X方向宽
        intent.putExtra( "outputY", 150 );//裁剪区的Y方向宽
        intent.putExtra( "scale", true );//是否保留比例
        intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString() );
        intent.putExtra( "return-data", false );//是否将数据保留在Bitmap中返回dataParcelable相应的Bitmap数据，防止造成OOM
        //判断文件是否存在
        //File saveToFile = ImageUtils.getTempFile();
        if (!saveToFile.getParentFile().exists()) {
            saveToFile.getParentFile().mkdirs();
        }
        //将剪切后的图片存储到此文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveToFile));
        Log.i( TAG, "startImageCrop: " + "即将跳到剪切图片" );
        startActivityForResult( intent, CROP_IMAGE );
    }

}
