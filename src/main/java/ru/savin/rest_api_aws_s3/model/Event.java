package ru.savin.rest_api_aws_s3.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("events")
public class Event {

    @Id
    private Long Id;
    private Long user_id;
    private Long file_id;
    private Status status;

}
