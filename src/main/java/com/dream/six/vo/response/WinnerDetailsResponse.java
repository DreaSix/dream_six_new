package com.dream.six.vo.response;


import lombok.Data;

@Data
public class WinnerDetailsResponse {
    private Integer id;
    private String winnerName;
    private MatchDetailsResponse matchDetails;
    private String winnerImage;
    private Double winnerAmount;
    private PlayerDetailsResponse playerDetailsResponse;
}