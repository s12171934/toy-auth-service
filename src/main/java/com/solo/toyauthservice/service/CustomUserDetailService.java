package com.solo.toyauthservice.service;

import com.solo.toyauthservice.dto.CustomUserDetails;
import com.solo.toyauthservice.entity.UserEntity;
import com.solo.toyauthservice.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

//jwt를 위한 CustomUserDetails을 사용하기 위해 Custom
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username);

        if(userEntity == null) return null;

        return new CustomUserDetails(userEntity);
    }
}
