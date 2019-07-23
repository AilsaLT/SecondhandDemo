package com.ghl.wuhan.secondhand.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * 项目名称：com.ghl.wuhan.secondhand.util
 * 类描述：判断网络状态
 * 创建人：Liting
 * 创建时间：2019/7/23 14:26
 * 修改人：Liting
 * 修改时间：2019/7/23 14:26
 * 修改备注：
 * 版本：
 */

public class NetworkStateUtils {

    /**
     * 判断网络状态
     * */
    public static boolean isNetworkConnected(Context context){
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo()!=null){
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }



}
