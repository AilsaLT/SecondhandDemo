package com.ghl.wuhan.secondhand.find_activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ghl.wuhan.secondhand.DO.SaleBO;
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

import okhttp3.Call;
import okhttp3.Response;

public class find_sale_activity extends AppCompatActivity {

    //属性定义部分
    private ImageView iv_back;
    private String TAG = "TAG";
    //拍照
    private ImageView image_touxiang;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_IMAGE = 3;
    private File filePath;
    //销售商品
    private int opType = 90003;
    private String goodsID;//ID
    private int goodsType;//商品所属类
    private String goodsName;//商品名
    private float price = 0.1f;// 价格
    private String unit; //单位
    private float quality = 1.0f;//数量
    private String userid;//发布人ID
    private String qq;
    private String token;
    private Button btn_submit;
    private EditText et_goodsName, et_price,et_unit,et_quality,et_qq;
    private TextView tv_back;
    //上传图片对象返回的Url
    private static String pictureUrl;
    //获取商品类型
    private Spinner getGoodsType;
    private Dialog progressDialog;//进度条

    private boolean networkState;//网络状态
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
                        File saveFile = ImageUtils.setTempFile(find_sale_activity.this );
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
                    image_touxiang.setImageBitmap( bitmap );
                    //上传图片对象
                    COSPictureUtils.initCOS(find_sale_activity.this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这两句必须放在super.onCreate(savedInstanceState);之后， setContentView(contentView);之前
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        View contentView = LayoutInflater.from(this).inflate(R.layout.me_user_register, null);
        setContentView(R.layout.find_sale_activity);

        //进来先判断网络状态
        networkState = NetworkStateUtils.isNetworkConnected(find_sale_activity.this);
        if(networkState == false){
            Toast.makeText(find_sale_activity.this,"你的网络在开小差哦",Toast.LENGTH_SHORT).show();
      }

        //初始化
        init();

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

        //点击进行拍照
        image_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //具体实现部分
                List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(find_sale_activity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //选择调用手机相机拍照或在相册中选择照片的逻辑实现部分
                        switch (position){
                            case 0:
                                //选择调用手机相机
                                Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                imageUri = ImageUtils.getImageUri(find_sale_activity.this);
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
            }
        });

        //取消发布商品
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //点击发布商品
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //设置进度条
                progressDialog = DialogUIUtils.showLoadingDialog(find_sale_activity.this,"正在发布......");
                progressDialog.show();
                //点击物理返回键是否可取消dialog
                progressDialog.setCancelable(true);
                //点击dialog之外 是否可取消
                progressDialog.setCanceledOnTouchOutside(false);

                SaleBO saleBO = new SaleBO();

                goodsName = et_goodsName.getText().toString().trim();//trim()的作用是去掉字符串左右的空格
                price = Float.parseFloat(et_price.getText().toString());
                Log.i(TAG, "成功获取goodsName==" + goodsName);
                Log.i(TAG, "成功获取price==" + price);
                //将存储在sp中的token拿到
                //获取用户名和token
                SharedPreferences pref = getSharedPreferences("userinfo", MODE_PRIVATE);
                token = pref.getString("token", "");
                Log.i(TAG, "成功获取token==" + token);

                userid = pref.getString("userid", "");
                Log.i(TAG, "userid==" + userid);

                //获取EditText上的商品单位和数量
                unit = et_unit.getText().toString().trim();
                quality = Float.parseFloat(String.valueOf(et_quality.getText()));
                qq = et_qq.getText().toString().trim();
                pictureUrl = COSPictureUtils.getPitureUrl();
                Log.i("TAG","find_sale_activity中pictureUrl--->"+pictureUrl);

                //判断用户输入的信息是否为空
//                if(goodsName == null || price)


                saleBO.setGoodsName(goodsName);
                saleBO.setPrice(price);
                saleBO.setToken(token);
                saleBO.setUserid(userid);
                saleBO.setUnit(unit);
                saleBO.setQuality(quality);
                saleBO.setQq(qq);
                saleBO.setPictureUrl(pictureUrl);

                String uuid = UUID.randomUUID().toString();
                saleBO.setUid(uuid);

                saleBO.setOpType(opType);
                saleBO.setGoodsID(goodsID);
                saleBO.setGoodsType(goodsType);

                //发送OkHttp请求
                sale(saleBO);

            }
        });

    }
/**
 * 发送OkHttp请求
 * */
    private void sale(SaleBO saleBO) {

        Gson gson = new Gson();
        String goodsJsonStr = gson.toJson(saleBO, SaleBO.class);
        Log.i(TAG, "find_sale_activity中goodsJsonStr is :" + goodsJsonStr);
        String url = "http://47.105.183.54:8080/Proj20/sale";

        HttpUtils.sendOkHttpRequest(url,goodsJsonStr, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "获取数据失败了" + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(find_sale_activity.this,"目前网络不佳！",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "获取数据成功了");
                    Log.d(TAG, "response.code()==" + response.code());

                    final String resultJsr = response.body().string();
                    Log.d(TAG, "find_sale_activity中response.body().string()==" + resultJsr);

                    //将response.code()转换成对象
                    UserVO userVO = new UserVO();
                    Gson gson = new Gson();
                    userVO = gson.fromJson(resultJsr, UserVO.class);
                    int flag = userVO.getFlag();


                        if (flag == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(find_sale_activity.this, "商品发布成功！", Toast.LENGTH_SHORT).show();
                                }
                            });


                            finish();
                        }
                        if (flag == 40001) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(find_sale_activity.this, "照片过大，商品发布失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (flag == 4000) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(find_sale_activity.this, "SALE_GOODS_FAILED_PARAMS", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                }
            }
        });

    }

    /**
     *  图片裁剪
     * */
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


    //初始化
    public void init(){
        //初始化
        iv_back = (ImageView) findViewById(R.id.iv_back);
        image_touxiang = (ImageView) findViewById(R.id.image_touxiang);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_goodsName = (EditText) findViewById(R.id.et_goodsName);
        et_price = (EditText) findViewById(R.id.et_price);
        et_qq = (EditText) findViewById(R.id.et_qq);
        et_unit = (EditText) findViewById(R.id.et_unit);
        et_quality = (EditText) findViewById(R.id.et_quality);
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