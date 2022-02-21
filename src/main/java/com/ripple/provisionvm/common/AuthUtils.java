package com.ripple.provisionvm.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class AuthUtils {
    private final int tokenExpirationTime = 30 * 60 * 1000;

    private final String tokenKey = "D46AC2DC2853A5845D42FC352B1C7E169E1A09EFD16DB89500FBA5BF2097F27A";

    public String generateToken(int userId) {
        Map<String,Object> claims = new HashMap<>();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(SignatureAlgorithm.HS512, tokenKey)
                .compact();
    }

    public void verifyToken(String token) throws JwtException {
        Jwts.parser()
                .setSigningKey(tokenKey)
                .parse(token);
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(tokenKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String updateExpirationDateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(SignatureAlgorithm.HS512, tokenKey)
                .compact();
    }

    public String getUserIdFromToken(String token){
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    public Date getExpirationDateFromToken(String token){
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    public boolean isTokenValid(String token){
        return getExpirationDateFromToken(token).after(new Date());
    }

    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    public int getUserIdFromRequest(WebRequest request){
        String jwtToken = request.getHeader("Authorization").substring(7);
		return Integer.parseInt(getUserIdFromToken(jwtToken));
    }
    
}
