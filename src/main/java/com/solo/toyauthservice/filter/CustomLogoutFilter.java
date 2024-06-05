package com.solo.toyauthservice.filter;

import com.solo.toyauthservice.repository.RefreshRepository;
import com.solo.toyauthservice.service.RefreshService;
import com.solo.toyauthservice.util.CookieUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {


    private final RefreshRepository refreshRepository;
    private final RefreshService refreshService;
    private final CookieUtil cookieUtil;

    public CustomLogoutFilter(RefreshRepository refreshRepository,
                              RefreshService refreshService,
                              CookieUtil cookieUtil
    ) {

        this.refreshRepository = refreshRepository;
        this.refreshService = refreshService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //HTTP요청과 관련된 메서드를 사용하기 위해 형변환한 오버로드 메서드 사용
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    //Overload
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //path and method 확인
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        if(!requestURI.startsWith("/auth/logout") || !requestMethod.equals("POST")) {

            chain.doFilter(request, response);
            return;
        }

        //refresh token 확인
        String refresh = refreshService.getRefresh(request);
        String checkRefresh = refreshService.checkRefresh(refresh);
        if (checkRefresh != null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃
        //refresh token redis에서 제거
        refreshRepository.deleteById(refresh);

        //refresh token cookie 제거
        response.addCookie(cookieUtil.deleteCookie("refresh"));

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
