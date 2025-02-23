package com.dream.six.vo.response;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class TeamPlayerDetailsResponse {

    private UUID id;
    private String teamName;
    private MatchDetailsResponse matchDetailsResponse;
    private Map<UUID, PlayersDto> playersDtoMap;

    @Data
    public static class PlayersDto {
        private UUID playerId;
        private String playerName;
        private String playerImage;
        private UUID bidId;
        private String status;
        private Double basePrice;
        private Double soldPrice;
//        private UserResponseVO userResponseVO;
    }
}