package com.yinya.shortlink.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ShortCodeGenerator {

    private static final int CODE_LENGTH = 8;
    private AtomicLong sequence = new AtomicLong(System.currentTimeMillis() % 10000);


    /**
     * 基于时间戳和序列号组合生成唯一的短码
     */
    public String generateUniqueCode() {
        long timestamp = Instant.now().getEpochSecond();
        long seq = sequence.incrementAndGet();

        String rawData = timestamp + ":" + seq;

        String hashAndEncode = hashAndEncode(rawData);

        return base62Encode(hashAndEncode).substring(0, CODE_LENGTH);
    }

    /**
     * 对给定的数据进行SHA-256哈希处理，并进行Base64编码
     */
    private String hashAndEncode(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Base64编码的字符串转换为Base62编码的字符串
     */
    private String base62Encode(String base64String) {
        StringBuilder out = new StringBuilder(base64String.length());
        for (byte c : base64String.getBytes()) {
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                out.append((char) c);
            }
        }
        return out.toString();
    }
}