package com.study.train.member.controller;

import com.study.train.common.resp.CommonResp;
import com.study.train.member.req.MemberRegisterReq;
import com.study.train.member.req.MemberSendCodeReq;
import com.study.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public Integer count() {
        return memberService.count();
    }


    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req) {
        return new CommonResp<>(memberService.register(req));
    }

    @PostMapping("/sendCode")
    public CommonResp<String> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        return new CommonResp<>(memberService.sendCode(req));
    }
}
