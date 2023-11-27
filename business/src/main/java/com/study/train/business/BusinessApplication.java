package com.study.train.business;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.study.train.business.mapper")
@ComponentScan("com.study.train")
public class BusinessApplication{


    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BusinessApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String port = environment.getProperty("server.port");
        LOGGER.info("项目启动成功，port:{}",port);
    }


}


