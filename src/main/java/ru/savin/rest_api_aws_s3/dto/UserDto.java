package ru.savin.rest_api_aws_s3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.savin.rest_api_aws_s3.model.Status;
import ru.savin.rest_api_aws_s3.model.UserRole;

import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserDto {

    private Long Id;
    private String username;
    private Status status;
    private List<EventDto> events;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private UserRole role;
}
