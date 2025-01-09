package com.melly.myjpa.config.oauth;

import com.melly.myjpa.config.auth.PrincipalDetails;
import com.melly.myjpa.domain.RoleEntity;
import com.melly.myjpa.domain.StatusType;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.repository.RoleRepository;
import com.melly.myjpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 후처리 함수(service) 생성
// 로그인 진행 순서
// 1. 구글 로그인
// 2. 인증 코드 받기
// 3. (권한이 담긴) 액세스 토큰을 받고
// 4. 유저의 프로필 정보를 가져온다.
// 5. 이후 자동 회원가입 / 로그인
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println(userRequest.getClientRegistration()+"\n");   // registrationId는 OAuth2 클라이언트 설정(google,facebook)을 구분하는 식별자 역할
        System.out.println(userRequest.getAccessToken()+"\n");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getClientId(); // Google 을 가리킴
        String providerId = oAuth2User.getAttribute("sub");
        String loginId = provider + "_" + providerId;
        String name = oAuth2User.getAttribute("name");
        String nickname = oAuth2User.getAttribute("given_name");
        String email = oAuth2User.getAttribute("email");
        // 비밀번호 설정 없이 OAuth 로그인만 사용
        String password = "N/A"; // 또는 null로 설정 가능
        String role = "USER";
        StatusType statusType = StatusType.ACTIVE;

        // RoleEntity 객체를 먼저 조회
        Optional<RoleEntity> roleEntity = roleRepository.findByRole(role);  // "USER"에 해당하는 RoleEntity를 조회


        UserEntity user = userRepository.findByLoginId(loginId);
        if (user == null) {
            user = UserEntity.builder()
                    .loginId(loginId)
                    .password(password)
                    .name(name)
                    .nickname(nickname)
                    .email(email)
                    .role(roleEntity.get())  // 이미 조회된 RoleEntity를 할당
                    .provider(provider)
                    .providerId(providerId)
                    .statusType(statusType)
                    .build();
            userRepository.save(user);
        }
        return new PrincipalDetails(user,oAuth2User.getAttributes());
    }
}
