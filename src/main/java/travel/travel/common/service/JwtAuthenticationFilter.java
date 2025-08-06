package travel.travel.common.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String accessToken = jwtTokenProvider.extractAccessTokenFromCookie(request);
        String refreshToken = jwtTokenProvider.extractRefreshTokenFromCookie(request);

        boolean accessTokenValid = StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken);
        boolean refreshTokenValid = StringUtils.hasText(refreshToken) && jwtTokenProvider.validateToken(refreshToken);

        if (accessTokenValid) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
        } else if (refreshTokenValid) {
            Long memberId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            Member member = memberRepository.findById(memberId).orElse(null);

            if (member != null && refreshToken.equals(member.getRefreshToken())) {
                String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
                ResponseCookie newAccessCookie = jwtTokenProvider.generateAccessTokenCookie(newAccessToken);
                response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

                log.info("Access Token 재발급 완료. 사용자 ID = {}", memberId);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access Token reissued. Please retry.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
