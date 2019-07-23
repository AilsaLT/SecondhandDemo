package com.ghl.wuhan.secondhand.util;

import android.content.Context;
import android.util.Log;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

/**
 * 项目名称：com.ghl.wuhan.secondhand.util
 * 类描述：上传图片对象使用
 * 创建人：Liting
 * 创建时间：2019/7/20 15:13
 * 修改人：Liting
 * 修改时间：2019/7/20 15:13
 * 修改备注：
 * 版本：
 */

public class COSPictureUtils {

    private static String pitureUrl;

    public static String getPitureUrl() {
        return pitureUrl;
    }

    public static void setPitureUrl(String pitureUrl) {
        COSPictureUtils.pitureUrl = pitureUrl;
    }

    /**
     *初始化
     * //在执行任何和 COS 服务相关请求之前，都需要先实例化 CosXmlService 对象，具体可分为如下几步：
     //1.初始化配置类 CosXmlServiceConfig。
     //2.初始化授权类 QCloudCredentialProvider。
     //3.初始化 COS 服务类 CosXmlService。
     **/
    public static void initCOS(Context context){

        //1.初始化配置类，创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        String region = "ap-chengdu";//成都
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 https 请求, 默认 http 请求
                .setDebuggable(true)
                .builder();
        //2.初始化授权类
        String secretId = "AKIDrLeroWkLaaGk4dSr6D83FycqQpCMuJVg"; //永久密钥 secretId
        String secretKey = "Jcu58MPL8OblpeIUTDpNhICvbmiQyIPR"; //永久密钥 secretKey
        //初始化 {@link QCloudCredentialProvider} 对象，来给 SDK 提供临时密钥
        QCloudCredentialProvider credentialProvider = new ShortTimeCredentialProvider(secretId,secretKey, 300);
        //3.初始化 CosXmlService 服务类
        //CosXmlService 是 COS 服务类，可用来操作各种 COS 服务，当您实例化配置类和授权类后，您可以很方便的实例化一个 COS 服务类，具体代码如下。
        CosXmlService cosXmlService = new CosXmlService(context, serviceConfig, credentialProvider);
        uploadingObject("bucket-1259670227",cosXmlService);
    }


    //上传图片对象
    public static void uploadingObject(String myBucketName, CosXmlService cosXmlService) {

        // 初始化 TransferConfig
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        Log.d("TAG", "uploadingObject: "+ImageUtils.getTempFile().toString());

        String bucket = myBucketName;
        String cosPath = "secondhand/images/"+ImageUtils.getName(); //即对象到 COS 上的绝对路径, 格式如 cosPath = "text.txt";//存储桶下的路径
        String srcPath = ImageUtils.getTempFile().toString(); // 如 srcPath=Environment.getExternalStorageDirectory().getPath() + "/text.txt";
        String uploadId = null; //若存在初始化分片上传的 UploadId，则赋值对应 uploadId 值用于续传，否则，赋值 null。
        //上传对象
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath, srcPath, uploadId);
        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d("TAG", String.format("progress = %d%%", (int) progress));
            }
        });
        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                //返回的图片Url
                Log.d("TAG", "Success: " + cOSXMLUploadTaskResult.accessUrl);
                setPitureUrl(cOSXMLUploadTaskResult.accessUrl);
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d("TAG", "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));
            }
        });
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d("TAG", "Task state:" + state.name());
            }
        });

    }


}
