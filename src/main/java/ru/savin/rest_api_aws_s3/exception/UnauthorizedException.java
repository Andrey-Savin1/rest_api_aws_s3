package ru.savin.rest_api_aws_s3.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException{

    public UnauthorizedException(String message) {
        super(message, "SLAY_UNAUTHORIZED");
    }
}
