package com.dream.six.service.impl;

import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.entity.*;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.mapper.ModelMapper;
import com.dream.six.repository.*;
import com.dream.six.service.WinnerDetailsService;
import com.dream.six.vo.request.WinnerDetailsRequest;
import com.dream.six.vo.response.MatchDetailsResponse;
import com.dream.six.vo.response.WinnerDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WinnerDetailsServiceImpl implements WinnerDetailsService {

    private final MatchDetailsRepository matchDetailsRepository;
    private final PlayerDetailsRepository playerDetailsRepository;
    private final WinnerDetailsRepository winnerDetailsRepository;
    private final UserInfoRepository userInfoRepository;
    private final TeamPlayerDetailsRepository teamPlayerDetailsRepository;
    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;
    private final BidRepository bidRepository;


    @Override
    public void createWinner(WinnerDetailsRequest request) throws Exception {
        Optional<MatchDetails> matchDetails = matchDetailsRepository.findById(request.getMatchId());
        Optional<PlayerDetails> playerDetails = playerDetailsRepository.findById(request.getPlayerId());

        if (matchDetails.isEmpty()){
            throw new Exception("Match is not found with id");
        }

        if (playerDetails.isEmpty()){
            throw new Exception("Player is not found with id");
        }
        WinnerDetails winnerDetails = buildWinnerDetails(request, matchDetails.get(), playerDetails.get());
        winnerDetailsRepository.save(winnerDetails);
    }

    @Override
    public List<WinnerDetailsResponse> getWinnerDetails() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<PlayerDetails> playerDetails = playerDetailsRepository.findAll();

        // Convert LocalDate to Timestamp for database query
        Timestamp startOfYesterday = Timestamp.valueOf(yesterday.atStartOfDay());
        Timestamp endOfToday = Timestamp.valueOf(today.atStartOfDay().plusDays(1).minusSeconds(1));

        // Fetch winner details within the date range
        List<WinnerDetails> winnerDetailsList = winnerDetailsRepository.findByCreatedAtBetween(startOfYesterday, endOfToday);

        List<BidEntity> bidEntities = bidRepository.findAll();
        // Map WinnerDetails to WinnerDetailsResponse
        return winnerDetailsList.stream()
                .map(winnerDetails -> {
                    return modelMapper.convertToWinnerDetailsResponse(winnerDetails, playerDetails, bidEntities);
                })
                .toList();
    }


    private WinnerDetails buildWinnerDetails(WinnerDetailsRequest request, MatchDetails matchDetails, PlayerDetails playerDetails) throws IOException {
        WinnerDetails winnerDetails = new WinnerDetails();
        UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, request.getUserId())));
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByCreatedByUUID(request.getUserId());
        if(optionalWalletEntity.isPresent()){
            WalletEntity walletEntity = optionalWalletEntity.get();
            walletEntity.setBalance(walletEntity.getBalance().add(request.getWinnerAmount()));
        }
        winnerDetails.setWinner(userInfo);
        winnerDetails.setWinnerAmount(request.getWinnerAmount());
        winnerDetails.setMatchDetails(matchDetails);
        winnerDetails.setPlayerDetails(playerDetails);
        return winnerDetails;
    }
}
