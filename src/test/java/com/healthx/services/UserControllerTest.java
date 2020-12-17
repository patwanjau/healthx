package com.healthx.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.healthx.db.UserRepository;
import com.healthx.db.entities.User;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void clearRepository() {
        userRepository.deleteAll();
    }

    @Test
    public void testUserCreation() throws Exception {
        var user = "{\"username\": \"test-user\", \"password\": \"password\"}";
        mockMvc.perform(
                post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("test-user"));

        var savedUser = userRepository.findByUsername("test-user");
        Assertions.assertTrue(savedUser.isPresent());
    }

    @Test
    public void testUserGet() throws Exception {
        var user = new User();
        user.setUsername("johnDoe");
        user.setPassword("j0hnD0e1234Pass");
        userRepository.save(user);

        mockMvc.perform(get("/users/" + user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    public void testNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/dummy"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUsers() throws Exception {
        var user = new User();
        user.setUsername("johnDoe");
        user.setPassword("j0hnD0e1234Pass");
        userRepository.save(user);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value(user.getUsername()));
    }
}
