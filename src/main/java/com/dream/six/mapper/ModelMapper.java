package com.dream.six.mapper;


import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.entity.*;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.vo.response.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.dream.six.mapper.CommonMapper.mapper;

@Component
public class ModelMapper {

    private final UserInfoRepository userInfoRepository;

    public ModelMapper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public MatchDetailsResponse convertEntityToMatchDetailsResponse(MatchDetails matchDetails){
        MatchDetailsResponse matchDetailsResponse = new MatchDetailsResponse();
        matchDetailsResponse.setMatchId(matchDetails.getId());
        matchDetailsResponse.setMatchName(matchDetails.getMatchName());
        matchDetailsResponse.setMatchTime(matchDetails.getMatchTime());
        matchDetailsResponse.setCountdownEndTime(matchDetails.getCountdownEndTime());
        matchDetailsResponse.setCountdownStartTime(matchDetails.getCountdownStartTime());
        matchDetailsResponse.setMatchAction(matchDetails.getMatchAction());
        matchDetailsResponse.setTeamOneName(matchDetails.getTeamOneName());
        matchDetailsResponse.setTeamTwoName(matchDetails.getTeamTwoName());
        matchDetailsResponse.setOptionCompleted(matchDetails.getOptionCompleted());
        byte[] imageBytes = matchDetails.getMatchImage();

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        matchDetailsResponse.setMatchImage(base64Image);
        return matchDetailsResponse;
    }

    public PlayerDetailsResponse convertEntityToPlayerDetailsResponse(PlayerDetails player) {
        PlayerDetailsResponse response = new PlayerDetailsResponse();
        response.setPlayerId(player.getId());
        response.setPlayerName(player.getPlayerName());
        response.setCountryName(player.getCountryName());
        response.setPlayerImage(Base64.getEncoder().encodeToString(player.getPlayerImage()));
        response.setBasePrice(player.getBasePrice());
        return response;
    }

    public TeamPlayerDetailsResponse convertToTeamPlayerDetailsResponse(
            TeamPlayerDetails teamPlayerDetails, List<PlayerDetails> playerDetailsList, List<BidEntity> bidEntities) {

        TeamPlayerDetailsResponse response = new TeamPlayerDetailsResponse();

            response.setId(teamPlayerDetails.getId()); // Use TeamPlayerDetails ID
            response.setTeamName(teamPlayerDetails.getTeamName());

            // Convert MatchDetails entity to response DTO
            response.setMatchDetailsResponse(
                    this.convertEntityToMatchDetailsResponse(teamPlayerDetails.getMatchDetails())
            );

        // Convert PlayersDtoMap properly with player details check & bidEntity
        response.setPlayersDtoMap(teamPlayerDetails.getPlayersDtoMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            UUID playerId = entry.getKey();  // Get player ID
                            TeamPlayerDetails.PlayersDto playerDto = entry.getValue();

                            // Find matching player details
                            PlayerDetails matchingPlayer = playerDetailsList.stream()
                                    .filter(player -> player.getPlayerName().equals(playerDto.getPlayerName()))
                                    .findFirst()
                                    .orElse(null);

                            // Find matching bid entity by playerId
                            BidEntity matchingBid = bidEntities.stream()
                                    .filter(bid -> bid.getPlayerId().equals(playerId))
                                    .findFirst()
                                    .orElse(null);

                            return mapToPlayersDto(playerDto, matchingPlayer, matchingBid);
                        }
                ))
        );

        return response;
    }



    /**
     * Maps PlayersDto entity to MatchPlayerDetailsResponse.PlayersDto DTO.
     */
    public TeamPlayerDetailsResponse.PlayersDto mapToPlayersDto(TeamPlayerDetails.PlayersDto playerEntity, PlayerDetails playerDetails, BidEntity bidEntity) {
        TeamPlayerDetailsResponse.PlayersDto dto = new TeamPlayerDetailsResponse.PlayersDto();
        dto.setPlayerName(playerEntity.getPlayerName());
        dto.setStatus(playerEntity.getStatus());
        byte[] imageBytes = playerDetails.getPlayerImage();

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        dto.setPlayerImage(base64Image);
        dto.setPlayerId(playerDetails.getId());
        dto.setBasePrice(playerEntity.getBasePrice());
        dto.setSoldPrice(playerEntity.getSoldPrice());
        if (bidEntity != null && !bidEntity.isDeleted()){
            dto.setBidId(bidEntity.getId());
        }
        if (playerEntity.getUserId() != null){
            UserInfoEntity userInfo =userInfoRepository.findById(playerEntity.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, playerEntity.getUserId())));
            UserResponseVO userResponseVO = mapper.convertUserInfoEntityToUserResponse(userInfo);
            userResponseVO.setName(userInfo.getName());
            dto.setUserResponseVO(userResponseVO);
        }
        return dto;
    }


    /**
     * Converts WinnerDetails entity to WinnerDetailsResponse DTO.
     */
    public WinnerDetailsResponse convertToWinnerDetailsResponse(WinnerDetails winnerDetails, List<PlayerDetails> playerDetails, List<BidEntity> bidEntities) {
        WinnerDetailsResponse response = new WinnerDetailsResponse();

        response.setId(winnerDetails.getId());
        UserResponseVO userResponseVO = mapper.convertUserInfoEntityToUserResponse(winnerDetails.getWinner());
        userResponseVO.setName(winnerDetails.getWinner().getName());
        response.setWinnerName(userResponseVO);
        response.setWinnerAmount(winnerDetails.getWinnerAmount());

        // Convert TeamPlayerDetails to response DTO
        if (winnerDetails.getMatchDetails() != null) {
            response.setMatchDetailsResponse(this.convertEntityToMatchDetailsResponse(winnerDetails.getMatchDetails()));
        }

        if (winnerDetails.getPlayerDetails() != null){
            response.setPlayerDetailsResponse(this.convertEntityToPlayerDetailsResponse(winnerDetails.getPlayerDetails()));
        }

        return response;
    }


}