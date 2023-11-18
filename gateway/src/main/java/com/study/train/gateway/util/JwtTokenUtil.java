package com.study.train.gateway.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

    public static String secret = "luuu-train";
    public static int expire = 60;
    public static String header;

    /**
     * 生成token
     *
     * @param claim payload
     * @return token
     */
    public static String createToken(Map<String,Object> claim) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS512");
        header.put("typ", "JWT");
        return Jwts.builder()
                .setHeader(header)
                //可以将基本信息放到claims中
                .setClaims(claim)
                //签发时间
                .setIssuedAt(nowDate)
                //超时设置,设置过期的日期
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    /**
     * 获取token中注册信息
     *
     * @param token
     * @return
     */
    public static Claims getTokenClaim(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 验证token是否过期失效
     *
     * @param token
     * @return true:过期  false: 未过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            return getTokenClaim(token).getExpiration().before(new Date());
        }catch (ExpiredJwtException e){
            return true;
        }
    }

}