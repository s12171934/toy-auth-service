package com.solo.toyauthservice.controller;

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

    @GetMapping("/auth/test")
    public String test(){
        return "test";
    }
}
