package com.dream.six.service;



import com.dream.six.vo.request.PlayerDetailsRequest;
import com.dream.six.vo.request.TeamPlayerDetailsRequest;
import com.dream.six.vo.request.UpdatePlayerSoldPriceRequest;
import com.dream.six.vo.response.PlayerDetailsResponse;
import com.dream.six.vo.response.TeamPlayerDetailsResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface PlayerDetailsService {
    void savePlayerDetails(PlayerDetailsRequest playerDetailsRequest) throws IOException;

    List<PlayerDetailsResponse> getPlayerDetails(PlayerDetailsRequest playerDetailsRequest);

    void saveTeamPlayerDetails(TeamPlayerDetailsRequest teamPlayerDetailsRequest) throws Exception;
     void updateSoldPrice(UUID teamPlayerId, UpdatePlayerSoldPriceRequest request);
    List<TeamPlayerDetailsResponse> getMatchTeamPlayers(UUID id);

    TeamPlayerDetailsResponse getTeamPlayerDetailsById(UUID teamPlayerId);

    List<TeamPlayerDetailsResponse> getUserMatchBets(UUID userId);
}
