package com.study.train.common.controller;


import cn.hutool.core.util.StrUtil;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public CommonResp exceptionHandler(Exception e) throws Exception {
        LOGGER.info("seata全局事务ID: {}", RootContext.getXID());
         // 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
         if (StrUtil.isNotBlank(RootContext.getXID())) {
             throw e;
         }
        CommonResp<Object> resp = new CommonResp<>();
        LOGGER.error("系统异常:" + e);
        resp.setSuccess(false);
        resp.setMessage(e.getMessage());
        return resp;
    }


    @ExceptionHandler(BusinessException.class)
    public CommonResp exceptionHandler(BusinessException e) {
        CommonResp<Object> resp = new CommonResp<>();
        LOGGER.error("业务异常:{}", e.getBusinessExceptionEnum().getDesc());
        resp.setSuccess(false);
        resp.setMessage(e.getBusinessExceptionEnum().getDesc());
        return resp;
    }

    /**
     * validation 校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public CommonResp exceptionHandler(BindException e) {
        CommonResp<Object> resp = new CommonResp<>();
        // 这里返回所有的错误信息，可根据实际情况是否打印全部错误信息
        // 拼接错误
        StringBuilder detailMessage = new StringBuilder();
        for (ObjectError objectError : e.getAllErrors()) {
            // 使用 ; 分隔多个错误
            if (detailMessage.length() > 0) {
                detailMessage.append(";");
            }
            // 拼接内容到其中
            detailMessage.append(objectError.getDefaultMessage());
        }

        LOGGER.error("校验异常:{}", detailMessage);
        resp.setSuccess(false);
        resp.setMessage(detailMessage.toString());
        return resp;
    }
}
