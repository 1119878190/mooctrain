package com.study.train.common.controller;


import com.study.train.common.exception.BusinessException;
import com.study.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public CommonResp exceptionHandler(Exception e) {
        CommonResp<Object> resp = new CommonResp<>();
        LOGGER.error("系统异常:" + e);
        resp.setSuccess(false);
        resp.setMessage(e.getMessage());
        return resp;
    }


    @ExceptionHandler(BusinessException.class)
    public CommonResp exceptionHandler(BusinessException e) {
        CommonResp<Object> resp = new CommonResp<>();
        LOGGER.error("业务异常:{}",e.getBusinessExceptionEnum().getDesc());
        resp.setSuccess(false);
        resp.setMessage(e.getBusinessExceptionEnum().getDesc());
        return resp;
    }
}
