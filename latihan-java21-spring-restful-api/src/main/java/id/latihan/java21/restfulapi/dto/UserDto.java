package id.latihan.java21.restfulapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String passwordConfirm;
    private Boolean status;
    private Long createdBy;
    private LocalDateTime createdDatetime;
    private Long modifiedBy;
    private LocalDateTime modifiedDatetime;

}
