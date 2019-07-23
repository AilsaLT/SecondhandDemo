package com.ghl.wuhan.secondhand;

import android.app.Application;

/**
 * 项目名称：com.ghl.wuhan.secondhand
 * 类描述：设置的一个全局变量，判断当前的fragment的网络请求是否结束，false--不能切换，true--可以切换
 * 创建人：Liting
 * 创建时间：2019/7/23 16:11
 * 修改人：Liting
 * 修改时间：2019/7/23 16:11
 * 修改备注：
 * 版本：
 */

public class MyApplication extends Application {

    private boolean flag;


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
