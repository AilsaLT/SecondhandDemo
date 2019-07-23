package com.ghl.wuhan.secondhand.DO;

import java.util.Arrays;

/**
 * 项目名称：com.ghl.wuhan.secondhand.DO
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/7/9 19:35
 * 修改人：Liting
 * 修改时间：2019/7/9 19:35
 * 修改备注：
 * 版本：
 */

public class ResponseBO {
    private int flag;        //成功失败标识
    private String message;  //具体内容
    private String userid;
    private String token; //token
    private String pictureUrl;//图片Url
    private byte [] image;

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


    @Override
    public String toString() {
        return "ResponseBO{" +
                "flag=" + flag +
                ", message='" + message + '\'' +
                ", userid='" + userid + '\'' +
                ", token='" + token + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
