package travel.travel.common.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private long accessMills;

    @Value("${jwt.refresh-expiration}")
    private long refreshMills;

    public String generateAccessToken(Long memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessMills);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshMills);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie generateAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(accessMills)
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(refreshMills)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT invalid: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    public String extractAccessTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }


    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String memberId = claims.getSubject();

        String roles = claims.get("roles", String.class);
        Collection<GrantedAuthority> authorities = (roles != null)
                ? Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
    }


    private Claims parseClaims(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject());

        } catch (ExpiredJwtException e) {
            return Long.parseLong(e.getClaims().getSubject());
        }
    }

}
