package io.omosh.dts.dtos;
import io.omosh.dts.models.enums.Role;
import io.omosh.dts.models.enums.Status;
import lombok.Data;

@Data
public class CreateUserDTO {

    private String username;
    private String fullname;
    private String password;
    private String email;
    private Status status;
    private Role role;

}