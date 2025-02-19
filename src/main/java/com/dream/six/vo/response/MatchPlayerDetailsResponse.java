package com.dream.six.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class MatchPlayerDetailsResponse {

    private UUID id;
    private String teamName;
    private MatchDetailsResponse matchDetailsResponse;
    private Map<UUID, PlayersDto> playersDtoMap;

    @Data
    public static class PlayersDto {
        private String playerName;
        private String status;
        private Double basePrice;
        private Double soldPrice;
    }
}