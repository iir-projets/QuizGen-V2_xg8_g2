package com.emsi.quizzapp.security.ws.controller;

import com.emsi.quizzapp.security.model.Role;
import com.emsi.quizzapp.security.model.User;
import com.emsi.quizzapp.security.jwt.JwtTokenProvider;
import com.emsi.quizzapp.security.service.UserService;
import com.emsi.quizzapp.security.ws.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService,
                          JwtTokenProvider tokenProvider,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register/participant")
    public ResponseEntity<?> registerParticipant(@RequestBody UserDto userDto) {
        User user = userService.register(userDto, Role.PARTICIPANT);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register/creator")
    public ResponseEntity<?> registerCreator(@RequestBody UserDto userDto) {
        User user = userService.register(userDto, Role.CREATOR);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDto userDto) {
        User user = userService.register(userDto, Role.ADMIN);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Maintenant ce cast fonctionnera
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "type", "Bearer",
                "id", user.getId(),
                "email", user.getEmail(),
                "roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        ));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}