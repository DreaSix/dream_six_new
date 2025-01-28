package com.dream.six.vo.response;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerDetailsResponse {

    private UUID playerId;

    private String playerName;

    private String countryName;

    private String playerImage;


}
