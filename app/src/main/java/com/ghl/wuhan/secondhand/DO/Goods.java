package com.ghl.wuhan.secondhand.DO;

import java.util.Arrays;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/3/31 21:33
 * 修改人：Liting
 * 修改时间：2019/3/31 21:33
 * 修改备注：
 * 版本：
 */

/*商品列表类*/

//解析goodsList
public class Goods {
    private String goodsID;  //ID
    private String goodsType; //商品所属类
    private String goodsName; //商品名
    private float price;       // 价格
    private String unit;      //单位
    private float quality;   //数量
    private String userid;   //发布人ID
    private byte[] goodsImg; //商品图片
    private byte[] img;

    private String goodstype; //商品所属类
    private String goodsname; //商品名


    private String token;    //token
    private String goodsTypeName; //所属类名（可选字段）
    private int opType;     //操作类型（发布，维护等）
    private int sex;

//    private int currentPages;//当前页
//    private int datums;//数据条数

    public int page;//页数
    protected int checkType;//查询方式 1---刷新  2---加载
    public int pageSize;//数据条数

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public byte[] getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(byte[] goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
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



    public String getGoodstype() {
        return goodstype;
    }

    public void setGoodstype(String goodstype) {
        this.goodstype = goodstype;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }


}
