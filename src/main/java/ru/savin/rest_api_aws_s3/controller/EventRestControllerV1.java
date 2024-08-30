package ru.savin.rest_api_aws_s3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.dto.EventDto;
import ru.savin.rest_api_aws_s3.mapper.EventMapper;
import ru.savin.rest_api_aws_s3.model.Event;
import ru.savin.rest_api_aws_s3.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventRestControllerV1 {


    private final EventService eventService;
    private final EventMapper eventMapper;


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
    public Mono<ResponseEntity<EventDto>> getEventById(@PathVariable Long id) {
        return eventService.getById(id)
                .map(eventMapper::map)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
    public Flux<EventDto> getAllEvents() {
        return eventService.getAll().map(eventMapper::map);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteUserById(@PathVariable Long id) {
        return eventService
                .getById(id)
                .flatMap(
                        user ->
                                eventService.deleteById(user.getId())
                                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                                        .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
                );
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<EventDto>> updateEvent(@PathVariable("id") Long id, @RequestBody EventDto eventDto) {
        return eventService.getById(id)
                .flatMap(
                        event -> {
                            Event updateEvent = eventMapper.map(eventDto);
                            updateEvent.setId(event.getId());
                            return eventService.update(updateEvent).map(eventMapper::map).map(ResponseEntity::ok);
                        })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/event")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<EventDto>> saveEvent(@RequestBody EventDto eventDto) {
        Event event = eventMapper.map(eventDto);
        return eventService
                .save(event)
                .map(eventMapper::map)
                .map(saveUser -> ResponseEntity.status(HttpStatus.CREATED).body(saveUser));
    }

}
