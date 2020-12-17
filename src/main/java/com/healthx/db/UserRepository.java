package com.healthx.db;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.healthx.db.entities.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String name);
}
