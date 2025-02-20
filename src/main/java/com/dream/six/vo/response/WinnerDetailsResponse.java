package com.dream.six.vo.response;


import lombok.Data;

import java.util.UUID;

@Data
public class WinnerDetailsResponse {
    private UUID id;
    private String winnerName;
    private Double winnerAmount;
    private TeamPlayerDetailsResponse teamPlayerDetailsResponse;
}