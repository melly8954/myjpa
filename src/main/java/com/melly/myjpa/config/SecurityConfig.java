package com.melly.myjpa.config;

import com.melly.myjpa.config.auth.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity  // Spring Security 필터가 Spring의 필터 체인에 등록되고, 애플리케이션의 보안 구성이 가능해진다. / Spring Security의 웹 보안을 활성화
@EnableMethodSecurity(securedEnabled = true)   // @Secured 어노테이션 활성화 ( 구 EnableGlobalMethodSecurity )
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()) // 모든 사용자가 접근할 수 있도록 허용
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("loginId")   // Spring Security 의 formLogin() 설정에서 기본적으로 username 파라미터 이름을 사용하므로 변경
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(customAuthenticationSuccessHandler)  // 로그인 성공 후 핸들러 설정
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")  // 로그아웃 URL
                        .logoutSuccessUrl("/")  // 로그아웃 후 리다이렉트 URL
                        .invalidateHttpSession(true)  // 세션 무효화
                        .clearAuthentication(true)  // 인증 정보 지우기
                        .permitAll());  // 로그아웃 URL은 모두 허용


        return http.build();

    }
}
