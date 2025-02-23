package com.dream.six.service.impl;

import com.dream.six.entity.*;
import com.dream.six.repository.*;
import com.dream.six.vo.response.BidResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final BidRepository bidRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserInfoRepository userInfoRepository;
    private final TeamPlayerDetailsRepository teamPlayerDetailsRepository;
    private final MatchDetailsRepository matchDetailsRepository;

    @Transactional
    public BidResponseDTO createBid(UUID matchId, UUID playerId) throws Exception {
        BidEntity existingBid = bidRepository.findByMatchIdAndPlayerId(matchId, playerId);
        Optional<MatchDetails> matchDetails = matchDetailsRepository.findById(matchId);

        if (matchDetails.isEmpty()) {
            throw new Exception("Match not found with this ID");
        }

        List<TeamPlayerDetails> teamPlayerDetailsList = teamPlayerDetailsRepository.findByMatchDetails(matchDetails.get());

        // Find the team that contains the player
        TeamPlayerDetails matchingTeam = teamPlayerDetailsList.stream()
                .filter(team -> team.getPlayersDtoMap().containsKey(playerId))
                .findFirst()
                .orElseThrow(() -> new Exception("Player not found in any team"));

        // Update player status to "BIDDING"
        TeamPlayerDetails.PlayersDto player = matchingTeam.getPlayersDtoMap().get(playerId);
        if (player != null) {
            player.setStatus("BIDDING");
            matchingTeam.getPlayersDtoMap().put(playerId, player);
            teamPlayerDetailsRepository.save(matchingTeam); // Save updated team details
        }

        // Create or update bid entity
        BidEntity bidEntity;
        if (existingBid == null) {
            bidEntity = new BidEntity();
            bidEntity.setMatchId(matchId);
            bidEntity.setPlayerId(playerId);
            bidRepository.save(bidEntity);
        } else {
            bidEntity = existingBid;
        }

        // Prepare response DTO
        BidResponseDTO bidResponseDTO = new BidResponseDTO();
        bidResponseDTO.setId(bidEntity.getId());
        bidResponseDTO.setMatchId(bidEntity.getMatchId());
        bidResponseDTO.setPlayerId(bidEntity.getPlayerId());

        return bidResponseDTO;
    }


    public BidResponseDTO saveMessage(UUID bidId, String username, String messageContent) {
        BidEntity existingBid = bidRepository.findById(bidId).orElseThrow(
                () -> new RuntimeException("No bid exists for bidId: " + bidId));

        List<UserInfoEntity> userInfoEntities = userInfoRepository.findAll();

        UserInfoEntity userInfoEntity = userInfoEntities.stream().filter(user -> Objects.equals(user.getUsername(), username)).findFirst().get();

        MessageDetails messageDetail = new MessageDetails();
        messageDetail.setName(userInfoEntity.getName());
        messageDetail.setBid(existingBid);
        messageDetail.setMessage(messageContent);
        messageDetail.setUsername(username);
        messageDetail.setTimestamp(LocalDateTime.now());

        messageRepository.save(messageDetail);


        messagingTemplate.convertAndSend("/topic/messages/" + bidId, messageDetail);
        List<MessageDetails> messageDetailsList = messageRepository.findByBidId(existingBid.getId());

        List<BidResponseDTO.MessageResponseDTO> messageResponseDTOList = messageDetailsList.stream()
                .map(messageDetails -> {
                    BidResponseDTO.MessageResponseDTO messageResponseDTO = new BidResponseDTO.MessageResponseDTO();
                    messageResponseDTO.setId(messageDetails.getId());
                    messageResponseDTO.setMessage(messageDetails.getMessage());
                    messageResponseDTO.setUsername(messageDetails.getUsername());
                    messageResponseDTO.setName(messageDetails.getName());
                    messageResponseDTO.setTimestamp(messageDetails.getTimestamp());
                    return messageResponseDTO;
                })
                .toList();

        BidResponseDTO bidResponseDTO = new BidResponseDTO();
        bidResponseDTO.setId(existingBid.getId());
        bidResponseDTO.setMatchId(existingBid.getMatchId());
        bidResponseDTO.setPlayerId(existingBid.getPlayerId());
        bidResponseDTO.setResponseDTOList(messageResponseDTOList);

        return bidResponseDTO;

          }

    @Transactional
    public BidResponseDTO getMessages(UUID bidId) {
        BidEntity entity = bidRepository.findById(bidId).orElseThrow(
                () -> new RuntimeException("No bid found with bidId: " + bidId));


        List<MessageDetails> messageDetailsList = messageRepository.findByBidId(entity.getId());

        List<BidResponseDTO.MessageResponseDTO> messageResponseDTOList = messageDetailsList.stream()
                .map(messageDetails -> {
                    BidResponseDTO.MessageResponseDTO messageResponseDTO = new BidResponseDTO.MessageResponseDTO();
                    messageResponseDTO.setId(messageDetails.getId());
                    messageResponseDTO.setMessage(messageDetails.getMessage());
                    messageResponseDTO.setUsername(messageDetails.getUsername());
                    messageResponseDTO.setTimestamp(messageDetails.getTimestamp());
                    messageResponseDTO.setName(messageDetails.getName());
                    return messageResponseDTO;
                })
                .toList();

        BidResponseDTO bidResponseDTO = new BidResponseDTO();
        bidResponseDTO.setId(entity.getId());
        bidResponseDTO.setMatchId(entity.getMatchId());
        bidResponseDTO.setPlayerId(entity.getPlayerId());
        bidResponseDTO.setResponseDTOList(messageResponseDTOList);

        return bidResponseDTO;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldMessages() {
        List<BidEntity> bids = bidRepository.findAll();

        for (BidEntity bid : bids) {
            LocalDateTime createdAt = bid.getCreatedAt().toLocalDateTime();

            if (createdAt.isBefore(LocalDateTime.now().minusDays(2))) {
                messageRepository.deleteByBidId(bid.getId());
                log.info("Deleted messages for bid with id: " + bid.getId());
            }
        }
        bids.removeIf(bid -> bid.getCreatedAt().toLocalDateTime().isBefore(LocalDateTime.now().minusDays(2)));

        bidRepository.deleteAll(bids);
    }

}
