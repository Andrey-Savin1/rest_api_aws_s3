package ru.savin.rest_api_aws_s3.service.Impl;

import com.amazonaws.services.mq.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.mapper.FileMapper;
import ru.savin.rest_api_aws_s3.model.Event;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.repositiry.EventRepository;
import ru.savin.rest_api_aws_s3.repositiry.FileRepository;
import ru.savin.rest_api_aws_s3.model.File;
import ru.savin.rest_api_aws_s3.repositiry.UserRepository;
import ru.savin.rest_api_aws_s3.service.FileService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

	private final FileRepository fileRepository;
	private final AmazonS3 s3client;
	private final UserRepository userRepository;
	private final EventRepository eventRepository;

	@Override
	public Mono<File> save(File file) {
		return fileRepository.save(file)
				.doOnNext(saveEvent -> log.debug("Сохранен file с id: {}", saveEvent.getId()))
				.doOnError(error -> log.error("Ошибка сохранения file", error));
	}

	@Override
	public Mono<Void> deleteById(Long id) {
		return fileRepository.deleteById(id);
	}

	@Override
	public Mono<File> getById(Long id) {
		return fileRepository.findById(id)
				.doOnNext(user -> log.debug("Найден file с id: {}", id))
				.doOnError(error -> log.error("File с id: {} не найден", id, error));
	}

	@Override
	public Flux<File> getAll() {
		return fileRepository.findAll()
				.doOnNext(event -> log.debug("Найден file: {}", event.getId()))
				.doOnError(error -> log.error("Ошибка поиска files", error));
	}

	@Override
	public Mono<File> update(File file) {
		return fileRepository.save(file)
				.doOnNext(updateUser -> log.debug("Обновлен file с id: {}", updateUser.getId()))
				.doOnError(error -> log.error("Ошибка обновления file", error));
	}

	@Override
	public Mono<File> uploadFiles(Long userId, FilePart filePart) {
		return searchUser(userId).flatMap(
				user ->{
					var url = s3client.getUrl("s3testrest", filePart.filename()).toExternalForm();
					var fileName = filePart.filename();
					
				return uploadToS3(filePart,fileName)
						.doOnSuccess( t -> log.debug("Файл загружен: {}", fileName))
						.doOnError( err -> log.debug("Ошибка загрузки файла: {}", fileName, err))
						.then(createFile(fileName, url))
						.flatMap(
								file -> createEvent(user.getId(), file.getId()).thenReturn(file)
						);
				}
		);
	}

	private Mono<PutObjectResult>  uploadToS3 (FilePart filePart, String fileName ) {
		return	DataBufferUtils.join(filePart.content()).map(
						dataBuffer -> s3client.putObject("s3testrest", fileName, dataBuffer.asInputStream(), new ObjectMetadata()));
	}

	private Mono<User> searchUser(Long userId) {
		return userRepository.findById(userId).
				switchIfEmpty(Mono.error(new NotFoundException("Юзер с id: " + userId + " не найден")));
	}

	private Mono<File> createFile(String fileName, String url) {
		var saveFile = File.builder().name(fileName).status(Status.ACTIVE).location(url).build();
		return fileRepository.save(saveFile)
				.doOnNext(file -> log.debug("Сохранен file с id: {}", file.getId()))
				.doOnError(error -> log.error("Ошибка сохранения file", error));
	}

	private Mono<Event> createEvent(Long userId, Long fileId) {
		var saveEvent = Event.builder().file_id(fileId).user_id(userId).status(Status.ACTIVE).build();
		return eventRepository.save(saveEvent)
				.doOnNext(event -> log.debug("Сохранен event с id: {}", event.getId()))
				.doOnError(error -> log.error("Ошибка сохранения event", error));
	}

}

