package com.ripple.provisionvm.config;

import com.ripple.provisionvm.common.JwtTokenFilter;
import com.ripple.provisionvm.service.AppUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Order(1)
    @Configuration
    public static class BasicSecurityAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        AppUserDetails appUserDetails;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/account/signup");
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .csrf()
                    .disable().antMatcher("/account/authenticate")
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic().and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(appUserDetails).passwordEncoder(passwordEncoder());

        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();

        }
    }

    @Order(2)
    @Configuration
    public static class JwtSecurityAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        JwtTokenFilter jwtTokenFilter;
        @Autowired
        AppUserDetails appUserDetails;
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/account/signup");
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated().and().addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class).sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(appUserDetails);

        }

    }

}
