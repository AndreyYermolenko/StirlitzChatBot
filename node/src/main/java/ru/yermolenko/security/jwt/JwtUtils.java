package ru.yermolenko.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.security.services.UserDetailsImpl;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public MessageResponse validateJwtToken(String authToken) {
        MessageResponse response = MessageResponse.builder().error(false).build();
        String errorMessage = null;
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return response;
        } catch (SignatureException e) {
            errorMessage = "Invalid JWT signature";
            logger.error(errorMessage + ": ", e.getMessage());
        } catch (MalformedJwtException e) {
            errorMessage = "Invalid JWT token";
            logger.error(errorMessage + ": ", e.getMessage());
        } catch (ExpiredJwtException e) {
            errorMessage = "JWT token is expired";
            logger.error(errorMessage + ": ", e.getMessage());
        } catch (UnsupportedJwtException e) {
            errorMessage = "JWT token is unsupported";
            logger.error(errorMessage + ": ", e.getMessage());
        } catch (IllegalArgumentException e) {
            errorMessage = "JWT claims string is empty";
            logger.error(errorMessage + ": ", e.getMessage());
        }
        response.setMessage(errorMessage);
        response.setError(true);
        return response;
    }

}
