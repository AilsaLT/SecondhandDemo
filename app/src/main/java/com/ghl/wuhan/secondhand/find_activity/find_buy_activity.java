package com.ghl.wuhan.secondhand.find_activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.BuyBO;
import com.ghl.wuhan.secondhand.DO.UserVO;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.util.COSPictureUtils;
import com.ghl.wuhan.secondhand.util.DialogUIUtils;
import com.ghl.wuhan.secondhand.util.HttpUtils;
import com.ghl.wuhan.secondhand.util.ImageUtils;
import com.ghl.wuhan.secondhand.util.NetworkStateUtils;
import com.google.gson.Gson;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;

import static java.lang.Float.parseFloat;

public class find_buy_activity extends AppCompatActivity {
    //属性定义
    private String TAG = "TAG";
    private ImageView iv_back;//返回
    private Button btn_submit;//点击提交

    //拍照
    private ImageView image_touxiang;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_IMAGE = 3;
    private File filePath;


    //销售商品
    private int opType = 90004;//操作类型
    private String goodsID;//ID
    private int goodsType;//商品所属类
    private String goodsName;//商品名
    private float price = 0.1f;// 价格
    private String qq;//联系方式
    private String unit; //单位
    private float quality = 1f;//数量
    private String userid;//发布人ID
    private String token;
    private String uname;


    private EditText et_goodsName, et_unit, et_price, et_quality, et_qq;
    private Spinner getGoodsType;//获取商品类型
    private Dialog progressDialog;//进度条
    private String pictureUrl;//图片的Url

    private boolean networkState;//判断网络状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_buy_activity);

        //进来先判断网络状态
        networkState = NetworkStateUtils.isNetworkConnected(find_buy_activity.this);

        if (networkState == false) {
            Toast.makeText(find_buy_activity.this, "你的网络在开小差哦！", Toast.LENGTH_SHORT).show();
        }

