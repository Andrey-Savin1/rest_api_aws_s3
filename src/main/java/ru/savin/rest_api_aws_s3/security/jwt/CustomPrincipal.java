package ru.savin.rest_api_aws_s3.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal implements Principal {

    private Long id;
    private String name;

}
