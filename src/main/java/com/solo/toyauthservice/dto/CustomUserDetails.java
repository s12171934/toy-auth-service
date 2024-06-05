package com.solo.toyauthservice.dto;

import com.solo.toyauthservice.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {

        this.userEntity = userEntity;
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
    public String getPassword() {

        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {

        return userEntity.getUsername();
    }

    //계정 사용가능 조건 표시, true면 해당 검증에서 통과, false가 있으면 사용 불가

    @Override
    public boolean isAccountNonExpired() { //계정의 만료 여부

        return true;
    }

    @Override
    public boolean isAccountNonLocked() { //계정의 잠금 상태

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { //자격 증명의 만료 여부

        return true;
    }

    @Override
    public boolean isEnabled() { //계정 활성화 여부

        return true;
    }
}
