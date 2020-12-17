package com.healthx.db;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.healthx.db.entities.Client;

public interface ClientRepository extends CrudRepository<Client, Integer> {

    Optional<Client> findClientByName(String name);
}
