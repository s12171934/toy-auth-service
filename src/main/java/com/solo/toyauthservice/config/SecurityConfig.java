package com.solo.toyauthservice.config;

import com.solo.toyauthservice.filter.*;
import com.solo.toyauthservice.repository.*;
import com.solo.toyauthservice.oauth2.*;
import com.solo.toyauthservice.service.RefreshService;
import com.solo.toyauthservice.util.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshRepository refreshRepository;
    private final RefreshService refreshService;
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          RefreshRepository refreshRepository,
                          RefreshService refreshService,
                          JWTUtil jwtUtil,
                          CookieUtil cookieUtil,
                          CustomSuccessHandler customSuccessHandler,
                          CustomOAuth2UserService customOAuth2UserService
    ) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshRepository = refreshRepository;
        this.refreshService = refreshService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.customSuccessHandler = customSuccessHandler;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { //비밀번호 암호화 메서드

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http.csrf((auth) -> auth.disable());

        //form login disable
        http.formLogin((auth) -> auth.disable());

        //http basic disable
        http.httpBasic((auth) -> auth.disable());

        //oauth2
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );

        //custom filter 등록 - 로그인 / 로그아웃
        CustomLoginFilter customLoginFilter = new CustomLoginFilter(authenticationManager(authenticationConfiguration), refreshService, jwtUtil, cookieUtil);
        customLoginFilter.setFilterProcessesUrl("/auth/login");
        http.addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class);

        CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(refreshRepository, refreshService, cookieUtil);
        http.addFilterBefore(customLogoutFilter, LogoutFilter.class);

        //session stateless하게 관리 for JWT
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
