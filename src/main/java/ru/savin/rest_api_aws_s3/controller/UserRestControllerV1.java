package ru.savin.rest_api_aws_s3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.dto.UserDto;
import ru.savin.rest_api_aws_s3.mapper.UserMapper;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.repositiry.EventRepository;
import ru.savin.rest_api_aws_s3.service.UserService;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<UserDto>> saveUser(@RequestBody UserDto userDto) {
        User user = userMapper.map(userDto);
        return userService
                .save(user)
                .map(userMapper::map)
                .map(saveUser -> ResponseEntity.status(HttpStatus.CREATED).body(saveUser));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<UserDto>> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        return userService.getById(id)
                .flatMap(
                        user -> {
                            User updateUser = userMapper.map(userDto);
                            updateUser.setId(user.getId());
                            return userService.update(updateUser).map(userMapper::map).map(ResponseEntity::ok);
                        })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'MODERATOR', 'ADMIN')")
    public Mono<ResponseEntity<UserDto>> getUserById(@PathVariable("id") Long id) {
        return userService.getById(id)
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("username/{userName}")
    @PreAuthorize("hasAnyAuthority('USER', 'MODERATOR', 'ADMIN')")
    public Mono<ResponseEntity<UserDto>> getUserByName(@PathVariable("userName") String userName) {
        return userService
                .findByUserName(userName)
                .map(userMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR' )")
    public Flux<UserDto> getAllUsers() {
        return userService.getAll().map(userMapper::map);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteUserById(@PathVariable Long id) {
        return userService
                .getById(id)
                .flatMap(
                        user ->
                                userService.deleteById(user.getId())
                                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                                        .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
                );
    }
}
