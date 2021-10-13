package ru.yermolenko.service;

import ru.yermolenko.payload.request.SignupRequest;
import ru.yermolenko.payload.response.MessageResponse;

public interface UserService {
    MessageResponse registerUser(SignupRequest signUpRequest);
    MessageResponse confirmRegistration(String id);
}
