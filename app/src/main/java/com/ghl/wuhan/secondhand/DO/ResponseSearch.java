package com.ghl.wuhan.secondhand.DO;

import java.util.List;

/**
 * 项目名称：com.ghl.wuhan.secondhand.home_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/4/5 19:11
 * 修改人：Liting
 * 修改时间：2019/4/5 19:11
 * 修改备注：
 * 版本：
 */

public class ResponseSearch {
    List<User> userVO;
    private int flag;
    private String message;
    private String token;

    public List<User> getUserVO() {
        return userVO;
    }

    public void setUserVO(List<User> userVO) {
        this.userVO = userVO;
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

    @Override
    public String toString() {
        return "ResponseSearch{" +
                "userVO=" + userVO +
                ", flag=" + flag +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
