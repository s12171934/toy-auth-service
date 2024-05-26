package com.solo.toyauthservice.dto;

import com.solo.toyauthservice.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomOAuth2User implements OAuth2User {

    private final UserEntity userEntity;

    public CustomOAuth2User(UserEntity userEntity) {

        this.userEntity = userEntity;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userEntity.getRole();
            }
        });

        return authorities;
    }

    @Override
    public String getName() {

        return userEntity.getName();
    }

    public String getUsername() {

        return userEntity.getUsername();
    }
}
