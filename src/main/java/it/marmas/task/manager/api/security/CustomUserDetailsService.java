package it.marmas.task.manager.api.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.repo.UserRepository;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Your User repository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This method is called by Spring Security to load user data for authentication

        UserDetails user = null;
        String findBy = "";
        Optional<User> opt;

        // Determine if the input is an email or username
        if (username.contains("@")) {
            opt = userRepository.findByEmail(username);
            findBy = "email";   // small fix: you were setting "username" here by mistake
        } else {
            opt = userRepository.findByUsername(username);
            findBy = "username";
        }
        final String findByFinal = findBy;

        // Wrap the User entity into a CustomUserDetails object or throw exception if not found
        user = opt.map(CustomUserDetails::new)
                  .orElseThrow(() -> new UsernameNotFoundException(
                      "User not found with " + findByFinal + ": " + username));

        return user;
    }
}
