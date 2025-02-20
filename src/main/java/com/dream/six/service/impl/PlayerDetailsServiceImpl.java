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
import com.dream.six.vo.request.UpdatePlayerSoldPriceRequest;
import com.dream.six.vo.response.PlayerDetailsResponse;
import com.dream.six.vo.response.TeamPlayerDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        playerDetails.setBasePrice(playerDetailsRequest.getBasePrice());

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
            playerDetailsResponse1.setBasePrice(playerDetails1.getBasePrice());

            playerDetailsResponse.add(playerDetailsResponse1);
        }
        return playerDetailsResponse;
    }

    @Transactional
    @Override
    public void saveTeamPlayerDetails(TeamPlayerDetailsRequest teamPlayerDetailsRequest) {
        // Fetch match details
        MatchDetails matchDetails = matchDetailsRepository.findById(teamPlayerDetailsRequest.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("No match found with this ID"));

        // Delete previous entries for the same team in the match
        List<TeamPlayerDetails> existingTeamDetails = teamPlayerDetailsRepository.findByMatchDetails(matchDetails)
                .stream()
                .filter(item -> item.getTeamName().equals(teamPlayerDetailsRequest.getTeamName()))
                .toList();

        teamPlayerDetailsRepository.deleteAll(existingTeamDetails);

        // Fetch player details from database
        List<PlayerDetails> playerDetailsList = new ArrayList<>();
        if (teamPlayerDetailsRequest.getPlayerIds() != null && !teamPlayerDetailsRequest.getPlayerIds().isEmpty()) {
            playerDetailsList = playerDetailsRepository.findAllById(teamPlayerDetailsRequest.getPlayerIds());

            if (playerDetailsList.size() != teamPlayerDetailsRequest.getPlayerIds().size()) {
                throw new ResourceNotFoundException("Some players are missing from the database.");
            }
        }

        // Convert players to PlayersDto map
        Map<UUID, TeamPlayerDetails.PlayersDto> playersDtoMap = playerDetailsList.stream()
                .collect(Collectors.toMap(
                        PlayerDetails::getId,
                        player -> {
                            TeamPlayerDetails.PlayersDto dto = new TeamPlayerDetails.PlayersDto();
                            dto.setPlayerName(player.getPlayerName());
                            dto.setBasePrice(player.getBasePrice());
                            dto.setSoldPrice(0.0); // Default to zero
                            dto.setStatus("UNSOLD"); // Default status
                            return dto;
                        }
                ));

        // Save new team players
        TeamPlayerDetails newTeamPlayerDetails = new TeamPlayerDetails();
        newTeamPlayerDetails.setTeamName(teamPlayerDetailsRequest.getTeamName());
        newTeamPlayerDetails.setMatchDetails(matchDetails);
        newTeamPlayerDetails.setPlayersDtoMap(playersDtoMap);

        teamPlayerDetailsRepository.save(newTeamPlayerDetails);
    }

    @Transactional
    public void updateSoldPrice(UUID teamPlayerId, UpdatePlayerSoldPriceRequest request) {
        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(teamPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("No team player details found for this match ID"));

        // Update the specific player's sold price and status
        if (teamPlayerDetails.getPlayersDtoMap().containsKey(request.getPlayerId())) {
            TeamPlayerDetails.PlayersDto playerDto = teamPlayerDetails.getPlayersDtoMap().get(request.getPlayerId());
            playerDto.setSoldPrice(request.getSoldPrice());
            playerDto.setStatus("SOLD");
            playerDto.setUserId(request.getUserId());
            teamPlayerDetails.getPlayersDtoMap().put(request.getPlayerId(), playerDto);

            teamPlayerDetailsRepository.save(teamPlayerDetails);
        } else {
            throw new ResourceNotFoundException("Player not found in this team");
        }
    }
    @Override
    public List<TeamPlayerDetailsResponse> getMatchTeamPlayers(UUID id) {
        // Fetch match details
        MatchDetails matchDetails = matchDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No match details present with this ID"));

        // Fetch all team player details for the given match
        List<TeamPlayerDetails> playerMatchDetails = teamPlayerDetailsRepository.findByMatchDetails(matchDetails);

        return playerMatchDetails.stream().map(
                modelMapper::convertToTeamPlayerDetailsResponse
        ).toList();

            }

    @Override
    public TeamPlayerDetailsResponse getTeamPlayerDetailsById(UUID teamPlayerId) {
        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(teamPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("No team player details found for this match ID"));

        return modelMapper.convertToTeamPlayerDetailsResponse(teamPlayerDetails);
    }


}
