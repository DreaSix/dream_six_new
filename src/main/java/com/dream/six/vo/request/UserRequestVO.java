package com.dream.six.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserRequestVO {

    @NotBlank(message = " name is required")
    private String name;

    @Pattern(
            regexp = "^[+]?[0-9]{10,15}$",
            message = "Phone number must be valid, with 10-15 digits and optional '+'"
    )
    private String phoneNumber;

    private String password;

   
    private List<UUID> roles;

}
