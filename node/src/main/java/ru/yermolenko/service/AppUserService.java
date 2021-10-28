package ru.yermolenko.service;

import ru.yermolenko.model.AppUser;
import ru.yermolenko.model.DataMessage;
import ru.yermolenko.payload.response.MessageResponse;

public interface AppUserService {
    MessageResponse registerUser(AppUser appUser);
    MessageResponse confirmRegistration(String id);
    MessageResponse setEmail(DataMessage dataMessage);
    MessageResponse setPassword(DataMessage dataMessage);
}
