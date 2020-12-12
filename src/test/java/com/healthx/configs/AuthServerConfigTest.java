package com.healthx.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServerConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTokenGenerationForValidUserAndClient() throws Exception {
        mockMvc.perform(
                post("/oauth/token")
                        .with(httpBasic("healthX-client", "s3cr3t"))
                        .queryParam("grant_type", "password")
                        .queryParam("username", "healthx-user")
                        .queryParam("password", "P@ssw0rd")
                        .queryParam("scope", "read"))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.jti").exists())
                .andExpect(status().isOk());
    }
}
