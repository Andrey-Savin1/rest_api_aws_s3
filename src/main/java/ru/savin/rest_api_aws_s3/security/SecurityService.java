package ru.savin.rest_api_aws_s3.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.exception.AuthException;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.security.jwt.TokenDetails;
import ru.savin.rest_api_aws_s3.service.UserService;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.password.secret}")
    private String secret;
    @Value("${jwt.password.exoiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.password.issuer}")
    private String issuer;


    private TokenDetails generateToken(User user) {

        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getRole());
            put("username", user.getUsername());
        }};

        return generateToken(claims, user.getId().toString());

    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        Long expirationTimeInMillis = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationTimeInMillis);
        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createDate = new Date();
        String token =
            Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(createDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();

        return TokenDetails.builder()
                .token(token)
                .issuedAt(createDate)
                .expiresAt(expirationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(String name, String password) {
        return userService.findByUserName(name)
                .flatMap(user -> {
                    if (user.getStatus().equals(Status.DELETED)) {
                        return Mono.error(new AuthException("Account disabled", "SLAY_USER_ACCOUNT_DISABLED"));
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new AuthException("invalid password", "SLAY_INVALID_PASSWORD"));
                    }
                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid username", "SLAY_INVALID_USERNAME")));
    }
}
