package com.realthon.etf.global.config;

import com.realthon.etf.auth.jwt.JwtFilter;
import com.realthon.etf.auth.jwt.JwtUtil;
import com.realthon.etf.auth.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public LoginFilter loginFilter(AuthenticationManager authenticationManager) {
        LoginFilter filter = new LoginFilter(authenticationManager, jwtUtil);
        filter.setFilterProcessesUrl("/auth/login");
        filter.setUsernameParameter("loginId");
        filter.setPasswordParameter("password");
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/auth/login", "/auth/refresh", "/auth/signup/**").permitAll()
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/auth/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class); // ← 여기서 체이닝 종료

        http.logout(logout -> logout
                .logoutUrl("/auth/logout")
                .addLogoutHandler(logoutHandler())
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setHeader("Authorization", "");
                    res.setHeader("X-Refresh-Token", "");
                    res.addHeader("Set-Cookie", "AccessToken=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
                    res.addHeader("Set-Cookie", "RefreshToken=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
                    res.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
                })
        );

        return http.build();
    }



    @Bean
    public LogoutHandler logoutHandler() {
        // 필요 시 여기서 refresh 토큰 블랙리스트/폐기 로직 추가
        return new SecurityContextLogoutHandler();
    }


}
