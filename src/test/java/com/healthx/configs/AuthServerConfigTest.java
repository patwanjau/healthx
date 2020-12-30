package com.healthx.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.healthx.db.ClientRepository;
import com.healthx.db.UserRepository;
import com.healthx.db.entities.Client;
import com.healthx.db.entities.User;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServerConfigTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testTokenGenerationForValidUserAndClient() throws Exception {
        mockMvc.perform(
                post("/oauth/token")
                        .with(httpBasic("healthX-client", "s3cr3t"))
                        .queryParam("grant_type", "password")
                        .queryParam("username", "healthx-user")
                        .queryParam("password", "P@ssw0rd")
                        .queryParam("scope", "read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.jti").exists());
    }

    @BeforeEach
    public void setUp() {
        Client client = new Client();
        client.setName("healthX-client");
        client.setSecret("s3cr3t");
        clientRepository.save(client);

        User user = new User();
        user.setPassword(passwordEncoder.encode("P@ssw0rd"));
        user.setUsername("healthx-user");
        user.setEnabled(true);
        userRepository.save(user);
    }
}
