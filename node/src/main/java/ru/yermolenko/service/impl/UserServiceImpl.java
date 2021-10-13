package ru.yermolenko.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yermolenko.dao.RoleDAO;
import ru.yermolenko.dao.UserDAO;
import ru.yermolenko.model.ERole;
import ru.yermolenko.model.Role;
import ru.yermolenko.model.User;
import ru.yermolenko.payload.request.SignupRequest;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final PasswordEncoder encoder;
    private final RoleDAO roleDAO;

    public UserServiceImpl(UserDAO userDAO, PasswordEncoder encoder, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.encoder = encoder;
        this.roleDAO = roleDAO;
    }

    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userDAO.existsByUsername(signUpRequest.getUsername())) {
            return MessageResponse.builder()
                    .message("Error: Username is already taken!")
                    .error(true)
                    .build();
        }

        if (userDAO.existsByEmail(signUpRequest.getEmail())) {
            return MessageResponse.builder()
                    .message("Error: Email is already in use!")
                    .error(true)
                    .build();
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> rolesFromRequest = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (rolesFromRequest == null || rolesFromRequest.isEmpty()) {
            Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            rolesFromRequest.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleDAO.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "user":
                        Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);

                        break;
                }
            });
        }

        user.setRoles(roles);
        userDAO.save(user);

        return MessageResponse.builder()
                .message("User registered successfully!")
                .error(false)
                .build();
    }
}
