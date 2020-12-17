package com.healthx.services;

import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.healthx.db.ClientRepository;
import com.healthx.db.entities.Client;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ClientControllerTest {
    private final Supplier<Client> clientSupplier = () -> {
        var client = new Client();
        client.setName("healthX-client");
        client.setSecret("he@lthX-cl!ent");
        return client;
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void clearRepository() {
        clientRepository.deleteAll();
    }

    @Test
    public void testClientRegistration() throws Exception {
        var client = "{\"name\": \"healthX-client-new\", \"secret\": \"he@lthX-cl!ent\"}";
        mockMvc.perform(
                post("/clients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(client)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("healthX-client-new"));

        var savedClient = clientRepository.findClientByName("healthX-client-new");
        Assertions.assertTrue(savedClient.isPresent());
    }

    @Test
    public void testClientGet() throws Exception {
        var client = clientSupplier.get();
        clientRepository.save(client);

        mockMvc.perform(get("/clients/" + client.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(client.getId()));
    }

    @Test
    public void testNonExistentClient() throws Exception {
        mockMvc.perform(get("/clients/dummy"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testClients() throws Exception {
        var client = clientSupplier.get();
        clientRepository.save(client);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(client.getName()));
    }
}
