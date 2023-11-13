package com.study.train.member.resp;

public class MemberLoginResp {

    private String id;
    private String mobile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberLoginResp{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
