package com.planprostructure.planpro.utils;
import java.time.Instant;
import java.util.Random;

public class ClientMsgIdGenerator { 

    public static String generateClientMsgId() {
        long timestamp = Instant.now().toEpochMilli();
        String randomSuffix = generateRandomString(8);
        return "msg_" + timestamp + "_" + randomSuffix;
    }
    
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}