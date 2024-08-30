package ru.savin.rest_api_aws_s3.service.Impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import ru.savin.rest_api_aws_s3.repositiry.UserRepository;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.model.UserRole;
import ru.savin.rest_api_aws_s3.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> registerUser(User user) {
        return userRepository.save(User.builder()
                        .username(user.getUsername())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.ADMIN)
                        .status(Status.ACTIVE)
                        .build())
                .doOnSuccess(u -> log.info("IN registerUser - user {} created", u))
                .doOnError(error -> log.error("Error registering user", error));
    }

    @Override
    public Mono<User> save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user)
                .doOnNext(saveUser -> log.debug("Найден юзер с id: {}", saveUser.getId()))
                .doOnError(error -> log.error("Ошибка сохранения юзера", error));
    }

    @Override
    public Mono<User> update(User user) {
        return userRepository
                .findById(user.getId())
                .flatMap(searchedUser -> {
                    if (!searchedUser.getPassword().equals(user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return userRepository.save(user);
                })
                .doOnNext(updateUser -> log.debug("Найден юзер с id: {}", updateUser.getId()))
                .doOnError(error -> log.error("Ошибка обновления юзера", error));

    }

    @Override
    public Mono<User> getById(Long id) {
        return userRepository.findById(id)
                .doOnNext(user -> log.debug("Найден юзер с id: {}", id))
                .doOnError(error -> log.error("Юзер с id: {} не найден", id, error));
    }


    @Override
    public Flux<User> getAll() {
        return userRepository.findAll()
                .doOnNext(user -> log.debug("Найден юзер: {}", user.getUsername()))
                .doOnError(error -> log.error("Ошибка поиска юзеров", error));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Mono<User> findByUserName(String userName) {
        return userRepository.findByUsername(userName)
                .doOnNext(user -> log.debug("Найден юзер с id: {}", userName))
                .doOnError(error -> log.error("Юзер с именем: {} не найден", userName, error));
    }
}
