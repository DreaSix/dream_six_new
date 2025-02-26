package com.dream.six.vo.response;


import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WinnerDetailsResponse {
    private UUID id;
    private UserResponseVO winnerName;
    private BigDecimal winnerAmount;
    private MatchDetailsResponse matchDetailsResponse;
    private PlayerDetailsResponse playerDetailsResponse;
}