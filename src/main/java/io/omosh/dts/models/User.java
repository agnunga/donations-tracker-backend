package io.omosh.dts.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.omosh.dts.models.enums.Role;
import io.omosh.dts.models.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String fullname;
    private String email;
    private String password;
    private Status status;
    private Role role;

}
