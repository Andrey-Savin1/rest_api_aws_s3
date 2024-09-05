package ru.savin.rest_api_aws_s3.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.savin.rest_api_aws_s3.model.Event;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.repositiry.EventRepository;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceImplTest {

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventServiceImpl eventService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	Event event = Event.builder()
			.Id(1L)
			.file_id(1L)
			.user_id(1L)
			.status(Status.ACTIVE)
			.build();

	@Test
	void save() {

		when(eventRepository.save(event)).thenReturn(Mono.just(event));

		Mono<Event> result = eventService.save(event);

		StepVerifier.create(result)
				.expectNext(event)
				.expectComplete()
				.verify();

	}

	@Test
	void update() {

		when(eventRepository.save(event)).thenReturn(Mono.just(event));

		Mono<Event> result = eventService.update(event);

		StepVerifier.create(result)
				.expectNext(event)
				.expectComplete()
				.verify();
	}

	@Test
	void getById() {
		when(eventRepository.findById(1L)).thenReturn(Mono.just(event));

		Mono<Event> result = eventService.getById(1L);

		StepVerifier.create(result)
				.expectNext(event)
				.expectComplete()
				.verify();
	}

	@Test
	void getAll() {
		Event event2 = Event.builder()
				.Id(2L)
				.file_id(2L)
				.user_id(2L)
				.status(Status.ACTIVE)
				.build();

		when(eventRepository.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(event, event2)));

		Flux<Event> result = eventService.getAll();

		StepVerifier.create(result)
				.expectNext(event)
				.expectNext(event2)
				.expectComplete()
				.verify();
	}

	@Test
	void deleteById() {

		Long idToDelete = 1L;

		// настраиваем поведение мока, чтобы deleteById вернул Mono.empty(),
		// что означает успешное удаление
		when(eventRepository.deleteById(idToDelete)).thenReturn(Mono.empty());

		Mono<Void> result = eventService.deleteById(idToDelete);

		StepVerifier.create(result)
				.expectComplete()  // Проверяем, что Mono завершился успешно
				.verify();

		// Проверяем, что метод deleteById в репозитории был вызван с правильным параметром
		verify(eventRepository).deleteById(idToDelete);

	}
}