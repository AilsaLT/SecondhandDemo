package com.ghl.wuhan.secondhand.DO;

import java.util.Arrays;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/3/26 18:37
 * 修改人：Liting
 * 修改时间：2019/3/26 18:37
 * 修改备注：
 * 版本：
 */

public class SaleBO {
    private int opType;
    private String goodsID;//ID
    private String goodsType;//商品所属类
    private String goodsName;//商品名
    private float price;// 价格
    private String unit; //单位
    private float quality;//数量
    private String userid;//发布人ID
    private byte [] goodImg;//商品图片
    private String uname;
    private String uphone;
    private int sex;
    private String qq;
    private String weixin;
    private String token;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public byte[] getGoodImg() {
        return goodImg;
    }

    public void setGoodImg(byte[] goodImg) {
        this.goodImg = goodImg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SaleBO{" +
                "opType=" + opType +
                ", goodsID='" + goodsID + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", quality=" + quality +
                ", userid='" + userid + '\'' +
                ", goodImg=" + Arrays.toString(goodImg) +
                ", uname='" + uname + '\'' +
                ", uphone='" + uphone + '\'' +
                ", sex=" + sex +
                ", qq='" + qq + '\'' +
                ", weixin='" + weixin + '\'' +
                ", token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
