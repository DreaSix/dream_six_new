package com.dream.six.vo.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequestVO {

    @NotNull(message = "Password cannot be null") @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotNull(message = "New Password cannot be null") @NotBlank(message = "New Password cannot be blank")
    private String newPassword;

    // Custom validation method for ensuring passwords are not equal
    @AssertTrue(message = "Password and New Password must be different")
    private boolean isPasswordsDifferent() {
        // Assuming case-sensitive comparison is desired, change to equalsIgnoreCase if case-insensitive comparison is needed
        return !password.equals(newPassword);
    }

}
