package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;

// this class holds several security constants that will used as authorization filter
public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 360000; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer "; // String value, and after this value a token value will be provided
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";

    // note: SpringApplicationContext (own created class) makes it possible to access Beans, created by Spring framework
    // in this case the bean named 'AppProperties'
    // method is marked at static, so that no new instance of security constance has to be created
    public static String getTokenSecret()
    {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
