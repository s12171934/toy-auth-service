package com.solo.toyauthservice.controller;

import com.solo.toyauthservice.service.PassportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassportController {

    private final PassportService passportService;

    public PassportController(PassportService passportService) {

        this.passportService = passportService;
    }

    //json형식으로 header에 삽입
    @GetMapping("/auth/passport")
    public String issuePassport(HttpServletRequest request) throws Exception{

        String accessToken = request.getHeader("access");
        return passportService.createPassport(accessToken);
    }
}
