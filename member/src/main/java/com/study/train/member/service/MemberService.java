package com.study.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.util.JwtTokenUtil;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Member;
import com.study.train.member.domain.MemberExample;
import com.study.train.member.mapper.MemberMapper;
import com.study.train.member.req.MemberLoginReq;
import com.study.train.member.req.MemberRegisterReq;
import com.study.train.member.req.MemberSendCodeReq;
import com.study.train.member.resp.MemberLoginResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberMapper memberMapper;


    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }


    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        // 先查询手机号是否已被注册
        Member memberDB = selectByMobile(mobile);
        if (Objects.nonNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);

        return member.getId();
    }

    public String sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);

        if (Objects.isNull(memberDB)) {
            // 如果手机未注册，则插入一条数据
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
            LOGGER.info("手机号不存在，插入一条数据:{}", member);
        } else {
            LOGGER.info("手机号已存在,直接发送验证码");
        }

        // 发送验证码  后续可对接短信平台
        String code = RandomUtil.randomString(4);
        code = "8888"; // 这里先写死，方便测试
        LOGGER.info("生成的短信验证码:{}", code);

        // 保存短信记录，手机号，有效期，是否已用


        return code;
    }


    public MemberLoginResp login(MemberLoginReq req) {
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);
        if (Objects.isNull(memberDB)) {
            // 若为null 说明没有获取验证码,如果未注册,获取验证码时自动注册
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        // 比对验证码 这里为了方便写死了，后续可从redis中查询出来比对
        if (!"8888".equals(req.getCode())) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp resp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
        // 生成token
        Map<String, Object> map = BeanUtil.beanToMap(resp);
        String token = JwtTokenUtil.createToken(map);
        LOGGER.info("生成token:{}", token);
        resp.setToken(token);
        return resp;

    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtil.isEmpty(members)) {
            return null;
        }
        return members.get(0);
    }
}
