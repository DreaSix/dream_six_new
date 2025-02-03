package com.dream.six.vo.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponseVO {

    private UUID id;

    private String Name;

    private String phoneNumber;

    private List<RoleDetail> roles;

    private String createdAt;

    private String updatedAt;

    private WalletResponse walletResponse;

    @Data
    public static class WalletResponse {

        private UUID walletId;
        private BigDecimal balance;
        private BigDecimal netExposure;
    }
}
