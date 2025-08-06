package travel.travel.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import travel.travel.common.service.JwtTokenProvider;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("onAuthenticationSuccess");

        OAuth2User oAuth2User =  (OAuth2User)authentication.getPrincipal();
        String kakaoId = String.valueOf(oAuth2User.getAttributes().get("id"));

        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Long memberId = member.getId();
        String accessToken = jwtTokenProvider.generateAccessToken(memberId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

        ResponseCookie accessTokenCookie = jwtTokenProvider.generateAccessTokenCookie(accessToken);
        ResponseCookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        String redirectUrl = UriComponentsBuilder
                .fromUriString("https://localhost:3000")
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

}
