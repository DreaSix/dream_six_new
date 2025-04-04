package com.dream.six.service.impl;

import com.dream.six.entity.*;
import com.dream.six.enums.PlayerStatus;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.mapper.ModelMapper;
import com.dream.six.repository.*;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerDetailsServiceImpl implements PlayerDetailsService {

    private final PlayerDetailsRepository playerDetailsRepository;

    private final MatchDetailsRepository matchDetailsRepository;

    private final TeamPlayerDetailsRepository teamPlayerDetailsRepository;
    private  final WalletRepository walletRepository;
    private final ModelMapper modelMapper;
    private final BidRepository bidRepository;

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
        playerDetails.setBasePrice(1000);

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
                            dto.setBasePrice(1000.0);
//                            dto.setBasePrice(player.getBasePrice());
                            dto.setSoldPrice(0.0); // Default to zero
                            dto.setStatus("UNSOLD"); // Default status
                            return dto;
                        }
                ));


        TeamPlayerDetails newTeamPlayerDetails = new TeamPlayerDetails();
        newTeamPlayerDetails.setTeamName(teamPlayerDetailsRequest.getTeamName());
        newTeamPlayerDetails.setMatchDetails(matchDetails);
        newTeamPlayerDetails.setPlayersDtoMap(playersDtoMap);

        teamPlayerDetailsRepository.save(newTeamPlayerDetails);
    }

    @Override
    public void updateUnsoldPlayer(UUID teamPlayerId, UUID playerId) {
        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(teamPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("No team player details found for teamPlayerId: " + teamPlayerId));

        // Validate if player exists in the team
        TeamPlayerDetails.PlayersDto playerDto = teamPlayerDetails.getPlayersDtoMap().get(playerId);
        if (playerDto == null) {
            throw new ResourceNotFoundException("Player with ID " + playerId + " not found in the team.");
        }

        playerDto.setStatus("UN_SOLD");
        teamPlayerDetails.getPlayersDtoMap().put(playerId, playerDto);

        // Save updated team player details
        teamPlayerDetailsRepository.save(teamPlayerDetails);

    }

    @Transactional
    @Override
    public void updateSoldPrice(UUID teamPlayerId, UpdatePlayerSoldPriceRequest request) {
        // Fetch team player details
        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(teamPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("No team player details found for teamPlayerId: " + teamPlayerId));

        // Validate if player exists in the team
        TeamPlayerDetails.PlayersDto playerDto = teamPlayerDetails.getPlayersDtoMap().get(request.getPlayerId());
        if (playerDto == null) {
            throw new ResourceNotFoundException("Player with ID " + request.getPlayerId() + " not found in the team.");
        }

        List<WalletEntity> walletEntities = walletRepository.findAll();

        // Fetch user wallet
        Optional<WalletEntity> optionalWalletEntity = walletEntities.stream().filter(wallet -> wallet.getCreatedByUUID().equals(request.getUserId())).findFirst();

        if (optionalWalletEntity.isEmpty()){
            throw new ResourceNotFoundException("No wallet found for userId: " + request.getUserId());
        }

        WalletEntity walletEntity = optionalWalletEntity.get();

        BigDecimal soldPrice = BigDecimal.valueOf(request.getSoldPrice());
        if (walletEntity.getBalance().compareTo(soldPrice) < 0) {
            throw new ResourceNotFoundException("Insufficient balance. Available: " + walletEntity.getBalance() + ", Required: " + soldPrice);
        }

        // Deduct the sold price from the wallet balance and update net exposure
        walletEntity.setBalance(walletEntity.getBalance().subtract(soldPrice));
        walletEntity.setNetExposure(walletEntity.getNetExposure().add(soldPrice));
        walletRepository.save(walletEntity);

        // Update player details
        playerDto.setSoldPrice(request.getSoldPrice());
        playerDto.setStatus("SOLD");
        playerDto.setUserId(request.getUserId());
        playerDto.setSoldDate(new Date());
        teamPlayerDetails.getPlayersDtoMap().put(request.getPlayerId(), playerDto);

        // Save updated team player details
        teamPlayerDetailsRepository.save(teamPlayerDetails);
    }

    @Transactional
    @Override
    public void cancelSoldPlayer(UUID teamPlayerId, UUID playerId) {
        // Fetch team player details
        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(teamPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("No team player details found for teamPlayerId: " + teamPlayerId));

        // Validate if player exists in the team
        TeamPlayerDetails.PlayersDto playerDto = teamPlayerDetails.getPlayersDtoMap().get(playerId);
        if (playerDto == null || !"SOLD".equals(playerDto.getStatus())) {
            throw new ResourceNotFoundException("Player with ID " + playerId + " is not sold or does not exist.");
        }

        List<WalletEntity> walletEntities = walletRepository.findAll();

        // Fetch user wallet
        Optional<WalletEntity> optionalWalletEntity = walletEntities.stream()
                .filter(wallet -> wallet.getCreatedByUUID().equals(playerDto.getUserId()))
                .findFirst();

        if (optionalWalletEntity.isEmpty()) {
            throw new ResourceNotFoundException("No wallet found for userId: " + playerDto.getUserId());
        }

        WalletEntity walletEntity = optionalWalletEntity.get();

        BigDecimal soldPrice = BigDecimal.valueOf(playerDto.getSoldPrice());

        // Refund the sold price back to wallet and adjust net exposure
        walletEntity.setBalance(walletEntity.getBalance().add(soldPrice));
        walletEntity.setNetExposure(walletEntity.getNetExposure().subtract(soldPrice));
        walletRepository.save(walletEntity);

        // Mark player as "CANCELLED"
        playerDto.setStatus("CANCELLED");
        playerDto.setSoldPrice(0.0);
        playerDto.setUserId(null);
        playerDto.setSoldDate(null);

        teamPlayerDetails.getPlayersDtoMap().put(playerId, playerDto);

        // Save updated team player details
        teamPlayerDetailsRepository.save(teamPlayerDetails);
    }


    @Override
    public List<TeamPlayerDetailsResponse> getMatchTeamPlayers(UUID id) {
        // Fetch match details
        MatchDetails matchDetails = matchDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No match details present with this ID"));

        // Fetch all team player details for the given match
        List<TeamPlayerDetails> playerMatchDetails = teamPlayerDetailsRepository.findByMatchDetails(matchDetails);
        List<BidEntity> bidDetails = bidRepository.findAll();
        List<PlayerDetails> playerDetailsList = playerDetailsRepository.findAll();
        return playerMatchDetails.stream().map(details -> {
            return modelMapper.convertToTeamPlayerDetailsResponse(details, playerDetailsList, bidDetails);
        }).toList();

            }

    @Override
    public TeamPlayerDetailsResponse getTeamPlayerDetailsById(UUID teamPlayerId) {
        List<PlayerDetails> playerDetailsList = playerDetailsRepository.findAll();
        List<BidEntity> bidDetails = bidRepository.findAll();

        TeamPlayerDetails teamPlayerDetails = teamPlayerDetailsRepository.findById(teamPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("No team player details found for this match ID"));

        return modelMapper.convertToTeamPlayerDetailsResponse(teamPlayerDetails, playerDetailsList, bidDetails);
    }

    @Override
    public List<TeamPlayerDetailsResponse> getUserMatchBets(UUID userId) {
        List<TeamPlayerDetails> teamPlayerDetailsList = teamPlayerDetailsRepository.findAll();
        List<PlayerDetails> playerDetailsList = playerDetailsRepository.findAll(); // Fetch all player details

        List<TeamPlayerDetailsResponse> responseList = new ArrayList<>();

        teamPlayerDetailsList.forEach(team -> {
            Map<UUID, TeamPlayerDetailsResponse.PlayersDto> playersDtoMap = new HashMap<>();

            team.getPlayersDtoMap().forEach((playerId, player) -> {
                if (player.getUserId() != null && player.getUserId().equals(userId)) {
                    TeamPlayerDetailsResponse.PlayersDto dto = new TeamPlayerDetailsResponse.PlayersDto();
                    dto.setPlayerId(playerId);
                    dto.setPlayerName(player.getPlayerName());
                    dto.setStatus(player.getStatus());
                    dto.setBasePrice(player.getBasePrice());
                    dto.setSoldPrice(player.getSoldPrice());
                    dto.setSoldDate(player.getSoldDate());
                    dto.setUserId(player.getUserId());
                    dto.setBidId(player.getUserId()); // If `bidId` is different, replace accordingly.

                    // Find the player's image using playerId
                    playerDetailsList.stream()
                            .filter(p -> p.getId().equals(playerId))
                            .findFirst()
                            .ifPresent(p -> {
                                byte[] imageBytes = p.getPlayerImage();
                                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                                dto.setPlayerImage(base64Image);
                            });

                    playersDtoMap.put(playerId, dto);
                }
            });

            // Only add teams where the user has placed a bet
            if (!playersDtoMap.isEmpty()) {
                TeamPlayerDetailsResponse response = new TeamPlayerDetailsResponse();
                response.setId(team.getId());
                response.setTeamName(team.getTeamName());
                response.setMatchDetailsResponse(modelMapper.convertEntityToMatchDetailsResponse(team.getMatchDetails())); // Convert MatchDetails to Response DTO
                response.setPlayersDtoMap(playersDtoMap);

                responseList.add(response);
            }
        });

        return responseList;
    }




}
