package io.omosh.donations.dtos;
import io.omosh.donations.models.Role;
import io.omosh.donations.models.Status;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {

    private String username;
    private String fullname;
    private String password;
    private String email;
    private Status status;
    private Set<Role> roles;

}