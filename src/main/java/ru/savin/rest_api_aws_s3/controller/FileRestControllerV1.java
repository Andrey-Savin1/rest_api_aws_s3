package ru.savin.rest_api_aws_s3.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.dto.FileDto;
import ru.savin.rest_api_aws_s3.dto.UserDto;
import ru.savin.rest_api_aws_s3.mapper.FileMapper;
import ru.savin.rest_api_aws_s3.mapper.UserMapper;
import ru.savin.rest_api_aws_s3.model.Event;
import ru.savin.rest_api_aws_s3.model.File;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.model.User;
import ru.savin.rest_api_aws_s3.s3.S3Service;
import ru.savin.rest_api_aws_s3.security.jwt.CustomPrincipal;
import ru.savin.rest_api_aws_s3.service.EventService;
import ru.savin.rest_api_aws_s3.service.FileService;
import ru.savin.rest_api_aws_s3.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileRestControllerV1 {


	private final FileService fileService;
	private final FileMapper fileMapper;
	private final S3Service s3Service;
	private final UserService userService;
	private final EventService eventService;
	private final UserMapper userMapper;


	//@PostMapping("file")
	public Mono<File> uploadFile(@PathVariable Long id, @RequestBody File file) {
		return fileService.save(file);
	}


	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
	public Mono<ResponseEntity<FileDto>> getFileById(@PathVariable("id") Long id) {
		return fileService
				.getById(id)
				.map(fileMapper::map)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
	public Flux<FileDto> getAllFiles() {
		return fileService.getAll().map(fileMapper::map);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('MODERATOR', 'ADMIN')")
	public Mono<ResponseEntity<Void>> deleteFileById(@PathVariable Long id) {
		return fileService
				.getById(id)
				.flatMap(
						user ->
								fileService.deleteById(user.getId())
										.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
										.switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
				);
	}


	@PostMapping(value = "/upload" )
	@PreAuthorize("hasAnyAuthority('USER', 'MODERATOR', 'ADMIN')")
	public  Mono<ResponseEntity<File>> uploadFile(Authentication authentication, @RequestPart("file") FilePart filePart) throws IOException {
		CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
		var userId = customPrincipal.getId();

		return fileService.uploadFiles(userId, filePart)
						.map(ResponseEntity::ok)
				.then(Mono.just(new ResponseEntity<File>(HttpStatus.NO_CONTENT)));

		}
	}



