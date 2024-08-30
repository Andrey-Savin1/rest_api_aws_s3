package ru.savin.rest_api_aws_s3.repositiry;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.savin.rest_api_aws_s3.model.Event;


public interface EventRepository extends R2dbcRepository<Event, Long> {


	Flux<Event> findByUser_id(Long id);
}
