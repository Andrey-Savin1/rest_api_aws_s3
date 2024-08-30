package ru.savin.rest_api_aws_s3.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.exception.UnauthorizedException;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.security.jwt.CustomPrincipal;
import ru.savin.rest_api_aws_s3.service.UserService;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userService.getById(customPrincipal.getId())
                .filter(user -> user.getStatus().equals(Status.ACTIVE))
                .switchIfEmpty(Mono.error(new UnauthorizedException("User disabled")))
                .map(user -> authentication);
    }
}
