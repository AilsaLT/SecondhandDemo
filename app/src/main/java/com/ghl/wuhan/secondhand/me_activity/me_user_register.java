package com.ghl.wuhan.secondhand.me_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.UserBO;
import com.ghl.wuhan.secondhand.DO.UserVO;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.util.DialogUIUtils;
import com.ghl.wuhan.secondhand.util.HttpUtil;
import com.ghl.wuhan.secondhand.util.ImageUtils;
import com.google.gson.Gson;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

import static com.ghl.wuhan.secondhand.util.DialogUIUtils.dismiss;

public class me_user_register extends AppCompatActivity {

    //属性定义
    private RelativeLayout rl_back;
    private String TAG = "TAG";

    private CircleImageView icon_image;
    private Uri imageUri;//
    private File filePath;

    private EditText et_uname, et_password, et_qr;//用户名,密码和密码的确认
    private Button btn_register;//提交用户名和密码使用
    private final int opType = 90001;//操作类型

    private String uphone;
    private int sex;
    private Dialog progressDialog;
    private SharedPreferences.Editor editor;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_IMAGE = 3;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片的照片显示出来
                    //需要对拍摄的照片进行处理编辑
                    //拍照成功的话，就调用BitmapFactory的decodeStream()方法把图片解析成Bitmap对象，然后显示
                    Log.i( TAG, "onActivityResult TakePhoto : "+imageUri );
                    //Bitmap bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream( imageUri ) );
                    //photo_taken.setImageBitmap( bitmap );
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
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(imageUri,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        //                        photo_taken.setImageBitmap(bitmap);
                        //设置照片存储文件及剪切图片
                        File saveFile = ImageUtils.setTempFile( me_user_register.this );
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
                    // 通过图片URI拿到剪切图片
                    //bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream( imageUri ) );
                    //通过FileName拿到图片
                    Bitmap bitmap = BitmapFactory.decodeFile( filePath.toString() );
                    //把裁剪后的图片展示出来
                    icon_image.setImageBitmap( bitmap );
                    //ImageUtils.Compress( bitmap );
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View contentView = LayoutInflater.from(this).inflate(R.layout.me_user_register, null);
        //这两句必须放在super.onCreate(savedInstanceState);之后， setContentView(contentView);之前
        setContentView(contentView);

        //初始化
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_password = (EditText) findViewById(R.id.et_password);
        et_qr = (EditText) findViewById(R.id.et_qr);
        btn_register = (Button) findViewById(R.id.btn_register);
        icon_image = (CircleImageView)findViewById(R.id.icon_image);//使图片能处理成圆形

        Log.i(TAG, "************onCreate init********");

        //点击弹出底部弹框，选择拍照或相册进行照片的选择
        icon_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //具体实现部分
                List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(me_user_register.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //选择调用手机相机拍照或在相册中选择照片的逻辑实现部分
                        switch (position){
                            case 0:
                                //选择调用手机相机
                                Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                imageUri = ImageUtils.getImageUri(me_user_register.this);
                                Log.i(TAG,"me_user_register中的imageUri--->"+imageUri);
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
            }
        });



        //取消注册
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击注册
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "************click btn success********");
                //获取用户名和密码参数
                final String uname = et_uname.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                final String upassword = et_password.getText().toString().trim();
                final String uqr = et_qr.getText().toString().trim();
                Log.i(TAG, "uname is :" + uname);
                Log.i(TAG, "upassword is :" + upassword);
                //6-16位数字字母混合,不能全为数字,不能全为字母,首位不能为数字

                String regex="^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";//密码格式验证(正则)
                Pattern p= Pattern.compile(regex);
                Matcher m=p.matcher(upassword);
                boolean isMatch=m.matches();

                if(uname.isEmpty()&&upassword.isEmpty()&&uqr.isEmpty()){
                    Toast.makeText(me_user_register.this,"请输入注册信息",Toast.LENGTH_SHORT).show();
                }else if(uname.isEmpty()){
                    Toast.makeText(me_user_register.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(upassword.isEmpty()){
                    Toast.makeText(me_user_register.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if(isMatch    ==  false){
                    Toast.makeText(me_user_register.this,"密码格式不对，6-16位数字字母混合,不能全为数字,不能全为字母,首位不能为数字",Toast.LENGTH_LONG).show();
                }else if(uqr.isEmpty()){
                    Toast.makeText(me_user_register.this,"请再次确认密码",Toast.LENGTH_SHORT).show();
                }else if(upassword.equals(uqr)==false){
                    Toast.makeText(me_user_register.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                }else {
                    //进度条的设置
                    progressDialog = DialogUIUtils.showLoadingDialog(me_user_register.this,"正在注册......");
                    progressDialog.show();
                    //点击物理返回键是否可取消dialog
                    progressDialog.setCancelable(true);
                    //点击dialog之外 是否可取消
                    progressDialog.setCanceledOnTouchOutside(false);

//                    //bitmap转byte[]
//                    Resources res = getResources();
//                    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.bt);
//                    final byte[] uimages = Bitmap2Bytes(bmp);
//                    Log.i(TAG, "获取图片成功.......");
//
//                    //将照片取出来


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            register(opType, uname, upassword, uimages, uphone, sex);
                            register(opType, uname, upassword,uphone, sex);
                        }
                    }).start();
                }
                //                Log.i(TAG, "register()函数调用成功");
            }
        });

    }
    //将对象转换成json串
    private void register(int opType, final String uname, String upassword, String uphone, int sex) {

        UserBO userBO = new UserBO();
        String uuid = UUID.randomUUID().toString();
        userBO.setUid(uuid);
        userBO.setUname(uname);
        userBO.setUpassword(upassword);
//        userBO.setUimage(uimages);
        userBO.setOpType(opType);
        userBO.setSex(sex);
        userBO.setUphone(uphone);

        Gson gson = new Gson();
        String userJsonStr = gson.toJson(userBO, UserBO.class);
        Log.i(TAG, "userJsonStr :" + userJsonStr);


        String url = "http://47.105.183.54:8080/Proj20/register";
        HttpUtil.sendOkHttpRequest(url, userJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String s = response.body().string();
                    Log.d(TAG, "response.body().string()==" + s);

                    //将response.body().string()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(s, UserVO.class);
                    int flag = userVO.getFlag();

                    if (flag == 200) {

                        //注册成功后将用户名存储起来
                                                editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                                editor.putString("uname",uname);
                                                editor.commit();
//                        SharedPreferencesUtil.saveSP("uname",uname);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_register.this, "您已注册成功！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });

                        Intent intent = new Intent(me_user_register.this,me_user_login.class);
                        startActivity(intent);
                        finish();

                    }
                    if (flag == 10002)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_register.this, "该用户名已存在，注册失败！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                    if (flag == 10003)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_register.this, "用户名为空，注册失败！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                    if (flag == 10004)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_register.this, "密码为空，注册失败！", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                    if (flag == 88888)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(me_user_register.this, "json parase error,please check your josn str.", Toast.LENGTH_SHORT).show();
                                //使进度条不可见
                                dismiss(progressDialog);
                            }
                        });
                }
            }
        });//此处省略回调方法
    }
    // bitmp转bytes
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void startImageCrop(File saveToFile,Uri uri) {
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
        intent.putExtra( "outputFormat", Bitmap.CompressFormat.PNG.toString() );
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
