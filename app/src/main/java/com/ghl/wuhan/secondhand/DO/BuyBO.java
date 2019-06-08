package com.ghl.wuhan.secondhand.DO;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/3/31 21:34
 * 修改人：Liting
 * 修改时间：2019/3/31 21:34
 * 修改备注：
 * 版本：
 */

public class BuyBO {
    private int flag;
    private String goodsName;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    @Override
    public String toString() {
        return "BuyBO{" +
                "flag=" + flag +
                ", goodsName='" + goodsName + '\'' +
                '}';
    }
}
