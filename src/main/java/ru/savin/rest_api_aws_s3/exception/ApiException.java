package ru.savin.rest_api_aws_s3.exception;

import lombok.Getter;

public class ApiException extends RuntimeException{

    @Getter
    protected String errorCode;

    public ApiException(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }

}
