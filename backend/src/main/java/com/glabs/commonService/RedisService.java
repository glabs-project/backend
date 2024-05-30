package com.glabs.commonService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    @Value("${redis.cashe.timer.minutes}")
    private Integer redisTimeout;

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveVerificationDetails(String email, String hashedPassword, String verificationCode) {
        VerificationDetails details = new VerificationDetails(hashedPassword, verificationCode);
        redisTemplate.opsForValue().set(email, details, redisTimeout, TimeUnit.MINUTES); // Данные будут храниться в Redis 2 минуты
    }

    public VerificationDetails getVerificationDetails(String email) {
        return (VerificationDetails) redisTemplate.opsForValue().get(email);
    }

    public void removeVerificationDetails(String email) {
        redisTemplate.delete(email);
    }

    public static class VerificationDetails implements Serializable {
        private static final long serialVersionUID = 1L;
        private String hashedPassword;
        private String verificationCode;

        public VerificationDetails(String hashedPassword, String verificationCode) {
            this.hashedPassword = hashedPassword;
            this.verificationCode = verificationCode;
        }

        public String getHashedPassword() {
            return hashedPassword;
        }

        public String getVerificationCode() {
            return verificationCode;
        }
    }
}
