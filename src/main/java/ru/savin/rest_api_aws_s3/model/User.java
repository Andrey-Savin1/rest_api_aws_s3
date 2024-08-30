package ru.savin.rest_api_aws_s3.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;


import java.util.List;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User {

    @Id
    private Long Id;
    private String username;
    private Status status;
    @Transient
    @ToString.Exclude
    private List<Event> events;
    private String password;
    private UserRole role;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
