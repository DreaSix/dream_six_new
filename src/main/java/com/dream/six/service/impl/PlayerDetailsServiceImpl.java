package com.dream.six.service.impl;

import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.PlayerDetails;
import com.dream.six.entity.TeamPlayerDetails;
import com.dream.six.exception.ResourceNotFoundException;
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

    @Override
    public void savePlayerDetails(PlayerDetailsRequest playerDetailsRequest) throws IOException {
        PlayerDetails playerDetails = new PlayerDetails();

        playerDetails.setPlayerName(playerDetailsRequest.getPlayerName());
        playerDetails.setCountryName(playerDetailsRequest.getCountryName());
        MultipartFile playerImage = playerDetailsRequest.getPlayerImage();
        if (playerImage != null && !playerImage.isEmpty()) {
            // Convert the image file to a byte array
            byte[] imageBytes = playerImage.getBytes();
            // Set the byte array to matchDetails
            playerDetails.setPlayerImage(imageBytes);
        }

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
        Optional<MatchDetails> optionalMatchDetails = matchDetailsRepository.findById(teamPlayerDetailsRequest.getMatchId());
        if (optionalMatchDetails.isEmpty()){
            throw new ResourceNotFoundException("No match is found with this id");
        }
        List<TeamPlayerDetails> teamPlayers = teamPlayerDetailsRepository.findByMatchDetailsAndTeamName(optionalMatchDetails.get(), teamPlayerDetailsRequest.getTeamName());
        teamPlayerDetailsRepository.deleteAll(teamPlayers);
        List<TeamPlayerDetails> teamPlayerDetailsList = new ArrayList<>();
        List<PlayerDetails> playerDetails = playerDetailsRepository.findAllByPlayerIds(teamPlayerDetailsRequest.getPlayerIds());
        MatchDetails matchDetails1 = optionalMatchDetails.get();
        for (PlayerDetails playerDetails1 : playerDetails){
            TeamPlayerDetails teamPlayerDetails = new TeamPlayerDetails();
            teamPlayerDetails.setTeamName(teamPlayerDetailsRequest.getTeamName());
            teamPlayerDetails.setPlayer(playerDetails1);
            teamPlayerDetails.setMatchDetails(matchDetails1);

            teamPlayerDetailsList.add(teamPlayerDetails);
        }

        teamPlayerDetailsRepository.saveAll(teamPlayerDetailsList);

    }

    @Override
    public List<MatchPlayerDetailsResponse> getMatchTeamPlayers(UUID id) {
        // Find match details by ID
        Optional<MatchDetails> matchDetailsOptional = matchDetailsRepository.findById(id);
        if (matchDetailsOptional.isEmpty()) {
            throw new ResourceNotFoundException("No match details present with this ID");
        }

        MatchDetails matchDetails = matchDetailsOptional.get();

        // Fetch team-player details for the match
        List<TeamPlayerDetails> teamPlayerDetailsList = teamPlayerDetailsRepository.findByMatchDetails(matchDetails);

        // Convert team-player details to response
        return teamPlayerDetailsList.stream().map(teamPlayer -> {
            PlayerDetails player = teamPlayer.getPlayer();

            MatchPlayerDetailsResponse response = new MatchPlayerDetailsResponse();
            response.setPlayerId(player.getId());
            response.setPlayerName(player.getPlayerName());
            response.setCountryName(player.getCountryName());
            response.setTeamName(teamPlayer.getTeamName());
            response.setPlayerImage(Base64.getEncoder().encodeToString(player.getPlayerImage()));
            response.setMatchId(id);
            return response;
        }).collect(Collectors.toList());
    }
}
