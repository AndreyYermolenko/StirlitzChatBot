package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
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
import ru.yermolenko.tools.CryptoTool;

import java.util.HashSet;
import java.util.Set;

@Service
@Log4j
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final PasswordEncoder encoder;
    private final RoleDAO roleDAO;
    private final CryptoTool cryptoTool;
    @Value("${link.address}")
    private String linkAddress;
    @Value("${server.port}")
    private String serverPort;

    public UserServiceImpl(UserDAO userDAO, PasswordEncoder encoder, RoleDAO roleDAO, CryptoTool cryptoTool) {
        this.userDAO = userDAO;
        this.encoder = encoder;
        this.roleDAO = roleDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        User userByUsername = userDAO.findByUsername(signUpRequest.getUsername()).orElse(null);
        if (userByUsername != null) {
            if (userByUsername.getIsActive()) {
                return MessageResponse.builder()
                        .message("Username is already taken!")
                        .error(true)
                        .build();
            } else {
                return MessageResponse.builder()
                        .linkForConfirmation(createLinkForConfirm(userByUsername.getId()))
                        .error(true)
                        .build();
            }
        }

        User userByEmail = userDAO.findByEmail(signUpRequest.getEmail()).orElse(null);
        if (userByEmail != null) {
            if (userByEmail.getIsActive()) {
                return MessageResponse.builder()
                        .message("Email is already in use!")
                        .error(true)
                        .build();
            } else {
                return MessageResponse.builder()
                        .linkForConfirmation(createLinkForConfirm(userByEmail.getId()))
                        .error(true)
                        .build();
            }
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> rolesFromRequest = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (rolesFromRequest == null || rolesFromRequest.isEmpty()) {
            Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role is not found."));
            roles.add(userRole);
        } else {
            rolesFromRequest.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleDAO.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "user":
                        Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role is not found."));
                        roles.add(userRole);

                        break;
                }
            });
        }

        user.setRoles(roles);
        user.setIsActive(false);
        User persistentUser = userDAO.save(user);
        String urlToConfirm = createLinkForConfirm(persistentUser.getId());

        return MessageResponse.builder()
                .linkForConfirmation(urlToConfirm)
                .error(false)
                .build();
    }

    @Override
    public MessageResponse confirmRegistration(String id) {
        Long userId = cryptoTool.idOf(id);;
        if (userId == null) {
            return MessageResponse.builder()
                    .message("User not found by this ID!")
                    .error(true)
                    .build();
        }

        User user = userDAO.findById(userId).orElse(null);
        if (user != null) {
            user.setIsActive(true);
            userDAO.save(user);
        }
        return MessageResponse.builder()
                .message("User registered successfully!")
                .error(false)
                .build();
    }

    private String createLinkForConfirm(Long userId) {
        String hash = cryptoTool.hashOf(userId);
        return "http://" + linkAddress + ":" + serverPort + "/api/auth/confirm?id=" + hash;
    }
}
