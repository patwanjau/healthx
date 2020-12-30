package com.healthx.configs;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.healthx.db.UserRepository;

import lombok.extern.java.Log;

@Log
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername invoked with username: " + username);
        var userDetails = userRepository.findUserByUsername(username)
                .map(UserDetailsWrapper::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        log.info("UserDetails with password - " + userDetails.getPassword());
        return userDetails;
    }
}
