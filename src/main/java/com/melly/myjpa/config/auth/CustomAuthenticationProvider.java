package com.melly.myjpa.config.auth;

import com.melly.myjpa.config.exception.AccountDeletedException;
import com.melly.myjpa.config.exception.CustomDisabledException;
import com.melly.myjpa.domain.StatusType;
import com.melly.myjpa.domain.UserEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();  // 로그인 아이디
        String enteredPassword = (String) authentication.getCredentials();  // 입력된 비밀번호

        // 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserEntity user = ((PrincipalDetails) userDetails).getUser();  // UserEntity 객체 가져오기

        // 1. 비밀번호 검증
        if (!passwordEncoder.matches(enteredPassword, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 계정 상태 검증 (비밀번호가 맞을 경우만 수행)
        if (StatusType.DELETED.equals(user.getStatusType())) {
            throw new AccountDeletedException("탈퇴된 계정입니다. 관리자에게 문의하십시오");
        }

        if (StatusType.INACTIVE.equals(user.getStatusType())) {
            throw new CustomDisabledException("이 계정은 비활성화 상태입니다. 관리자에게 문의하십시오");
        }

        // PrincipalDetails를 생성하여 반환
        PrincipalDetails principalDetails = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(principalDetails, enteredPassword, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
