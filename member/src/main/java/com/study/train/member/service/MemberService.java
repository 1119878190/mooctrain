package com.study.train.member.service;


import cn.hutool.core.collection.CollectionUtil;
import com.study.train.member.domain.Member;
import com.study.train.member.domain.MemberExample;
import com.study.train.member.mapper.MemberMapper;
import com.study.train.member.req.MemberRegisterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;


    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }


    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        // 先查询手机号是否已被注册
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtil.isNotEmpty(members)) {
            throw new RuntimeException("手机号已被注册");
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);

        return member.getId();
    }
}
