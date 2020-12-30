package com.healthx.configs;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

//@Service
public class AuthenticationProviderService extends DaoAuthenticationProvider {
    /*
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();
//        new UsernamePasswordAuthenticationToken()
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
    */
}
