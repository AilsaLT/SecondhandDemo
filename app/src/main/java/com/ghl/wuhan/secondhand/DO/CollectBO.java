package com.ghl.wuhan.secondhand.DO;

/**
 * 项目名称：com.ghl.wuhan.secondhand.DO
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/7/22 15:01
 * 修改人：Liting
 * 修改时间：2019/7/22 15:01
 * 修改备注：
 * 版本：
 */

public class CollectBO {
   private int collectFlag;//是否收藏标志  0---不收藏，1---收藏
   private String token;
   private String userid;
    private String goodsID;

    public int getCollectFlag() {
        return collectFlag;
    }

    public void setCollectFlag(int collectFlag) {
        this.collectFlag = collectFlag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }

    @Override
    public String toString() {
        return "CollectBO{" +
                "collectFlag=" + collectFlag +
                ", token='" + token + '\'' +
                ", userid='" + userid + '\'' +
                ", goodsID='" + goodsID + '\'' +
                '}';
    }
}
