package ru.savin.rest_api_aws_s3.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.repositiry.EventRepository;
import ru.savin.rest_api_aws_s3.model.Event;
import ru.savin.rest_api_aws_s3.service.EventService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Mono<Event> save(Event event) {
        return eventRepository.save(event)
                .doOnNext(saveEvent -> log.debug("Сохранен event с id: {}", saveEvent.getId()))
                .doOnError(error -> log.error("Ошибка сохранения event", error));
    }

    @Override
    public Mono<Event> update(Event event) {
        return eventRepository.save(event)
                .doOnNext(updateUser -> log.debug("Обновлен event с id: {}", updateUser.getId()))
                .doOnError(error -> log.error("Ошибка обновления event", error));
    }

    public Mono<Event> getById(Long id) {
        return eventRepository.findById(id)
                .doOnNext(user -> log.debug("Найден event с id: {}", id))
                .doOnError(error -> log.error("Event с id: {} не найден", id, error));
    }

    public Flux<Event> getAll() {
        return  eventRepository.findAll()
                .doOnNext(event -> log.debug("Найден event: {}", event.getId()))
                .doOnError(error -> log.error("Ошибка поиска events", error));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
       return eventRepository.deleteById(id);
    }
}
