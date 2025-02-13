package com.dream.six.service.impl;

import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.PlayerDetails;
import com.dream.six.entity.WinnerDetails;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.repository.MatchDetailsRepository;
import com.dream.six.repository.PlayerDetailsRepository;
import com.dream.six.repository.WinnerDetailsRepository;
import com.dream.six.service.WinnerDetailsService;
import com.dream.six.vo.request.WinnerDetailsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class WinnerDetailsServiceImpl implements WinnerDetailsService {

    private final MatchDetailsRepository matchDetailsRepository;
    private final PlayerDetailsRepository playerDetailsRepository;
    private final WinnerDetailsRepository winnerDetailsRepository;

    @Override
    public void createWinner(WinnerDetailsRequest request) throws Exception {
        MatchDetails matchDetails = matchDetailsRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match details not found with ID: " + request.getMatchId()));

        PlayerDetails playerDetails = playerDetailsRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player details not found with ID: " + request.getPlayerId()));

        WinnerDetails winnerDetails = buildWinnerDetails(request, matchDetails, playerDetails);
        winnerDetailsRepository.save(winnerDetails);
    }


    private WinnerDetails buildWinnerDetails(WinnerDetailsRequest request, MatchDetails matchDetails, PlayerDetails playerDetails) throws IOException {
        WinnerDetails winnerDetails = new WinnerDetails();
        winnerDetails.setWinnerName(request.getWinnerName());
        winnerDetails.setWinnerAmount(request.getWinnerAmount());
        winnerDetails.setMatchDetails(matchDetails);
        winnerDetails.setPlayerDetails(playerDetails);
        return winnerDetails;
    }
}
