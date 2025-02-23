package com.dream.six.mapper;


import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.entity.*;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.vo.response.MatchDetailsResponse;
import com.dream.six.vo.response.PlayerDetailsResponse;
import com.dream.six.vo.response.TeamPlayerDetailsResponse;
import com.dream.six.vo.response.WinnerDetailsResponse;
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
            TeamPlayerDetails teamPlayerDetails, List<PlayerDetails> playerDetailsList) {

        TeamPlayerDetailsResponse response = new TeamPlayerDetailsResponse();

        response.setId(teamPlayerDetails.getId()); // Use TeamPlayerDetails ID
        response.setTeamName(teamPlayerDetails.getTeamName());

        // Convert MatchDetails entity to response DTO
        response.setMatchDetailsResponse(
                this.convertEntityToMatchDetailsResponse(teamPlayerDetails.getMatchDetails())
        );

        // Convert PlayersDtoMap properly with player details check
        response.setPlayersDtoMap(teamPlayerDetails.getPlayersDtoMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            PlayerDetails matchingPlayer = playerDetailsList.stream()
                                    .filter(player -> player.getPlayerName().equals(entry.getValue().getPlayerName()))
                                    .findFirst()
                                    .orElse(null);

                            return mapToPlayersDto(entry.getValue(), matchingPlayer);
                        }
                ))
        );

        return response;
    }


    /**
     * Maps PlayersDto entity to MatchPlayerDetailsResponse.PlayersDto DTO.
     */
    public TeamPlayerDetailsResponse.PlayersDto mapToPlayersDto(TeamPlayerDetails.PlayersDto playerEntity, PlayerDetails playerDetails) {
        TeamPlayerDetailsResponse.PlayersDto dto = new TeamPlayerDetailsResponse.PlayersDto();
        dto.setPlayerName(playerEntity.getPlayerName());
        dto.setStatus(playerEntity.getStatus());
        byte[] imageBytes = playerDetails.getPlayerImage();

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        dto.setPlayerImage(base64Image);
        dto.setPlayerId(playerDetails.getId());
        dto.setBasePrice(playerEntity.getBasePrice());
        dto.setSoldPrice(playerEntity.getSoldPrice());
//        UserInfoEntity userInfo =userInfoRepository.findById(playerEntity.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, playerEntity.getUserId())));
//        dto.setUserResponseVO(mapper.convertUserInfoEntityToUserResponse(userInfo));
        return dto;
    }


    /**
     * Converts WinnerDetails entity to WinnerDetailsResponse DTO.
     */
    public WinnerDetailsResponse convertToWinnerDetailsResponse(WinnerDetails winnerDetails, List<PlayerDetails> playerDetails) {
        WinnerDetailsResponse response = new WinnerDetailsResponse();

        response.setId(winnerDetails.getId());
        response.setWinnerName(mapper.convertUserInfoEntityToUserResponse(winnerDetails.getWinner()));
        response.setWinnerAmount(winnerDetails.getWinnerAmount());

        // Convert TeamPlayerDetails to response DTO
        if (winnerDetails.getMatchDetails() != null) {
            response.setTeamPlayerDetailsResponse(this.convertToTeamPlayerDetailsResponse(winnerDetails.getMatchDetails(), playerDetails));
        }

        return response;
    }


}