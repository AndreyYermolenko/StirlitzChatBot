package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yermolenko.dao.AppUserDAO;
import ru.yermolenko.dao.RoleDAO;
import ru.yermolenko.model.AppUser;
import ru.yermolenko.model.ERole;
import ru.yermolenko.model.Role;
import ru.yermolenko.payload.request.SignupRequest;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.service.MailSenderService;
import ru.yermolenko.service.AppUserService;
import ru.yermolenko.tools.CryptoTool;

import java.util.HashSet;
import java.util.Set;

@Service
@Log4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final PasswordEncoder encoder;
    private final RoleDAO roleDAO;
    private final CryptoTool cryptoTool;
    private final MailSenderService mailSenderService;
    @Value("${link.address}")
    private String linkAddress;
    @Value("${server.port}")
    private String serverPort;

    public AppUserServiceImpl(AppUserDAO appUserDAO, PasswordEncoder encoder, RoleDAO roleDAO,
                              CryptoTool cryptoTool, MailSenderService mailSenderService) {
        this.appUserDAO = appUserDAO;
        this.encoder = encoder;
        this.roleDAO = roleDAO;
        this.cryptoTool = cryptoTool;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        AppUser userByUsername = appUserDAO.findByUsername(signUpRequest.getUsername()).orElse(null);
        AppUser userByEmail = appUserDAO.findByEmail(signUpRequest.getEmail()).orElse(null);
        if (userByUsername != null && !userByUsername.getIsActive() && userByEmail == null) {
            return MessageResponse.builder()
                    .message("Please check your email which you sent early for activation " +
                            "else choose a new username with current email!")
                    .error(true)
                    .build();
        }

        if (userByUsername != null) {
            if (userByUsername.getIsActive()) {
                return MessageResponse.builder()
                        .message("Username is already taken!")
                        .error(true)
                        .build();
            } else {
                String message = createTextMessageForConfirmation(userByUsername.getId());
                mailSenderService.send(userByUsername.getEmail(), "Activation", message);
                return MessageResponse.builder()
                        .message("Please check your email and go to activate link!")
                        .error(false)
                        .build();
            }
        }

        if (userByEmail != null) {
            if (userByEmail.getIsActive()) {
                return MessageResponse.builder()
                        .message("Email is already in use!")
                        .error(true)
                        .build();
            } else {
                String message = createTextMessageForConfirmation(userByEmail.getId());
                mailSenderService.send(userByEmail.getEmail(), "Activation", message);
                return MessageResponse.builder()
                        .message("Please check your email and go to activate link!")
                        .error(false)
                        .build();
            }
        }

//        AppUser user = new AppUser(signUpRequest.getUsername(), signUpRequest.getEmail(),
//                encoder.encode(signUpRequest.getPassword()));

        Set<String> rolesFromRequest = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (rolesFromRequest == null || rolesFromRequest.isEmpty()) {
            Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role is not found."));
            roles.add(userRole);
        } else {
            rolesFromRequest.forEach(role -> {
                switch (role) {
//                    case "admin":
//                        Role adminRole = roleDAO.findByName(ERole.ROLE_ADMIN)
//                                .orElseThrow(() -> new RuntimeException("Role is not found."));
//                        roles.add(adminRole);
//
//                        break;
                    case "user":
                        Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role is not found."));
                        roles.add(userRole);

                        break;
                }
            });
        }

//        user.setRoles(roles);
//        user.setIsActive(false);
//        AppUser persistentAppUser = appUserDAO.save(user);

//        String message = createTextMessageForConfirmation(persistentAppUser.getId());
//        mailSenderService.send(persistentAppUser.getEmail(), "Activation", message);
        return MessageResponse.builder()
                .message("Please check your email and go to activate link!")
                .error(false)
                .build();
    }

    @Override
    public MessageResponse confirmRegistration(String id) {
        Long userId = cryptoTool.idOf(id);;
        if (userId == null) {
            return MessageResponse.builder()
                    .message("Incorrect ID!")
                    .error(true)
                    .build();
        }
        AppUser appUser = appUserDAO.findById(userId).orElse(null);
        if (appUser != null && appUser.getIsActive()) {
            return MessageResponse.builder()
                    .message("User is already activated!")
                    .error(true)
                    .build();
        } else if (appUser != null && !appUser.getIsActive()) {
            appUser.setIsActive(true);
            appUserDAO.save(appUser);
            return MessageResponse.builder()
                    .message("User registered successfully!")
                    .error(false)
                    .build();
        } else {
            return MessageResponse.builder()
                    .message("User not found!")
                    .error(true)
                    .build();
        }
    }

    private String createTextMessageForConfirmation(Long userId) {
        String hash = cryptoTool.hashOf(userId);
        return "http://" + linkAddress + ":" + serverPort + "/api/auth/confirm?id=" + hash;
    }
}
