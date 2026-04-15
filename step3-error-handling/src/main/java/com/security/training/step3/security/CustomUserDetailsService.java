package com.security.training.step3.security;

import com.security.training.step3.repository.AuthorityRepository;
import com.security.training.step3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        var authorities = authorityRepository.findByUserId(user.getId()).stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                .toList();

        return CustomUserDetails.builder()
                .user(user)
                .authorities(authorities)
                .build();
    }
}
