package edu.du.myproject1101_1.config;

import edu.du.myproject1101_1.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/forgot-password", "/register", "/assets/**", "/js/**", "/images/**").permitAll() // 로그인, 회원가입,
                // 정적 자원 허용
                .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                .and()
                .formLogin()
                .loginPage("/login") // 커스텀 로그인 페이지
                .usernameParameter("email") // 입력 필드 이름이 'email'이면 이 부분도 맞춰야 함
                .defaultSuccessUrl("/myProject", true) // 로그인 성공 시 리다이렉트 페이지 설정
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // 로그아웃 후 리다이렉트 경로
                .permitAll()
                .and()
                .csrf().disable(); // 필요 시 CSRF 비활성화 (개발 중에만 비활성화 권장)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
