package com.appsdeveloperblog.app.ws.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // ??
import com.appsdeveloperblog.app.ws.service.UserService;

// special class, containing some configuration
// therefore needs annotation @EnableWebSecurity
// and class to be extended with WebSecurityConfigurerAdapter
// (comes from Spring package, providing ready to use Spring security classes)
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    //    constructor for web security class
    // custom implementation of UserService...
    // ..which extends the interface UserDetailsService from Spring security package
    // and we're using it with WebSecurity class from Spring security package as well
    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // comes from springframework security interface
    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // override couple of methods.. because we're extending WebSecurityConfigurerAdapter
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // ******
        // TEMPORARY
        // ******
        // This prevents the application from creating cookies. Used for testing purposes only.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // needed to configure some of the webservice entry points in our web application as public, and some as protected

        // making the directory '/users' public
        // this directory is saved in the (own created) constant 'SIGN_UP_URL' in the class 'SecurityConstants'
        // This is NOT Spring stuff but self made
        // ************
        // FILTER CHAIN
        //**************
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
                .permitAll()
                .antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL)
                .permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL)
                .permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL)
                .permitAll()
                .anyRequest().authenticated().and()
                .addFilter(getAuthenticationfilter())
                .addFilter(new AuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // prevent authorization header to be cached, by making the session stateless
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // encrypting user password, coming from springframework.security.crypto
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    public AuthenticationFilter getAuthenticationfilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
        filter.setFilterProcessesUrl("/users/login");
        return filter;
    }
}