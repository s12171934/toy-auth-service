package com.solo.toyauthservice.config;

import com.solo.toyauthservice.filter.CustomLoginFilter;
import com.solo.toyauthservice.filter.CustomLogoutFilter;
import com.solo.toyauthservice.repository.RefreshRepository;
import com.solo.toyauthservice.service.RefreshService;
import com.solo.toyauthservice.util.CookieUtil;
import com.solo.toyauthservice.util.JWTUtil;
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

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          RefreshRepository refreshRepository,
                          RefreshService refreshService,
                          JWTUtil jwtUtil,
                          CookieUtil cookieUtil
    ) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshRepository = refreshRepository;
        this.refreshService = refreshService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    //authenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    //비밀번호 암호화 메서드
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

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

        //custom filter 등록
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
