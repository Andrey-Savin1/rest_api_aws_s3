package ru.savin.rest_api_aws_s3.service;

import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.model.User;

public interface UserService extends GenericService<User, Long> {

	Mono<User> findByUserName(String userName);

	Mono<User> registerUser(User user);

}