        //初始化
        init();
        //取消求购
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //spanner
        getGoodsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goodsType = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //调用手机摄像头拍照或选择相册
        image_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加运行时权限
                if (ContextCompat.checkSelfPermission(find_buy_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(find_buy_activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    picsTokenPhoto();
                }
            }
        });

        //点击发布商品
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
                token = pref.getString("token", "");    //将存储在sp中的token拿到
                uname = pref.getString("uname", "");
                userid = pref.getString("userid", "");
                String uuid = UUID.randomUUID().toString();
                Log.i(TAG, "find_buy_activity中的userid--->" + userid);
                Log.i(TAG, "成功获取token==" + token);
                Log.i(TAG, "成功获取uname==" + uname);
                //判断token是否为空
                if (token == null) {
                    Toast.makeText(find_buy_activity.this, "您的登录信息可能已经过期，请重新登录！", Toast.LENGTH_SHORT).show();
                    return;
                }

                //获取EditText上的值
                goodsName = et_goodsName.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                Log.i(TAG, "成功获取goodsName==" + goodsName);
                unit = et_unit.getText().toString().trim();
                qq = et_qq.getText().toString().trim();
                pictureUrl = COSPictureUtils.getPitureUrl();
                Log.i(TAG, "find_buy_activity中pictureUrl--->" + pictureUrl);
                String st_price = et_price.getText().toString();
                String st_quality = et_quality.getText().toString();

                //判断输入框是否为空
                if (goodsName == null || goodsName.equals("") || st_price == null || st_price.equals("") || unit == null || unit.equals("") || st_quality == null || st_quality.equals("") || qq == null || qq.equals("") || pictureUrl == null || pictureUrl.equals("")) {
                    Toast.makeText(find_buy_activity.this, "请输入完整的信息哦！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //数据类型转换
                price = Float.parseFloat(st_price);
                quality = parseFloat(st_quality);
                //判断输入格式是否正确
                //{4,14}表示长度为4到14。//[1-9][0-9]{5,9}匹配6到10位QQ号码,[1-9]表示第一位不能为0
                String regex = "[1-9][0-9]{4,14}";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(qq);
                boolean isMatch = m.matches();
                if (isMatch == false) {
                    Toast.makeText(find_buy_activity.this, "您的QQ格式不对哦！", Toast.LENGTH_SHORT).show();
                    return;
                }

                BuyBO buyBO = new BuyBO();
                buyBO.setPrice(price);
                buyBO.setGoodsName(goodsName);
                buyBO.setToken(token);
                buyBO.setUname(uname);
                buyBO.setUserid(userid);
                buyBO.setUnit(unit);
                buyBO.setQuality(quality);
                buyBO.setQq(qq);
                buyBO.setUid(uuid);
                buyBO.setOpType(opType);
                buyBO.setGoodsID(goodsID);
                buyBO.setGoodsType(goodsType);
                buyBO.setPictureUrl(pictureUrl);

                //设置进度条
                progressDialog = DialogUIUtils.showLoadingDialog(find_buy_activity.this, "正在发布......");
                progressDialog.show();
                //点击物理返回键是否可取消dialog
                progressDialog.setCancelable(true);
                //点击dialog之外 是否可取消
                progressDialog.setCanceledOnTouchOutside(false);
                //发送OkHttp请求
                buyGoods(buyBO);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片的照片显示出来
                    //需要对拍摄的照片进行处理编辑
                    //拍照成功的话，就调用BitmapFactory的decodeStream()方法把图片解析成Bitmap对象，然后显示
                    Log.i(TAG, "onActivityResult TakePhoto : " + imageUri);
                    //Bitmap bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream( imageUri ) );
                    //photo_taken.setImageBitmap( bitmap );
                    //设置照片存储文件及剪切图片
                    File saveFile = ImageUtils.getTempFile();
                    filePath = ImageUtils.getTempFile();
//                    Bitmap bitmap = BitmapFactory.decodeFile(filePath.toString());
//                    //把裁剪后的图片展示出来
//                    image_touxiang.setImageBitmap(bitmap);
//                    //拍完照后，上传图片对象
//                    COSPictureUtils.initCOS(find_buy_activity.this);
                    startImageCrop(saveFile, imageUri);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //选中相册照片显示
                    Log.i(TAG, "onActivityResult: 执行到打开相册了");
                    try {
                        imageUri = data.getData(); //获取系统返回的照片的Uri
                        Log.i(TAG, "onActivityResult: uriImage is " + imageUri);
                        //设置照片存储文件及剪切图片
                        File saveFile = ImageUtils.setTempFile(find_buy_activity.this);
                        filePath = ImageUtils.getTempFile();
                        startImageCrop(saveFile, imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_IMAGE:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: CROP_IMAGE" + "进入了CROP");
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath.toString());
                    //把裁剪后的图片展示出来
                    image_touxiang.setImageBitmap(bitmap);
                    //拍完照后，上传图片对象
                    COSPictureUtils.initCOS(find_buy_activity.this);
                }
                break;
            default:
                break;
        }
    }

    //调用手机摄像头拍照或选择相册
    private void picsTokenPhoto() {
        //具体实现部分
        List<String> stringList = new ArrayList<String>();
        stringList.add("拍照");
        stringList.add("从相册选择");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(find_buy_activity.this, stringList);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //选择调用手机相机拍照或在相册中选择照片的逻辑实现部分
                switch (position) {
                    case 0:
                        //选择调用手机相机
                        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        imageUri = ImageUtils.getImageUri(find_buy_activity.this);
                        Log.i(TAG, "find_sale_activity中的imageUri--->" + imageUri);
                        //putExtra()指定图片的输出地址，填入之前获得的Uri对象
                        photo.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(photo, TAKE_PHOTO);
                        //底部弹框消失
                        optionBottomDialog.dismiss();
                        break;
                    case 1:
                        //选择相册
                        Intent picsIn = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(picsIn, CHOOSE_PHOTO);
                        //底部弹框消失
                        optionBottomDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    picsTokenPhoto();
                }else {
                    Toast.makeText(this,"您拒绝了访问SD卡的权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //发送OkHttp请求
    private void buyGoods(BuyBO buyBO) {
        Gson gson = new Gson();
        String buyJsonStr = gson.toJson(buyBO, BuyBO.class);
        Log.i(TAG, "find_buy_actvity中需要传到后台的buyJsonStr is :" + buyJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/buy";
        HttpUtils.sendOkHttpRequest(url, buyJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(find_buy_activity.this, "目前网络不佳！", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();//进度条消失
                        return;
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());
                    final String buyGoodsJstr = response.body().string();//后台获取的数据
                    Log.d(TAG, "find_buy_activity中buyGoodsJstr--->" + buyGoodsJstr);
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(buyGoodsJstr, UserVO.class);//解析
                    final int flag = userVO.getFlag();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 200) {
                                Toast.makeText(find_buy_activity.this, "求购商品信息已发布成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            if (flag == 40001) {
                                Toast.makeText(find_buy_activity.this, "照片过大，商品发布失败！", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();//进度条消失
                                return;
                            }
                            if (flag == 30001) {
                                Toast.makeText(find_buy_activity.this, "登录信息已过期，请重新登录", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();//进度条消失
                                return;
                            }
                            if (flag == 40003) {
                                Toast.makeText(find_buy_activity.this, "商品数据出错", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();//进度条消失
                                return;
                            }
                        }
                    });
                }
            }
        });
    }

    //图片裁剪
    private void startImageCrop(File saveToFile, Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        Log.i(TAG, "startImageCrop: " + "执行到压缩图片了" + "uri is " + uri);
        intent.setDataAndType(uri, "image/*");//设置Uri及类型
        //uri权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");//
        intent.putExtra("aspectX", 1);//X方向上的比例
        intent.putExtra("aspectY", 1);//Y方向上的比例
        intent.putExtra("outputX", 500);//裁剪区的X方向宽
        intent.putExtra("outputY", 500);//裁剪区的Y方向宽
        intent.putExtra("scale", true);//是否保留比例
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("return-data", false);//是否将数据保留在Bitmap中返回dataParcelable相应的Bitmap数据，防止造成OOM
        //判断文件是否存在
        //File saveToFile = ImageUtils.getTempFile();
        if (!saveToFile.getParentFile().exists()) {
            saveToFile.getParentFile().mkdirs();
        }
        //将剪切后的图片存储到此文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveToFile));
        Log.i(TAG, "startImageCrop: " + "即将跳到剪切图片");
        startActivityForResult(intent, CROP_IMAGE);
    }

    //初始化
    public void init() {
        image_touxiang = (ImageView) findViewById(R.id.image_touxiang);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_goodsName = (EditText) findViewById(R.id.et_goodsName);
        et_unit = (EditText) findViewById(R.id.et_unit);
        et_quality = (EditText) findViewById(R.id.et_quality);
        et_price = (EditText) findViewById(R.id.et_price);
        et_qq = (EditText) findViewById(R.id.et_qq);
        getGoodsType = (Spinner) findViewById(R.id.goods_Type);
    }

    //防止Window Leaked窗体泄漏
    //所以在关闭(finish)某个Activity前，要确保附属在上面的Dialog或PopupWindow已经关闭(dismiss)了。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
