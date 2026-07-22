package com.yhs.base.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yhs.base.pojo.vo.BusinessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token工具类
 *
 * @author 05697-LongLiHua
 * @version Id: TokenUtils.java, v 0.1 2021/1/12 16:42  LongLiHua Exp $
 * @Description
 */
@UtilityClass
public class TokenUtil {

    /**
     * 公钥
     */
    public static final String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKbojyJGxna9bfW4GbDvychwIHnELpEAO8r+FozGwmCz\n" +
            "V5E36PJnAS51ZBd5rBvqYytjYhmKmtLZDeWAmwgPg4kCAwEAAQ==";

    /**
     * 私钥
     */
    public static final String PRIVATE_KEY = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEApuiPIkbGdr1t9bgZsO/JyHAgecQu\n" +
            "kQA7yv4WjMbCYLNXkTfo8mcBLnVkF3msG+pjK2NiGYqa0tkN5YCbCA+DiQIDAQABAkEAkHimRF4M\n" +
            "tYTcJB+5pSy5dVSQ17VXNU2Fc/yhWjNE1tudx6Um2LL6TpYSwqO+UiyvKm59uarvNaP34vXPK7Kj\n" +
            "zQIhAOUXb7wRddOqcZ1b0i6NB1Mci5Tmnu8kkoJlgF6wmMYfAiEAuoNV/4jI1mSJNLinzCnH/j/x\n" +
            "uiUc4jviG/cmfZA/8VcCIC/i5T03nRyAfT06S0Xlvsk1p0uZpVExoNpW4y1n1pdbAiA9lTXk/L4L\n" +
            "hACFpt7Im5cMTQK8ipqB3HHo9+7+kI18WwIhALOmu27sxySnUYOOJpKrkG3PSOYBsdpHWWTgkFyS\n" +
            "wPa3";

    //设置过期时间 默认7天
    private static final long EXPIRE_DATE = 30L * 24 * 60 * 60 * 1000;
    //token秘钥
    private static final String TOKEN_SECRET = "KJNfgsadfasdfasd!@#%$#@^@\\";

    /**
     * 生成token值
     *
     * @param username  账号
     * @param password  密码
     * @param userAgent 用户标识
     * @return java.lang.String
     **/
    public static String token(String username, String password, String userAgent) {
        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            if (!StringUtils.isEmpty(userAgent)) {
                header.put("userAgent", userAgent);
            }
            //携带username，password信息，生成签名
            token = JWT.create().withHeader(header).withClaim("username", username).withClaim("password", password).withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return token;
    }

    /**
     * 校验token
     *
     * @param token token内容
     * @return BusinessResponse<com.auth0.jwt.interfaces.DecodedJWT>
     **/
    public static BusinessResponse<DecodedJWT> verifyToken(String token, String userAgent) {
        DecodedJWT verify;
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verify = verifier.verify(token);
            String userAgentToken = verify.getHeaderClaim("userAgent").asString();
            if (!StringUtils.isEmpty(userAgentToken)) {
                if (!StringUtils.equalsIgnoreCase(userAgentToken, userAgent)) {
                    return BusinessResponse.fail(500005, "当前环境与登录环境不一致,请重新登录");
                }
            }
        } catch (JWTDecodeException jwtDecodeException) {
            return BusinessResponse.fail(500001, "用户名无效");
        } catch (SignatureVerificationException signatureVerificationException) {
            return BusinessResponse.fail(500002, "用户名解析错误");
        } catch (TokenExpiredException tokenExpiredException) {
            return BusinessResponse.fail(500003, "登录已过期,请重新登录");
        } catch (Exception ex) {
            return BusinessResponse.fail(500004, "请重新登录");
        }

        return BusinessResponse.ok(verify);
    }

    /**
     * 校验用户信息
     *
     * @param token     token
     * @param userAgent 用户表示
     * @return BusinessResponse
     **/
    public static BusinessResponse getUserInfo(String token, String userAgent) {
        BusinessResponse<DecodedJWT> decodedJWTBusinessResponse = verifyToken(token, userAgent);
        if (!decodedJWTBusinessResponse.success()) {
            return decodedJWTBusinessResponse;
        }
        DecodedJWT decodedJWT = decodedJWTBusinessResponse.getData();
        String username = decodedJWT.getClaim("username").asString();
        return BusinessResponse.ok(username);
    }

    /**
     * 获取用户账号
     *
     * @param request 请求信息
     * @return java.lang.String
     **/
    public static BusinessResponse getUserInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies == null || cookies.length == 0) {
            return null;
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }
        //获取用户标识
        String userAgent = request.getHeader("User-Agent");
        return getUserInfo(token, userAgent);

    }
}
