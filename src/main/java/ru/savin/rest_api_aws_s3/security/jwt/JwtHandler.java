package ru.savin.rest_api_aws_s3.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.exception.AuthException;
import ru.savin.rest_api_aws_s3.exception.UnauthorizedException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JwtHandler {

    private final String secret;

    public JwtHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check (String accessToken){
        return Mono.just(verify(accessToken))
                .onErrorResume(e -> Mono.error( new UnauthorizedException(e.getMessage())));
    }

    private  VerificationResult verify(String token){
        Claims claims = getClaimsFromToken(token);
        final Date expirationDate = claims.getExpiration();
        if(expirationDate.before(new Date())) {
            throw  new RuntimeException("Token expired");
        }
        return new VerificationResult(claims,token);
    }

    private Claims getClaimsFromToken(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
       return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
//        return Jwts.parser()
//                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
//                .parseClaimsJwt(token)
//                .getBody();
    }

    public static class VerificationResult{

        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }

}
