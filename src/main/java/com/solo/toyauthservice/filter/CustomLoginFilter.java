package com.solo.toyauthservice.filter;

import com.solo.toyauthservice.service.RefreshService;
import com.solo.toyauthservice.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RefreshService refreshService;
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public CustomLoginFilter(AuthenticationManager authenticationManager,
                             RefreshService refreshService,
                             JWTUtil jwtUtil,
                             CookieUtil cookieUtil
    ) {

        this.authenticationManager = authenticationManager;
        this.refreshService = refreshService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //요청에서 username password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //로그인 검증을 위한 토큰생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //토큰을 검증을 위해 manager로 전송
        return authenticationManager.authenticate(authenticationToken);
    }

    //로그인 성공 시 Jwt 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String username = authResult.getPrincipal().toString();

        //role 획득 과정
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority grantedAuthority = iterator.next();
        String role = grantedAuthority.getAuthority();

        //access token 유효기간 10분, refresh token 유효기간 1일
        String accessToken = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        refreshService.addRefreshEntity(username, role);

        response.addHeader("access", accessToken);
        response.addCookie(cookieUtil.createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
