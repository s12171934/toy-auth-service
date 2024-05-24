package com.solo.toyauthservice.service;

import com.solo.toyauthservice.entity.RefreshEntity;
import com.solo.toyauthservice.repository.RefreshRepository;
import com.solo.toyauthservice.util.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class RefreshService {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshRepository refreshRepository;

    public RefreshService(JWTUtil jwtUtil,
                          CookieUtil cookieUtil,
                          RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refresh = getRefresh(request);

        String checkRefresh = checkRefresh(refresh);
        if (checkRefresh != null) return new ResponseEntity<>(checkRefresh, HttpStatus.BAD_REQUEST);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //access token 유효기간 10분, refresh token 유효기간 1일
        String newAccessToken = jwtUtil.createJwt("access", username, role, 10 * 60 * 1000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        addRefreshEntity(username, newRefreshToken);

        response.setHeader("access", newAccessToken);
        response.addCookie(cookieUtil.createCookie("refresh", newRefreshToken));

        return new ResponseEntity<>("refresh token reissue", HttpStatus.OK);
    }

    //redis에 refresh token 저장
    public void addRefreshEntity(String username, String refresh) {

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);

        refreshRepository.save(refreshEntity);
    }

    public String checkRefresh(String refresh) {

        //refresh 쿠키가 없음
        if (refresh == null) return "refresh token not found";

        //key = refresh인 쿠키의 token이 refresh token인지 검사
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) return "invalid refresh token";

        //refesh token이 redis에 저장되어 있는지 검사
        boolean isExist = refreshRepository.existsById(refresh);
        if (!isExist) return "invalid refresh token";

        //refresh token 유효기간 검사
        try {
            jwtUtil.isExpired(refresh);
        }
        catch (ExpiredJwtException e) {
            return "refresh token expired";
        }

        return null;
    }

    //http request cookie 중 key = refresh 찾기
    public String getRefresh(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) return cookie.getValue();
        }

        return null;
    }

}
