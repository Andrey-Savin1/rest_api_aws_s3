package ru.savin.rest_api_aws_s3.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.savin.rest_api_aws_s3.model.Status;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventDto {

    private Long Id;
    private Long userId;
    private Long fileId;
    private Status status;
}
