package ru.yermolenko.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.yermolenko.dao.RoleDAO;
import ru.yermolenko.dao.UserDAO;
import ru.yermolenko.model.ERole;
import ru.yermolenko.model.Role;
import ru.yermolenko.model.User;
import ru.yermolenko.payload.request.SignupRequest;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.service.MailSenderService;
import ru.yermolenko.service.UserService;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private MailSenderService mailSenderService;
    @MockBean
    private RoleDAO roleDAO;
    @MockBean
    private UserDAO userDAO;

    @Test
    void registerUserSuccessful() {
        Role ROLE_USER = Role.builder().id(1).name(ERole.ROLE_USER).build();
        Role ROLE_ADMIN = Role.builder().id(2).name(ERole.ROLE_ADMIN).build();

        Mockito.doReturn(Optional.of(ROLE_USER))
                .when(roleDAO)
                .findByName(eq(ERole.ROLE_USER));
        Mockito.doReturn(Optional.of(ROLE_ADMIN))
                .when(roleDAO)
                .findByName(eq(ERole.ROLE_ADMIN));
        SignupRequest sr = SignupRequest.builder()
                .username("John")
                .email("john.jackson@mail.ru")
                .password("p@ssw0rd")
                .role(Set.of(new String[]{"user"}))
                .build();

        Mockito.doReturn("encode_pass%")
                .when(passwordEncoder)
                .encode(sr.getPassword());

        Mockito.doReturn(Optional.ofNullable(null))
                .when(userDAO)
                .findByUsername(sr.getUsername());
        Mockito.doReturn(Optional.ofNullable(null))
                .when(userDAO)
                .findByEmail(sr.getEmail());

        User user = User.builder()
                .username(sr.getUsername())
                .email(sr.getEmail())
                .password(passwordEncoder.encode(sr.getPassword()))
                .isActive(false)
                .roles(Set.of(new Role[]{ROLE_USER}))
                .build();

        User persistentUser = User.builder()
                .id(1L)
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .roles(user.getRoles())
                .build();

        Mockito.doReturn(persistentUser)
                .when(userDAO)
                .save(user);

        MessageResponse messageResponse = userService.registerUser(sr);

        assertFalse(messageResponse.hasError());
        Mockito.verify(mailSenderService, Mockito.times(1))
                .send(eq(persistentUser.getEmail()), eq(("Activation")), anyString());
    }
}