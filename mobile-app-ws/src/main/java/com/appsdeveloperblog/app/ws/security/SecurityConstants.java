package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;

// this class holds several security constants that will used as authorization filter
public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer "; // String value, and after this value a token value will be provided
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";

    // note: SpringApplicationContext (own created class) makes it possible to access Beans, created by Spring framework
    // in this case the bean named 'AppProperties'
    // method is marked at static, so that no new instance of securtiy constance has to be created
    public static String getTokenSecret()
    {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
