package com.example.lamlaisecurity.config.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.expired}")
    private Long EXPIRED;
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            logger.error("Token hết hạn {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Định dạng Token không chính xác {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token không hợp lệ {}", ex.getMessage());
        } catch (SignatureException ex) {
            logger.error("Mã xác nhận Token không hợp lệ {}", ex.getMessage());
        }

        return false;
    }

    public String getUsernameByToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
