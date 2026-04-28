package com.remitly.stockmarket.filter;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleRateLimiter {

    private static final int MAX_REQUESTS = 100;
    private static final long TIME_WINDOW_MS = 60_000;

    private final Map<String, UserRequestInfo> requests = new ConcurrentHashMap<>();

    public boolean tryConsume(String clientId) {
        UserRequestInfo info = requests.computeIfAbsent(clientId, k -> new UserRequestInfo());
        synchronized (info) {
            long now = Instant.now().toEpochMilli();
            if (now - info.windowStart > TIME_WINDOW_MS) {
                info.windowStart = now;
                info.count = 1;
                return true;
            }
            if (info.count < MAX_REQUESTS) {
                info.count++;
                return true;
            }
            return false;
        }
    }

    private static class UserRequestInfo {
        long windowStart = Instant.now().toEpochMilli();
        int count = 0;
    }
}