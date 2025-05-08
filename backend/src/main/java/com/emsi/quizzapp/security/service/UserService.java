package com.emsi.quizzapp.security.service;

import com.emsi.quizzapp.security.model.Role;
import com.emsi.quizzapp.security.model.User;
import com.emsi.quizzapp.security.repository.UserRepository;
import com.emsi.quizzapp.security.ws.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User register(UserDto userDto, Role role);
    User authenticate(String email, String password);

    @Service
    class Default implements UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public Default(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public User register(UserDto userDto, Role role) {
            User user = new User();
            user.setName(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setRole(role);
            return userRepository.save(user);
        }

        @Override
        public User authenticate(String email, String password) {
            return userRepository.findFirstByEmail(email)
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                    .orElse(null);
        }
    }
}