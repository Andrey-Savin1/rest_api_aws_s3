package ru.savin.rest_api_aws_s3.model;



import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("file")
public class File {

    @Id
    private Long Id;
    private String location;
    private String name;
    private Status status;

}
