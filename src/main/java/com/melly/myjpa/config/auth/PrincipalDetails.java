package com.melly.myjpa.config.auth;


import com.melly.myjpa.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// Spring Security 에서 인증된 사용자의 정보는 Authentication 객체 안에 저장
// Authentication 객체 안에는 UserDetails 객체가 포함
// UserDetails 는 실제로 사용자 정보를 담고 있는 인터페이스
// UserDetails 인터페이스를 구현한 커스텀 클래스, Spring Security 에서 사용자 인증 및 권한 관리에 사용되는 클래스
public class PrincipalDetails implements UserDetails {

    private UserEntity user;

    public PrincipalDetails(UserEntity user) {
        this.user = user;
    }

    // 사용자의 로그인 ID를 반환
    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    // 사용자의 비밀번호를 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자의 권한을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
        collet.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().getRoleName();
            }
        });
        return collet;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;    // 계정 만료 여부, true는 만료되지 않았음을 의미
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;     // 계정 잠금 여부, true는 잠금되지 않았음을 의미
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    // 자격 증명(비밀번호) 만료 여부, true는 만료되지 않았음을 의미
    }

    @Override
    public boolean isEnabled() {
        // false 는?
        // 1년동안 회원이 로그인을 안하면 휴먼계정으로
        // 현재 시간 - 계정 로그인 시간 >> 1년을 초과 시  return false;

        return true;    // 계정 활성화 여부, true는 활성화된 계정
    }
}