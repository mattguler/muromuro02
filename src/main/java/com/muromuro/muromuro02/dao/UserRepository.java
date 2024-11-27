package com.muromuro.muromuro02.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** The custom JPA repository that helps retrieve the user data from the Muromuro database. */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
