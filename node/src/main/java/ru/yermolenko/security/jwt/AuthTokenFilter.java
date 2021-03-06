package ru.yermolenko.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.yermolenko.payload.error.ErrorMessage;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.security.services.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

@Log4j
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private final Set<String> REQUESTS_WITHOUT_ACCESS_TOKEN = Set.of(
            "/api/auth/sign_up",
            "/api/auth/confirm",
            "/api/auth/sign_in",
            "/api/auth/refresh_token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            MessageResponse validationResult = jwtUtils.validateJwtToken(jwt);
            // Case when jwt == null is authentication stage.
            if (jwt != null && !validationResult.hasError()) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null && validationResult.hasError()) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                ErrorMessage errorMessage = ErrorMessage.builder()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .timestamp(new Date())
                        .message(validationResult.getMessage())
                        .description("")
                        .build();
                response.getWriter().write(new ObjectMapper().writeValueAsString(errorMessage));
                return;
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: " +  e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        String path = request.getRequestURI();
        return REQUESTS_WITHOUT_ACCESS_TOKEN.contains(path);
    }
}
