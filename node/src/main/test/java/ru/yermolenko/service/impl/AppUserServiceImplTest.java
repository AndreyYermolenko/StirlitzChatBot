//package ru.yermolenko.service.impl;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import ru.yermolenko.dao.RoleDAO;
//import ru.yermolenko.dao.AppUserDAO;
//import ru.yermolenko.model.AppUser;
//import ru.yermolenko.model.ERole;
//import ru.yermolenko.model.Role;
//import ru.yermolenko.payload.request.SignupRequest;
//import ru.yermolenko.payload.response.MessageResponse;
//import ru.yermolenko.service.MailSenderService;
//import ru.yermolenko.service.AppUserService;
//
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//
//@SpringBootTest
//@ActiveProfiles("dev")
//class AppUserServiceImplTest {
//    @Autowired
//    private AppUserService appUserService;
//    @MockBean
//    private PasswordEncoder passwordEncoder;
//    @MockBean
//    private MailSenderService mailSenderService;
//    @MockBean
//    private RoleDAO roleDAO;
//    @MockBean
//    private AppUserDAO appUserDAO;
//
//    /**
//     * Successful test variation when user and admin roles are present in SignupRequest.
//     */
//    @Test
//    void registerUserSuccessfulTest1() {
//        Role ROLE_USER = Role.builder().id(1).name(ERole.ROLE_USER).build();
//        Role ROLE_ADMIN = Role.builder().id(2).name(ERole.ROLE_ADMIN).build();
//
//        Mockito.doReturn(Optional.of(ROLE_USER))
//                .when(roleDAO)
//                .findByName(eq(ERole.ROLE_USER));
//        Mockito.doReturn(Optional.of(ROLE_ADMIN))
//                .when(roleDAO)
//                .findByName(eq(ERole.ROLE_ADMIN));
//        SignupRequest sr = SignupRequest.builder()
//                .username("John")
//                .email("john.jackson@mail.ru")
//                .password("p@ssw0rd")
//                .role(Set.of(new String[]{"user", "admin"}))
//                .build();
//
//        Mockito.doReturn("encode_pass%")
//                .when(passwordEncoder)
//                .encode(sr.getPassword());
//
//        Mockito.doReturn(Optional.ofNullable(null))
//                .when(appUserDAO)
//                .findByUsername(sr.getUsername());
//        Mockito.doReturn(Optional.ofNullable(null))
//                .when(appUserDAO)
//                .findByEmail(sr.getEmail());
//
//        AppUser user = AppUser.builder()
//                .username(sr.getUsername())
//                .email(sr.getEmail())
//                .password(passwordEncoder.encode(sr.getPassword()))
//                .isActive(false)
//                .roles(Set.of(new Role[]{ROLE_USER, ROLE_ADMIN}))
//                .build();
//
//        AppUser persistentAppUser = AppUser.builder()
//                .id(1L)
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .isActive(user.getIsActive())
//                .roles(user.getRoles())
//                .build();
//
//        Mockito.doReturn(persistentAppUser)
//                .when(appUserDAO)
//                .save(user);
//
//        MessageResponse messageResponse = appUserService.registerUser(sr);
//
//        assertFalse(messageResponse.hasError());
//        Mockito.verify(mailSenderService, Mockito.times(1))
//                .send(eq(persistentAppUser.getEmail()), eq(("Activation")), anyString());
//    }
//
//    /**
//     * Successful test variation when any roles are absent in SignupRequest.
//     */
//    @Test
//    void registerUserSuccessfulTest2() {
//        Role ROLE_USER = Role.builder().id(1).name(ERole.ROLE_USER).build();
//
//        Mockito.doReturn(Optional.of(ROLE_USER))
//                .when(roleDAO)
//                .findByName(eq(ERole.ROLE_USER));
//        SignupRequest sr = SignupRequest.builder()
//                .username("John")
//                .email("john.jackson@mail.ru")
//                .password("p@ssw0rd")
//                .build();
//
//        Mockito.doReturn("encode_pass%")
//                .when(passwordEncoder)
//                .encode(sr.getPassword());
//
//        Mockito.doReturn(Optional.ofNullable(null))
//                .when(appUserDAO)
//                .findByUsername(sr.getUsername());
//        Mockito.doReturn(Optional.ofNullable(null))
//                .when(appUserDAO)
//                .findByEmail(sr.getEmail());
//
//        AppUser user = AppUser.builder()
//                .username(sr.getUsername())
//                .email(sr.getEmail())
//                .password(passwordEncoder.encode(sr.getPassword()))
//                .isActive(false)
//                .roles(Set.of(new Role[]{ROLE_USER}))
//                .build();
//
//        AppUser persistentAppUser = AppUser.builder()
//                .id(1L)
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .isActive(user.getIsActive())
//                .roles(user.getRoles())
//                .build();
//
//        Mockito.doReturn(persistentAppUser)
//                .when(appUserDAO)
//                .save(user);
//
//        MessageResponse messageResponse = appUserService.registerUser(sr);
//
//        assertFalse(messageResponse.hasError());
//        Mockito.verify(mailSenderService, Mockito.times(1))
//                .send(eq(persistentAppUser.getEmail()), eq(("Activation")), anyString());
//    }
//
//    /**
//     * Failed test variation when user doesn't complete registration with current username
//     * and change email in signup request.
//     */
//    @Test
//    void registerUserFailed1() {
//        SignupRequest sr = SignupRequest.builder()
//                .username("John")
//                .email("john.jackson@mail.ru")
//                .password("p@ssw0rd")
//                .build();
//
//        Mockito.doReturn(Optional.of(AppUser.builder().isActive(false).build()))
//                .when(appUserDAO)
//                .findByUsername(sr.getUsername());
//        Mockito.doReturn(Optional.ofNullable(null))
//                .when(appUserDAO)
//                .findByEmail(sr.getEmail());
//
//        MessageResponse messageResponse = appUserService.registerUser(sr);
//
//        String failedMessage = "Please check your email which you sent early for activation " +
//                                "else choose a new username with current email!";
//        assertEquals(failedMessage, messageResponse.getMessage());
//        assertTrue(messageResponse.hasError());
//        Mockito.verify(mailSenderService, Mockito.times(0))
//                .send(anyString(), eq(("Activation")), anyString());
//    }
//
//    /**
//     * Failed test variation when username is already taken.
//     */
//    @Test
//    void registerUserFailed2() {
//        SignupRequest sr = SignupRequest.builder()
//                .username("John")
//                .email("john.jackson@mail.ru")
//                .password("p@ssw0rd")
//                .build();
//
//        Mockito.doReturn(Optional.of(AppUser.builder().isActive(true).build()))
//                .when(appUserDAO)
//                .findByUsername(sr.getUsername());
//
//        MessageResponse messageResponse = appUserService.registerUser(sr);
//
//        String failedMessage = "Username is already taken!";
//        assertEquals(failedMessage, messageResponse.getMessage());
//        assertTrue(messageResponse.hasError());
//        Mockito.verify(mailSenderService, Mockito.times(0))
//                .send(anyString(), eq(("Activation")), anyString());
//    }
//
//    /**
//     * Failed test variation when email is already taken.
//     */
//    @Test
//    void registerUserFailed3() {
//        SignupRequest sr = SignupRequest.builder()
//                .username("John")
//                .email("john.jackson@mail.ru")
//                .password("p@ssw0rd")
//                .build();
//
//        Mockito.doReturn(Optional.of(AppUser.builder().isActive(true).build()))
//                .when(appUserDAO)
//                .findByEmail(sr.getEmail());
//
//        MessageResponse messageResponse = appUserService.registerUser(sr);
//
//        String failedMessage = "Email is already in use!";
//        assertEquals(failedMessage, messageResponse.getMessage());
//        assertTrue(messageResponse.hasError());
//        Mockito.verify(mailSenderService, Mockito.times(0))
//                .send(anyString(), eq(("Activation")), anyString());
//    }
//}
