package com.study.train.gateway.config;

import com.study.train.gateway.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 过滤器,用于校验token
 */
@Component
public class LoginMemberFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginMemberFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 排除不需要拦截的请求
        if (path.contains("/admin")
                || path.contains("/hello")
                || path.contains("/member/member/login")
                || path.contains("/member/member/sendCode")) {
            LOGGER.info("不需要登录验证：{}", path);
            return chain.filter(exchange);
        } else {
            LOGGER.info("需要登录验证：{}", path);
        }
        // 获取header的token参数
        String token = exchange.getRequest().getHeaders().getFirst("token");
        LOGGER.info("会员登录验证开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            LOGGER.info("token为空，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 校验token是否有效，包括token是否被改过，是否过期
        try {
            boolean isExpired = JwtTokenUtil.isTokenExpired(token);
            if (isExpired) {
                LOGGER.warn("token过期，请求被拦截");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            } else {
                LOGGER.info("token有效，放行该请求");
                return chain.filter(exchange);
            }
        }catch (Exception e){
            LOGGER.info("token解析失败");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    /**
     * 优先级设置 数字越小 优先级越高
     *
     * @return int
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
