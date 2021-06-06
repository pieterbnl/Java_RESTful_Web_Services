package com.appsdeveloperblog.app.ws.shared;

import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
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


    // Note that Claims & Jwts both come from JSON web token package
    public static boolean hasTokenExpired(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.getTokenSecret()) // encrypt with own security constant class
                .parseClaimsJws(token).getBody();

        Date tokenExpirationDate = claims.getExpiration(); // get expiration date of token
        Date todayDate = new Date(); // set today's date as date object

        return tokenExpirationDate.before(todayDate); // tests if expiration date of token is before current date
    }

    public String generateEmailVerificationToken(String userId) {

        // User JSON webtoken library to build new token
        // Expiration time of token is set by adding 10 days (EXPIRATION_TIME) to current time
        // Token is signed with Jwts HS512 and own security constant class
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }

    public String generatePasswordResetToken(String userId)
    {
        // User JSON webtoken library to build new token
        // Expiration time of token is set by adding 1 hour (EXPIRATION_TIME) to current time - considering it's a password reset
        // Token is signed with Jwts HS512 and own security constant class
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }
}