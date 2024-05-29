package com.solo.toyauthservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.toyauthservice.dto.PassportDTO;
import com.solo.toyauthservice.entity.UserEntity;
import com.solo.toyauthservice.util.JWTUtil;
import org.springframework.stereotype.Service;

@Service
public class PassportService {

    private final JWTUtil jwtUtil;

    public PassportService(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    public String createPassport(String accessToken) throws JsonProcessingException {

        PassportDTO passportDTO = new PassportDTO();
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        passportDTO.setUsername(username);
        passportDTO.setRole(role);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(passportDTO);
    }
}
