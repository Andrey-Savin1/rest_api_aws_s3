package ru.savin.rest_api_aws_s3.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.security.jwt.CustomPrincipal;
import ru.savin.rest_api_aws_s3.security.jwt.JwtHandler;

import java.util.List;

public class UserAuthenticationBearer {

    public static Mono<Authentication> create (JwtHandler.VerificationResult verificationResult){

        Claims claims = verificationResult.claims;
        String subject = claims.getSubject();

        String role = claims.get("role", String.class);
        String userName = claims.get("name", String.class);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        Long principalId = Long.parseLong(subject);
        CustomPrincipal customPrincipal = new CustomPrincipal(principalId, userName);

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(customPrincipal, null, authorities));
    }

}
