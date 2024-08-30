package ru.savin.rest_api_aws_s3.repositiry;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.savin.rest_api_aws_s3.model.File;


public interface FileRepository extends R2dbcRepository<File, Long> {
}
