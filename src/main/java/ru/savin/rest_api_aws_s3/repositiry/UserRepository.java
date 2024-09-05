package ru.savin.rest_api_aws_s3.repositiry;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.model.User;


public interface UserRepository extends R2dbcRepository<User, Long> {

    Mono<User> findByUsername(String username);

}
