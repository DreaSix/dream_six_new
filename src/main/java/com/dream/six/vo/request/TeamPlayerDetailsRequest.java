package com.dream.six.vo.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TeamPlayerDetailsRequest {

    private String teamName;
    private List<UUID> playerIds;
    private UUID matchId;

}
