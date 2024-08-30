package ru.savin.rest_api_aws_s3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.dto.AuthRequestDto;
import ru.savin.rest_api_aws_s3.dto.AuthResponseDto;
import ru.savin.rest_api_aws_s3.dto.UserDto;
import ru.savin.rest_api_aws_s3.mapper.UserMapper;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.security.SecurityService;
import ru.savin.rest_api_aws_s3.security.jwt.CustomPrincipal;
import ru.savin.rest_api_aws_s3.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationRestControllerV1 {


    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        User user = userMapper.map(dto);
        return userService.registerUser(user)
                .map(userMapper::map);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        return securityService.authenticate(authRequestDto.getUsername(), authRequestDto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getById(customPrincipal.getId())
                .map(userMapper::map);
    }
}
