package com.realthon.etf.auth.service;

import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        var user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("loginId not found: " + loginId));
        return new CustomUserDetails(user);
    }
}
