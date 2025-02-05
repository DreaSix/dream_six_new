package com.dream.six.service.impl;

import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.PlayerDetails;
import com.dream.six.entity.TeamPlayerDetails;
import com.dream.six.enums.PlayerStatus;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.mapper.ModelMapper;
import com.dream.six.repository.MatchDetailsRepository;
import com.dream.six.repository.PlayerDetailsRepository;
import com.dream.six.repository.TeamPlayerDetailsRepository;
import com.dream.six.service.PlayerDetailsService;
import com.dream.six.vo.request.PlayerDetailsRequest;
import com.dream.six.vo.request.TeamPlayerDetailsRequest;
import com.dream.six.vo.response.MatchPlayerDetailsResponse;
import com.dream.six.vo.response.PlayerDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerDetailsServiceImpl implements PlayerDetailsService {

    private final PlayerDetailsRepository playerDetailsRepository;

    private final MatchDetailsRepository matchDetailsRepository;

    private final TeamPlayerDetailsRepository teamPlayerDetailsRepository;
    private final ModelMapper modelMapper;

    @Override
    public void savePlayerDetails(PlayerDetailsRequest playerDetailsRequest) throws IOException {
        PlayerDetails playerDetails = new PlayerDetails();

        playerDetails.setPlayerName(playerDetailsRequest.getPlayerName());
        playerDetails.setCountryName(playerDetailsRequest.getCountryName());
        MultipartFile playerImage = playerDetailsRequest.getPlayerImage();
        if (playerImage != null && !playerImage.isEmpty()) {
            byte[] imageBytes = playerImage.getBytes();
            playerDetails.setPlayerImage(imageBytes);
        }
        playerDetails.setStatus(String.valueOf(PlayerStatus.UN_SOLD));

        playerDetailsRepository.save(playerDetails);
    }

    @Override
    public List<PlayerDetailsResponse> getPlayerDetails(PlayerDetailsRequest playerDetailsRequest) {
        List<PlayerDetails> playerDetails = playerDetailsRepository.findAll();
        List<PlayerDetailsResponse> playerDetailsResponse = new ArrayList<>();
        for (PlayerDetails playerDetails1 : playerDetails){
            PlayerDetailsResponse playerDetailsResponse1 = new PlayerDetailsResponse();
            playerDetailsResponse1.setPlayerId(playerDetails1.getId());
            playerDetailsResponse1.setPlayerName(playerDetails1.getPlayerName());
            playerDetailsResponse1.setCountryName(playerDetails1.getCountryName());
            byte[] imageBytes = playerDetails1.getPlayerImage();

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            playerDetailsResponse1.setPlayerImage(base64Image);

            playerDetailsResponse.add(playerDetailsResponse1);
        }
        return playerDetailsResponse;
    }

    @Override
    public void saveTeamPlayerDetails(TeamPlayerDetailsRequest teamPlayerDetailsRequest) throws Exception {
        // Fetch match details
        MatchDetails matchDetails = matchDetailsRepository.findById(teamPlayerDetailsRequest.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("No match is found with this id"));


        List<PlayerDetails> playerDetailsList = playerDetailsRepository.findAllById(teamPlayerDetailsRequest.getPlayerIds());

        // Create a new TeamPlayerDetails entry
        TeamPlayerDetails teamPlayerDetails = new TeamPlayerDetails();
        teamPlayerDetails.setTeamName(teamPlayerDetailsRequest.getTeamName());
        teamPlayerDetails.setMatchDetails(matchDetails);
        teamPlayerDetails.setPlayers(playerDetailsList);

        // Save new team players
        teamPlayerDetailsRepository.save(teamPlayerDetails);
    }

    @Override
    public MatchPlayerDetailsResponse getMatchTeamPlayers(UUID id) {
        TeamPlayerDetails matchDetails = teamPlayerDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No match details present with this ID"));

        MatchPlayerDetailsResponse matchPlayerDetailsResponse = new MatchPlayerDetailsResponse();
        matchPlayerDetailsResponse.setId(matchDetails.getId());
        matchPlayerDetailsResponse.setTeamName(matchDetails.getTeamName());
        matchPlayerDetailsResponse.setMatchDetailsResponse(modelMapper.convertEntityToMatchDetailsResponse(matchDetails.getMatchDetails()));
        matchPlayerDetailsResponse.setPlayerDetailsResponseList(
                matchDetails.getPlayers().stream().map(
                        modelMapper :: convertEntityToPlayerDetailsResponse
                ).toList()
        );
        return matchPlayerDetailsResponse;
    }


}
