package com.campus.secondhand.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthUtil {

    private static final Map<String, Long> tokenMap = new ConcurrentHashMap<>();

    public static String login(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenMap.put(token, userId);
        return token;
    }

    public static Long getLoginId(String token) {
        return tokenMap.get(token);
    }

    public static void logout(String token) {
        tokenMap.remove(token);
    }

    public static boolean isLogin(String token) {
        return token != null && tokenMap.containsKey(token);
    }
}
