package com.study.train.common.exception;

public enum BusinessExceptionEnum {


    MEMBER_MOBILE_EXIST("手机号已被注册");


    private String desc;


    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
