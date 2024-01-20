package com.study.train.member.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope //@RefreshScope作用：在nacos修改配置，服务不用重启也生效
public class TestController {

    @Value("${pro}")
    private String nacosTest;

    @GetMapping("/test")
    public String test() {
        return "hello world";
    }


    @GetMapping("/nacosTest")
    public String nacosTest() {
        System.out.println(nacosTest);
        return nacosTest;
    }

}
