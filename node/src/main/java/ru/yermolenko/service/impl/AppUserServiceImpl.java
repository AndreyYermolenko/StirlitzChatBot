package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yermolenko.dao.AppUserDAO;
import ru.yermolenko.model.AppUser;
import ru.yermolenko.model.DataMessage;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.service.AppUserService;
import ru.yermolenko.service.MailSenderService;
import ru.yermolenko.tools.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static ru.yermolenko.model.UserState.*;

@Service
@Log4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final PasswordEncoder encoder;
    private final CryptoTool cryptoTool;
    private final MailSenderService mailSenderService;
    @Value("${link.address}")
    private String linkAddress;
    @Value("${server.port}")
    private String serverPort;

    public AppUserServiceImpl(AppUserDAO appUserDAO, PasswordEncoder encoder, CryptoTool cryptoTool,
                              MailSenderService mailSenderService) {
        this.appUserDAO = appUserDAO;
        this.encoder = encoder;
        this.cryptoTool = cryptoTool;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public MessageResponse registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return MessageResponse.builder()
                    .error(true)
                    .message("Вы уже зарегистрированы!")
                    .build();
        }
        return MessageResponse.builder()
                .error(false)
                .message("Введите, пожалуйста, ваш email:")
                .build();
    }

    @Override
    public MessageResponse setEmail(DataMessage dataMessage) {
        String email = dataMessage.getMessageText();
        AppUser currentAppUser = dataMessage.getAppUser();
        boolean parseError = false;

        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            parseError = true;
        }

        if (parseError) {
            return MessageResponse.builder()
                    .error(true)
                    .message("Пожалуйста, введите, корректный email!")
                    .build();
        }

        AppUser userByEmail = appUserDAO.findByEmail(email).orElse(null);
        if (userByEmail == null || userByEmail.getId().equals(currentAppUser.getId())) {
            currentAppUser.setEmail(email);
            currentAppUser.setState(WAIT_FOR_PASSWORD_STATE);
            appUserDAO.save(currentAppUser);
            return MessageResponse.builder()
                    .error(false)
                    .message("Отлично! А теперь введите ваш пароль. Подходящий пароль содержит, как минимум, " +
                            "верхний регистр, одну строчную букву, одну цифру и длиной от 6 до 10 символов.")
                    .build();
        } else {
            return MessageResponse.builder()
                    .error(true)
                    .message("Этот email уже используется! Введите корректный email!")
                    .build();
        }
    }

    @Override
    public MessageResponse setPassword(DataMessage dataMessage) {
        String regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,10}$";
        String password = dataMessage.getMessageText();
        AppUser appUser = dataMessage.getAppUser();

        if (password.matches(regexp)) {
            appUser.setState(BASIC_STATE);
            appUser.setPassword(encoder.encode(password));
            appUserDAO.save(appUser);

            String message = createTextMessageForConfirmation(appUser.getId());
            mailSenderService.send(appUser.getEmail(), "Activation", message);

            return MessageResponse.builder()
                    .error(false)
                    .message("Отлично! Чтобы закончить регистрацию - проверьте ваш email и подтвердите его!")
                    .build();
        } else {
            return MessageResponse.builder()
                    .error(true)
                    .message("Пароль неккоректный! Подходящий пароль содержит, как минимум, " +
                            "верхний регистр, одну строчную букву, одну цифру и длиной от 6 до 10 символов")
                    .build();
        }
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
