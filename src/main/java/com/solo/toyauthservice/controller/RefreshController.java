package com.solo.toyauthservice.controller;

import com.solo.toyauthservice.service.RefreshService;
import jakarta.servlet.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RefreshController {

    private final RefreshService refreshService;

    public RefreshController(RefreshService refreshService) {

        this.refreshService = refreshService;
    }

    //access token 만료시 refresh token 을 보내 새로운 access token과 refresh token 생성
    //refresh token은 1회용임
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        return refreshService.reissueToken(request, response);
    }
}
