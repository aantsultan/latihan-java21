package id.latihan.java21.restfulapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -4877854472840735136L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String password;
    private Boolean status;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime = LocalDateTime.now();
    @Column(name = "modified_by")
    private Long modifiedBy;
    @Column(name = "modified_datetime")
    private LocalDateTime modifiedDatetime;

}
