package com.appsdeveloperblog.app.ws.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appsdeveloperblog.app.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

// Extends UsernamePasswordAuthenticationFilter from Spring framework
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // AuthenticationManager also comes from Spring
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // As part of the AuthenticationManager we need to override a couple of methods
    // This method will be triggered when user login is checked
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            UserLoginRequestModel creds = new ObjectMapper()
                    .readValue(req.getInputStream(),UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method will be triggered if user (email) and password match
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                           HttpServletResponse res,
                                           FilterChain chain,
                                           Authentication auth) throws  IOException, ServletException {

        String userName = ((User) auth.getPrincipal()).getUsername();
        //String tokenSecret = new SecurityConstants().getTokenSecret();

        // Generate JSON webtoken (from jsonwebtoken dependency in our POM)
        // Will be included in header information, and client that receives it, needs to extract and store it
        // For iOS mobile this token will be stored in iOS key chain
        // Everytime the device communicates with our API for secure sources, the key will have to be provided
        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret() )
                .compact();

        // Use Spring application context, to get UserService implementation bean, so that we can get use details
        // Because userServiceImpl implements UserService, we'll need to use UserService as data type
        // When userServiceImpl gets created by Spring, the name of the bean will be the name of the class, but with lower case 'user..' instead of 'User..' !!
        // Then the bean is casted to (UserService)
        // Note: SpringApplicationContext is own create java class (in home directory)
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDTO userDto = userService.getUser(userName);

        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserID",userDto.getUserId());
    }
}