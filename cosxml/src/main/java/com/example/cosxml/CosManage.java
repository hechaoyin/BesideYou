package com.example.cosxml;

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
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import static com.example.cosxml.Constant.appid;
import static com.example.cosxml.Constant.bucket;
import static com.example.cosxml.Constant.keyDuration;
import static com.example.cosxml.Constant.region;
import static com.example.cosxml.Constant.secretId;
import static com.example.cosxml.Constant.secretKey;

/**
 * @ClassName: com.example.cosxml
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/28 15:49
 */

public class CosManage {
    private static CosManage instance;

    private CosXmlService cosXmlService;

    private CosManage() {
    }

    public static CosManage getInstance(Context context) {
        if (instance == null) {
            synchronized (CosManage.class) {
                if (instance == null) {
                    instance = new CosManage();
                    instance.init(context);
                }
            }
        }
        return instance;
    }

    private void init(Context context) {
        context = context.getApplicationContext();
        //创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();
        //创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
        ShortTimeCredentialProvider localCredentialProvider = new ShortTimeCredentialProvider(secretId, secretKey, keyDuration);
        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        cosXmlService = new CosXmlService(context, serviceConfig, localCredentialProvider);
    }

    /**
     * @param srcPath          远端路径，即存储到 COS 上的绝对路径  "/test.txt";
     * @param cosPath          本地文件的绝对路径 Environment.getExternalStorageDirectory().getPath() + "/test.txt"
     * @param progressListener 进度回调
     */
    public void upload(String srcPath, String cosPath, final ProgressListener progressListener) {
//        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
//        putObjectRequest.setSign(signDuration,null,null); //若不调用，则默认使用sdk中sign duration（60s）

        /*设置进度显示
          实现 CosXmlProgressListener.onProgress(long progress, long max)方法，
          progress 已上传的大小， max 表示文件的总大小
        */
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                float result = (float) (progress * 100.0 / max);
                // TODO : 上传进度result
                if (progressListener != null) {
                    progressListener.onProgress((int) result);
                }
            }
        });
        //使用同步方法上传
        /*
        try {
            PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
            Log.w("TEST", "success: " + putObjectResult.accessUrl);
        } catch (CosXmlClientException e) {
            //抛出异常
            Log.w("TEST", "CosXmlClientException =" + e.toString());
        } catch (CosXmlServiceException e) {
            //抛出异常
            Log.w("TEST", "CosXmlServiceException =" + e.toString());
        }
        */
        //使用异步回调上传：sdk 为对象存储各项服务提供异步回调操作方法

        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.w("TEST", "success =" + result.accessUrl);
                Log.w("TEST", "success =" + result.httpMessage);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {

                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                Log.w("TEST", errorMsg);
            }
        });
    }
    public void upload(String srcPath, String cosPath, final ResultListener resultListener) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.w("TEST", "success =" + result.accessUrl);
                resultListener.onSuccess("http://"+ result.accessUrl);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {

                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                Log.w("TEST", errorMsg);
                resultListener.onFail(errorMsg);
            }
        });
    }
}
