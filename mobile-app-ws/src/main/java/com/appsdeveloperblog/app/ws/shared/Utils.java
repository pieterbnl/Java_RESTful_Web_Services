package com.appsdeveloperblog.app.ws.shared;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Component
public class Utils {

    public String generateUserID() {
        return UUID.randomUUID().toString();
    }

    public String generateAddressID() {
        return UUID.randomUUID().toString();
    }
//
//    private final Random RANDOM = new SecureRandom();
//    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//
//    public String generateUserID(int length) {
//        // return generateRandomString(length);
//        return UUID.randomUUID().toString();
//    }
//
//    private String generateRandomString(int length) {
//        StringBuilder returnValue = new StringBuilder(length);
//
//        for (int i = 0; i < length; i++) {
//            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
//        }
//        return new String(returnValue);
//    }
}