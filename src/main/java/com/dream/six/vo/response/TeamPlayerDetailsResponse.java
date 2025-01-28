package com.dream.six.vo.response;


import com.dream.six.entity.PlayerDetails;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TeamPlayerDetailsResponse {
    private UUID matchId;
    private List<PlayerDetails> playerDetailsList;
}
