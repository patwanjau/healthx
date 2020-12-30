package com.healthx.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import com.healthx.db.UserRepository;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("classpath:healthx.jks")
    private Resource keyStoreResource;
    @Value("${app.keystore.alias}")
    private String keyStoreAlias;
    @Value("${app.keystore.password}")
    private String keyStorePassword;
    private UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .and()
                .csrf(c -> c.ignoringAntMatchers("/users/**", "/clients/**"))
                .authorizeRequests()
                .mvcMatchers("/users/**").permitAll()
                .mvcMatchers("/clients/**").permitAll()
                .and()
                .userDetailsService(userDetailsService())
                .authenticationProvider(authenticationProvider());
        super.configure(http);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(authenticationProvider())
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
        super.configure(auth);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new AuthenticationProviderService();
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        /*
        var userDetailsManager = new InMemoryUserDetailsManager();
        var user = User.withUsername("healthx-user").password("P@ssw0rd")
                .authorities("read", "write", "trust").build();
        userDetailsManager.createUser(user);
        return userDetailsManager;
        */
        return new JpaUserDetailsService(userRepository);
    }

    @Bean
    public TokenStore tokenStore() {
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
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    @Primary
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
