package ru.savin.rest_api_aws_s3.service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.savin.rest_api_aws_s3.dto.FileDto;
import ru.savin.rest_api_aws_s3.model.File;

import java.io.IOException;
import java.io.InputStream;

public interface FileService extends GenericService<File, Long> {

	Mono<File> uploadFiles(Long id, FilePart filePart) throws IOException;
}
