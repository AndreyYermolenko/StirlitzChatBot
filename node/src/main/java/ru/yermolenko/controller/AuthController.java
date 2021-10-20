package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yermolenko.exception.TokenRefreshException;
import ru.yermolenko.model.RefreshToken;
import ru.yermolenko.payload.request.LoginRequest;
import ru.yermolenko.payload.request.SignupRequest;
import ru.yermolenko.payload.request.TokenRefreshRequest;
import ru.yermolenko.payload.response.JwtResponse;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.payload.response.TokenRefreshResponse;
import ru.yermolenko.security.jwt.JwtUtils;
import ru.yermolenko.security.services.RefreshTokenService;
import ru.yermolenko.security.services.UserDetailsImpl;
import ru.yermolenko.service.AppUserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Log4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final AppUserService appUserService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService, AppUserService appUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.appUserService = appUserService;
    }

    @PostMapping("/sign_up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse messageResponse = appUserService.registerUser(signUpRequest);
        log.debug(messageResponse);

        if (messageResponse.hasError()) {
            return ResponseEntity.badRequest().body(messageResponse);
        } else {
            return ResponseEntity.ok(messageResponse);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("id") String id) {
        MessageResponse messageResponse = appUserService.confirmRegistration(id);
        if (messageResponse.hasError()) {
            return ResponseEntity.badRequest().body(messageResponse);
        } else {
            return ResponseEntity.ok(messageResponse);
        }
    }

    @PostMapping("/sign_in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    refreshTokenService.deleteByUserId(user.getId());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!"));
    }

    @GetMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.deleteByUsername(userDetails.getUsername());
        return ResponseEntity.ok(MessageResponse.builder().message("Log out successful!").build());
    }
}
