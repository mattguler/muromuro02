package com.muromuro.muromuro02.security;

import com.muromuro.muromuro02.dao.User;
import com.muromuro.muromuro02.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.core.userdetails.User.builder;

/**
 * The custom UserDetailsService implementation to enable authorization for the
 * Spring Security framework. Uses the user data from the Muromuro database to
 * enable authorization for Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user =
                userRepository
                        .findById(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "No user found in MuromuroDB with username: "
                                                + username));
        // This is a builder for a different User class, defined in Spring Security itself.
        UserBuilder userBuilder = builder();
        return userBuilder
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .authorities(getAuthoritiesList(user.getAuthorities()))
                .build();
    }

    private List<SimpleGrantedAuthority> getAuthoritiesList(String authorities) {
        return Arrays.stream(authorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
