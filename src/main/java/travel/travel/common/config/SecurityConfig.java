package travel.travel.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;
import travel.travel.common.handler.OAuth2AuthenticationFailureHandler;
import travel.travel.common.handler.OAuth2AuthenticationSuccessHandler;
import travel.travel.common.service.CustomOAuth2UserService;
import travel.travel.common.service.JwtAuthenticationFilter;
import travel.travel.common.service.JwtTokenProvider;
import travel.travel.member.repository.MemberRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberRepository memberRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final String[] whiteList = {
            "/",
            "/api/plan/**",
            "/api/location/**",
            "/api/oauth2/**",
            "/oauth2/**",
            "/favicon.ico",
            "/.well-known/**",
            "/login/**",
            "/api/auth/logout",
            "/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityConfig initialized");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout", "POST"))
                        .logoutSuccessHandler((req, res, auth) -> {
                            ResponseCookie accessDel = ResponseCookie.from("accessToken", "")
                                    .path("/")
                                    .domain("planzupzup.co.kr")
                                    .sameSite("None")
                                    .secure(true)
                                    .httpOnly(true)
                                    .maxAge(0)
                                    .build();
                            ResponseCookie refreshDel = ResponseCookie.from("refreshToken", "")
                                    .path("/")
                                    .domain("planzupzup.co.kr")
                                    .sameSite("None")
                                    .secure(true)
                                    .httpOnly(true)
                                    .maxAge(0)
                                    .build();

                            res.addHeader(HttpHeaders.SET_COOKIE, accessDel.toString());
                            res.addHeader(HttpHeaders.SET_COOKIE, refreshDel.toString());

                            SecurityContextHolder.clearContext();
                            res.setStatus(204);
                            log.info("로그아웃 성공 : \naccess : {} \nrefresh : {}", accessDel, refreshDel);
                        })
                        .deleteCookies("accessToken", "refreshToken")
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                        .userInfoEndpoint(userinfo -> userinfo
                                .userService(customOAuth2UserService))
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, memberRepository);
    }
}
