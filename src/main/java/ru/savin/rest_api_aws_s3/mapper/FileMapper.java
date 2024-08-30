package ru.savin.rest_api_aws_s3.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.savin.rest_api_aws_s3.dto.FileDto;
import ru.savin.rest_api_aws_s3.model.File;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileDto map(File file);

    @InheritInverseConfiguration
    File map(FileDto fileDto);

}
