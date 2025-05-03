package io.omosh.dts.models;

import io.omosh.dts.models.enums.Role;
import io.omosh.dts.models.enums.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private Long id;

    private String username;
    private String fullname;
    private String email;
    private String password;
    private Status status;
    private Role role;

}
