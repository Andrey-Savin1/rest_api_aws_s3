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
import ru.savin.rest_api_aws_s3.model.File;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.repositiry.FileRepository;
import ru.savin.rest_api_aws_s3.service.FileService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileServiceImplTest {

	@Mock
	private FileRepository fileRepository;

	@InjectMocks
	private FileServiceImpl fileService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	void save() {

		File inputFile = File.builder()
				.Id(1L)
				.name("test")
				.build();

		when(fileRepository.save(inputFile)).thenReturn(Mono.just(inputFile));

		Mono<File> result = fileService.save(inputFile);

		StepVerifier.create(result)
				.expectNext(inputFile)
				.expectComplete()
				.verify();
	}

	@Test
	void deleteById() {

		Long idToDelete = 1L;

		// настраиваем поведение мока, чтобы deleteById вернул Mono.empty(),
		// что означает успешное удаление
		when(fileRepository.deleteById(idToDelete)).thenReturn(Mono.empty());

		Mono<Void> result = fileService.deleteById(idToDelete);

		StepVerifier.create(result)
				.expectComplete()
				.verify();

		verify(fileRepository).deleteById(idToDelete);
	}

	@Test
	void getById() {
		File inputFile = File.builder()
				.Id(1L)
				.name("test")
				.build();
		when(fileRepository.findById(1L)).thenReturn(Mono.just(inputFile));

		Mono<File> result = fileService.getById(1L);

		StepVerifier.create(result)
				.expectNext(inputFile)
				.expectComplete()
				.verify();
	}

	@Test
	void getAll() {

		File inputFile = File.builder()
				.Id(1L)
				.name("test")
				.build();

		File inputFile2 = File.builder()
				.Id(1L)
				.name("test")
				.build();

		when(fileRepository.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(inputFile, inputFile2)));

		Flux<File> result = fileService.getAll();

		StepVerifier.create(result)
				.expectNext(inputFile)
				.expectNext(inputFile2)
				.expectComplete()
				.verify();

	}

	@Test
	void update() {

		File inputFile = File.builder()
				.Id(1L)
				.name("test")
				.build();

		when(fileRepository.save(inputFile)).thenReturn(Mono.just(inputFile));


		Mono<File> result = fileService.update(inputFile);

		StepVerifier.create(result)
				.expectNext(inputFile)
				.expectComplete()
				.verify();

	}

}