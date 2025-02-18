package com.dream.six.service.impl;

import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.PlayerDetails;
import com.dream.six.entity.WinnerDetails;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.mapper.ModelMapper;
import com.dream.six.repository.MatchDetailsRepository;
import com.dream.six.repository.PlayerDetailsRepository;
import com.dream.six.repository.WinnerDetailsRepository;
import com.dream.six.service.WinnerDetailsService;
import com.dream.six.vo.request.WinnerDetailsRequest;
import com.dream.six.vo.response.MatchDetailsResponse;
import com.dream.six.vo.response.WinnerDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WinnerDetailsServiceImpl implements WinnerDetailsService {

    private final MatchDetailsRepository matchDetailsRepository;
    private final PlayerDetailsRepository playerDetailsRepository;
    private final WinnerDetailsRepository winnerDetailsRepository;
    private final ModelMapper modelMapper;


    @Override
    public void createWinner(WinnerDetailsRequest request) throws Exception {
        MatchDetails matchDetails = matchDetailsRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match details not found with ID: " + request.getMatchId()));

        WinnerDetails winnerDetails = buildWinnerDetails(request, matchDetails);
        winnerDetailsRepository.save(winnerDetails);
    }

    @Override
    public List<WinnerDetailsResponse> getWinnerDetails() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Convert to Timestamp for database comparison
        Timestamp startOfYesterday = Timestamp.valueOf(yesterday.atStartOfDay());
        Timestamp endOfToday = Timestamp.valueOf(today.plusDays(1).atStartOfDay().minusSeconds(1));

        List<WinnerDetails> winnerDetailsList = winnerDetailsRepository.findByCreatedAtBetween(startOfYesterday, endOfToday);
        List<WinnerDetailsResponse> winnerDetailsResponseList = new ArrayList<>();

        List<MatchDetails> matchDetails = matchDetailsRepository.findAll();

        for (WinnerDetails winnerDetails: winnerDetailsList){
            WinnerDetailsResponse winnerDetailsResponse = new WinnerDetailsResponse();
            MatchDetails matchDetails1 = matchDetails.stream().filter(match -> match.getId().equals(winnerDetails.getMatchDetails().getId())).findFirst().get();
            winnerDetailsResponse.setId(winnerDetails.getId());
            winnerDetailsResponse.setWinnerAmount(winnerDetails.getWinnerAmount());
            winnerDetailsResponse.setWinnerName(winnerDetails.getWinnerName());
            winnerDetailsResponse.setMatchDetails(modelMapper.convertEntityToMatchDetailsResponse(matchDetails1));

            winnerDetailsResponseList.add(winnerDetailsResponse);

        }

        return winnerDetailsResponseList;
    }


    private WinnerDetails buildWinnerDetails(WinnerDetailsRequest request, MatchDetails matchDetails) throws IOException {
        WinnerDetails winnerDetails = new WinnerDetails();
        winnerDetails.setWinnerName(request.getWinnerName());
        winnerDetails.setWinnerAmount(request.getWinnerAmount());
        winnerDetails.setMatchDetails(matchDetails);
        return winnerDetails;
    }
}
