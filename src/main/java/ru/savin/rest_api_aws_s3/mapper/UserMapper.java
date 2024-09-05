package ru.savin.rest_api_aws_s3.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.savin.rest_api_aws_s3.dto.UserDto;
import ru.savin.rest_api_aws_s3.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto map(User user);
    @InheritInverseConfiguration
    User map(UserDto userDto);

}
