package com.dream.six.service.impl;

import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.PlayerDetails;
import com.dream.six.entity.TeamPlayerDetails;
import com.dream.six.entity.WinnerDetails;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.mapper.ModelMapper;
import com.dream.six.repository.MatchDetailsRepository;
import com.dream.six.repository.PlayerDetailsRepository;
import com.dream.six.repository.TeamPlayerDetailsRepository;
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
    private final TeamPlayerDetailsRepository teamPlayerDetailsRepository;
    private final ModelMapper modelMapper;


    @Override
    public void createWinner(WinnerDetailsRequest request) throws Exception {
        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("TeamPlayerDetails details not found with ID: " + request.getMatchId()));

        WinnerDetails winnerDetails = buildWinnerDetails(request, teamPlayerDetails);
        winnerDetailsRepository.save(winnerDetails);
    }

    @Override
    public List<WinnerDetailsResponse> getWinnerDetails() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Convert LocalDate to Timestamp for database query
        Timestamp startOfYesterday = Timestamp.valueOf(yesterday.atStartOfDay());
        Timestamp endOfToday = Timestamp.valueOf(today.atStartOfDay().plusDays(1).minusSeconds(1));

        // Fetch winner details within the date range
        List<WinnerDetails> winnerDetailsList = winnerDetailsRepository.findByCreatedAtBetween(startOfYesterday, endOfToday);

        // Map WinnerDetails to WinnerDetailsResponse
        return winnerDetailsList.stream()
                .map(modelMapper::convertToWinnerDetailsResponse)
                .toList();
    }


    private WinnerDetails buildWinnerDetails(WinnerDetailsRequest request, TeamPlayerDetails teamPlayerDetails) throws IOException {
        WinnerDetails winnerDetails = new WinnerDetails();
        winnerDetails.setWinnerName(request.getWinnerName());
        winnerDetails.setWinnerAmount(request.getWinnerAmount());
        winnerDetails.setMatchDetails(teamPlayerDetails);
        return winnerDetails;
    }
}
