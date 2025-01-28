package com.dream.six.vo.response;

import lombok.Data;

import java.util.UUID;

@Data
public class MatchPlayerDetailsResponse {

    private UUID playerId;
    private String playerName;
    private String countryName;
    private String playerImage; // Base64 encoded image
    private String teamName;
    private UUID matchId;



}