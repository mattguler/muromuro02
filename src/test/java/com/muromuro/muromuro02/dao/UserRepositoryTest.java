package com.muromuro.muromuro02.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the User JPA Repository.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("passwordformuromuro");
        user.setEnabled(true);
        user.setAuthorities("ROLE_USER,ROLE_ADMIN");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        Optional<User> dbUser = userRepository.findById("testuser");
        assertTrue(dbUser.isPresent());
        assertEquals("testuser", dbUser.get().getUsername());
        assertEquals("passwordformuromuro", dbUser.get().getPassword());
        assertTrue(dbUser.get().isEnabled());
        assertEquals("ROLE_USER,ROLE_ADMIN", dbUser.get().getAuthorities());
        assertEquals(LocalDateTime.now().getYear(), dbUser.get().getCreatedAt().getYear());
        assertEquals(LocalDateTime.now().getMonthValue(), dbUser.get().getCreatedAt().getMonthValue());
        assertEquals(LocalDateTime.now().getDayOfMonth(), dbUser.get().getCreatedAt().getDayOfMonth());

        userRepository.deleteById("testuser");
    }
}
