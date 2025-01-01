package com.melly.myjpa.config.auth;

import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// Spring Security 의 formLogin() 설정에서 .loginProcessingUrl()을 지정하면
// 이 URL로 POST 요청이 들어올 때 Spring Security 는 해당 요청을 처리하고
// 자동으로 UserDetailsService의 loadUserByUsername() 메서드를 호출하여 사용자를 인증한다.
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByLoginId(loginId);
        if(user == null) {
            throw new UsernameNotFoundException("User not found with loginId: " + loginId);
        }else {
            return new PrincipalDetails(user);
        }
    }

}
