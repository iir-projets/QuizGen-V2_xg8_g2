// UserRepository.java
package com.emsi.quizzapp.security.repository;

import com.emsi.quizzapp.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Use improved case-insensitive query with debug logging
    default Optional<User> findFirstByEmail(String email) {
        System.out.println("Finding user by email: " + email);

        // Print all users in the database for debugging
        System.out.println("All users in the database:");
        this.findAll().forEach(user ->
                System.out.println(" - " + user.getId() + ": " + user.getEmail() + " (" + user.getRole() + ")"));

        // Use case-insensitive search
        return findByEmailIgnoreCase(email);
    }

    Optional<User> findByEmailIgnoreCase(String email);
}
