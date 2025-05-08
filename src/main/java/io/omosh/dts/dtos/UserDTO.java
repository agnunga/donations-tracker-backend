package io.omosh.dts.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.omosh.dts.models.User;
import io.omosh.dts.utils.HelperUtil;

public record UserDTO(@JsonProperty("id") Long id,
                      @JsonProperty("username") String username,
                      @JsonProperty("email") String email,
                      @JsonProperty("fullname") String fullname,
                      @JsonProperty("status") String status,
                      @JsonProperty("role") String role) {

    public static UserDTO from(User user) {
        return new UserDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname(),
                HelperUtil.toSentenceCase(user.getStatus().name()),
                HelperUtil.toSentenceCase(user.getRole().name()));
    }
}