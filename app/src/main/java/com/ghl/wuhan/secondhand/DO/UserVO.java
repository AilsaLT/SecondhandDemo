package com.ghl.wuhan.secondhand.DO;

/**
 * 项目名称：com.ghl.wuhan.secondhand.me_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/3/24 21:25
 * 修改人：Liting
 * 修改时间：2019/3/24 21:25
 * 修改备注：
 * 版本：
 */

public class UserVO {
    private int flag;//标志符
    private int collectFlag;//2--已经收藏

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getCollectFlag() {
        return collectFlag;
    }

    public void setCollectFlag(int collectFlag) {
        this.collectFlag = collectFlag;
    }
}
