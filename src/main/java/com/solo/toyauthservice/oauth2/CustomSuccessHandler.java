package com.solo.toyauthservice.oauth2;

import com.solo.toyauthservice.dto.CustomOAuth2User;
import com.solo.toyauthservice.service.RefreshService;
import com.solo.toyauthservice.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${front-end.url}")
    private String viewUrl;

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshService refreshService;

    public CustomSuccessHandler(JWTUtil jwtUtil, CookieUtil cookieUtil, RefreshService refreshService) {

        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.refreshService = refreshService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //access token 유효기간 10분, refresh token 유효기간 1일
        String accessToken = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String refreshToken = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        refreshService.addRefreshEntity(username, refreshToken);

        response.addHeader("access", accessToken);
        response.addCookie(cookieUtil.createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        response.sendRedirect(viewUrl);
    }
}
