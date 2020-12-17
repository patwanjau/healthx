package com.healthx.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("classpath:healthx.jks")
    private Resource keyStoreResource;
    @Value("${app.keystore.alias}")
    private String keyStoreAlias;
    @Value("${app.keystore.password}")
    private String keyStorePassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin();
        http.csrf(c -> c.ignoringAntMatchers("/users/**", "/clients/**"));
        http.authorizeRequests()
                .mvcMatchers("/users/**").permitAll()
                .mvcMatchers("/clients/**").permitAll();
        super.configure(http);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var userDetailsManager = new InMemoryUserDetailsManager();
        var user = User.withUsername("healthx-user").password("P@ssw0rd")
                .authorities("read", "write", "trust").build();
        userDetailsManager.createUser(user);
        return userDetailsManager;
    }

    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    @Primary
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        var accessTokenConverter = new JwtAccessTokenConverter();
        var keyStoreFactory = new KeyStoreKeyFactory(keyStoreResource, keyStorePassword.toCharArray());
        accessTokenConverter.setKeyPair(keyStoreFactory.getKeyPair(keyStoreAlias));
        return accessTokenConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //noinspection deprecation
        return NoOpPasswordEncoder.getInstance(); //FIXME: Upgrade to BCryptPasswordEncoder || DelegatingPasswordEncoder
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
