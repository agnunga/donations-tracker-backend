package io.omosh.donations.dtos;
import io.omosh.donations.models.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {

    private String username;
    private String fullname;
    private String password;
    private String email;
    private boolean status;
    private Set<Role> roles;

}