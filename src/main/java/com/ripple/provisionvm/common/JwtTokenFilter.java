package com.ripple.provisionvm.common;

import java.io.IOException;

import com.ripple.provisionvm.repositories.AppUserRepository;
import com.ripple.provisionvm.service.AppUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    AuthUtils authUtils;

    @Autowired
    AppUserDetails appUserDetails;

    @Autowired
    AppUserRepository appUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                final String requestTokenHeader = request.getHeader("Authorization");
                String username = null;
                String token = null;
                if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                    token = requestTokenHeader.substring(7);
                } else {
                    log.warn("Unexpected structure of JWT Token");
                }
                try {
                    if(authUtils.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null){
                        String userId = authUtils.getUserIdFromToken(token);
                        username = appUserRepository.findById(Integer.parseInt(userId)).get().getEmailId();
                        if(username == null || username.length() ==0){
                            username = appUserRepository.findById(Integer.parseInt(userId)).get().getMobileNumber();
                        }
                        UserDetails userDetails = appUserDetails.loadUserByUsername(username);
                    
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    log.error("JWT Token has expired");
                }
                filterChain.doFilter(request, response);
            }
}
