package com.dream.six.vo.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WinnerDetailsRequest {

    private String winnerName;
    private UUID matchId;
    private BigDecimal winnerAmount;
    private UUID userId;


}
