package ru.savin.rest_api_aws_s3.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericService<T, Id> {

    Mono<T> save(T t);

    Mono<T> update(T t);

    Mono<T> getById(Id id);

    Flux<T> getAll();

    Mono<Void> deleteById(Id id);


}
