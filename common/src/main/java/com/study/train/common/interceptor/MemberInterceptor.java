package com.study.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.MemberLoginResp;
import com.study.train.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("MemberInterceptor开始");
        //获取header的token参数
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LOG.info("获取会员登录token：{}", token);
            boolean tokenExpired = JwtTokenUtil.isTokenExpired(token);
            if (!tokenExpired){
                Claims tokenClaim = JwtTokenUtil.getTokenClaim(token);
                String jsonStr = JSONUtil.toJsonStr(tokenClaim);
                LOG.info("当前登录会员：{}", jsonStr);
                MemberLoginResp member = JSONUtil.toBean(jsonStr, MemberLoginResp.class);
                // 设置用户登录信息上下文
                LoginMemberContext.setMember(member);
            }

        }
        LOG.info("MemberInterceptor结束");
        return true;
    }


}
