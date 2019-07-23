package com.ghl.wuhan.secondhand.DO;

import java.util.List;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/4/2 19:35
 * 修改人：Liting
 * 修改时间：2019/4/2 19:35
 * 修改备注：
 * 版本：
 */

/*商品类*/

//解析查询商品中的复杂Json串
public class ResponseBuy {
    private int flag;
    private String message;
    private String token;
    private String uname;

    private String uphone;
    private int sex;
    private String qq;
    private String weixin;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    private int flagType;//1--摊位，2--求购
    private List<Goods> goodsList;



    private int opType;

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getFlagType() {
        return flagType;
    }

    public void setFlagType(int flagType) {
        this.flagType = flagType;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodList) {
        this.goodsList = goodList;
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

    @Override
    public String toString() {
        return "ResponseBuy{" +
                "flag=" + flag +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", uname='" + uname + '\'' +
                ", uphone='" + uphone + '\'' +
                ", sex=" + sex +
                ", qq='" + qq + '\'' +
                ", weixin='" + weixin + '\'' +
                ", flagType=" + flagType +
                ", goodsList=" + goodsList +
                ", opType=" + opType +
                '}';
    }
}
