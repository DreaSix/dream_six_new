package com.dream.six.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MatchPlayerDetailsResponse {

    private UUID id;
    private String teamName;
    private MatchDetailsResponse matchDetailsResponse;
    private List<PlayerDetailsResponse> playerDetailsResponseList;

}