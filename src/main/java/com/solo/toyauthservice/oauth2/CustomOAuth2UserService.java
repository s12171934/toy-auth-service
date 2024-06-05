package com.solo.toyauthservice.oauth2;

import com.solo.toyauthservice.dto.CustomOAuth2User;
import com.solo.toyauthservice.entity.UserEntity;
import com.solo.toyauthservice.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        //어떤 OAUth2 Server와 통신하는 확인 후 OAuth2Respone를 해당 Server객체로 구현
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return  null;
        }

        //OAuth로 로그인 시 username 설정, 이미 로그인한 적이 있는지 없는지 확인
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        UserEntity existUser = userRepository.findByUsername(username);

        //로그인 한 적이 없다면, DB에 해당 회원 저장
        if(existUser == null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setName(oAuth2Response.getEmail());
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);

            return new CustomOAuth2User(userEntity);
        }
        //로그인 한 적이 있다면, 정보 갱신
        else {
            existUser.setEmail(oAuth2Response.getEmail());

            userRepository.save(existUser);

            return new CustomOAuth2User(existUser);
        }
    }
}
