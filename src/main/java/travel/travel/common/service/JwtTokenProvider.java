package travel.travel.common.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

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

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshMills);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public ResponseCookie generateAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .domain("planzupzup.co.kr")
                .maxAge(60 * 30)
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .domain("planzupzup.co.kr")
                .maxAge(60 * 60 * 24 * 7)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
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
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
