package io.omosh.dts.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.omosh.dts.models.User;
import io.omosh.dts.utils.HelperUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("status")
    private String status;

    @JsonProperty("role")
    private String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.status = HelperUtil.toSentenceCase(user.getStatus().name());
        this.role = HelperUtil.toSentenceCase(user.getRole().name());
    }
}