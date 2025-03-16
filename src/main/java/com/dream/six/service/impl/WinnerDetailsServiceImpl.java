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
        MatchDetails matchDetails = matchDetailsRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("No match found with this ID"));
        Optional<PlayerDetails> playerDetails = playerDetailsRepository.findById(request.getPlayerId());
        List<TeamPlayerDetails> teamPlayerDetails = teamPlayerDetailsRepository.findByMatchDetails(matchDetails);

        Optional<TeamPlayerDetails> teamPlayerDetails1 = teamPlayerDetails.stream()
                .filter(team -> team.getPlayersDtoMap().containsKey(request.getPlayerId()))
                .findFirst();

        if (teamPlayerDetails1.isEmpty()){
            throw new Exception("Player is not assigned to team");

        }
        if (playerDetails.isEmpty()){
            throw new Exception("Player is not found with id");
        }
        WinnerDetails winnerDetails = buildWinnerDetails(request, matchDetails, playerDetails.get(), teamPlayerDetails1.get());
        winnerDetailsRepository.save(winnerDetails);
    }

    @Override
    public List<WinnerDetailsResponse> getWinnerDetails() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<PlayerDetails> playerDetails = playerDetailsRepository.findAll();

        Timestamp startOfYesterday = Timestamp.valueOf(yesterday.atStartOfDay());
        Timestamp endOfToday = Timestamp.valueOf(today.atStartOfDay().plusDays(1).minusSeconds(1));

        List<WinnerDetails> winnerDetailsList = winnerDetailsRepository.findByCreatedAtBetween(startOfYesterday, endOfToday);

        List<BidEntity> bidEntities = bidRepository.findAll();
        return winnerDetailsList.stream()
                .map(winnerDetails -> {
                    return modelMapper.convertToWinnerDetailsResponse(winnerDetails, playerDetails, bidEntities);
                })
                .toList();
    }


    private WinnerDetails buildWinnerDetails(WinnerDetailsRequest request, MatchDetails matchDetails, PlayerDetails playerDetails, TeamPlayerDetails teamPlayerDetails) throws IOException {
        WinnerDetails winnerDetails = new WinnerDetails();
        TeamPlayerDetails.PlayersDto playersDto = teamPlayerDetails.getPlayersDtoMap().get(playerDetails.getId());
            UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(playersDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, playersDto.getUserId())));
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByCreatedByUUID(playersDto.getUserId());
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
