package com.ghl.wuhan.secondhand.DO;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/3/26 18:38
 * 修改人：Liting
 * 修改时间：2019/3/26 18:38
 * 修改备注：
 * 版本：
 */

public class SaleVO {
    private String token;//token
    private String opType;//操作类型（发布，维护等）

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    @Override
    public String toString() {
        return "SaleVO{" +
                "token='" + token + '\'' +
                ", opType='" + opType + '\'' +
                '}';
    }
}
