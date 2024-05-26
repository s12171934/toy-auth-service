package com.solo.toyauthservice.controller;

import com.solo.toyauthservice.entity.UserEntity;
import com.solo.toyauthservice.service.RefreshService;
import jakarta.servlet.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RefreshController {

    private final RefreshService refreshService;

    public RefreshController(RefreshService refreshService) {

        this.refreshService = refreshService;
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        return refreshService.reissueToken(request, response);
    }

    //gateway를 통한 테스트
    @GetMapping("/auth/test")
    public String test(HttpServletRequest request, @RequestBody UserEntity userEntity){
        System.out.println(request.getHeader("passport"));
        System.out.println(userEntity.getRole());

        return "test";
    }
}
