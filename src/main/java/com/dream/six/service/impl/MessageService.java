package com.dream.six.service.impl;

import com.dream.six.entity.BidEntity;
import com.dream.six.entity.MessageDetails;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.entity.WalletEntity;
import com.dream.six.repository.BidRepository;
import com.dream.six.repository.MessageRepository;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.repository.WalletRepository;
import com.dream.six.vo.response.BidResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final WalletRepository walletRepository;

    @Transactional
    public BidResponseDTO createBid(UUID matchId, UUID playerId) {
        BidEntity existingBid = bidRepository.findByMatchIdAndPlayerId(matchId, playerId);

        BidEntity bidEntity;

        if (existingBid == null) {
            BidEntity newBid = new BidEntity();
            newBid.setMatchId(matchId);
            newBid.setPlayerId(playerId);
            bidEntity = bidRepository.save(newBid);
        } else {
            bidEntity = existingBid;
        }


        BidResponseDTO bidResponseDTO = new BidResponseDTO();
        bidResponseDTO.setId(bidEntity.getId());
        bidResponseDTO.setMatchId(bidEntity.getMatchId());
        bidResponseDTO.setPlayerId(bidEntity.getPlayerId());

        return bidResponseDTO;
    }

    public BidResponseDTO saveMessage(UUID bidId, String username, UUID userId, String messageContent) {
        // Retrieve the existing bid entity
        BidEntity existingBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("No bid exists for bidId: " + bidId));

        // Fetch user information
        UserInfoEntity userInfoEntity = userInfoRepository.findByUserNameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Check if user is a regular user and validate wallet balance
        if (isUserRole(userInfoEntity)) {
            validateWalletBalance(userId, messageContent);
        }

        // Create and save message detail
        MessageDetails messageDetail = new MessageDetails();
        messageDetail.setName(userInfoEntity.getName());
        messageDetail.setBid(existingBid);
        messageDetail.setMessage(messageContent);
        messageDetail.setUsername(username);
        messageDetail.setTimestamp(LocalDateTime.now());

        messageRepository.save(messageDetail);

        // Send message to websocket
        messagingTemplate.convertAndSend("/topic/messages/" + bidId, messageDetail);

        // Retrieve and map messages for response
        List<BidResponseDTO.MessageResponseDTO> messageResponseDTOList = messageRepository.findByBidId(existingBid.getId())
                .stream()
                .map(this::mapToMessageResponseDTO)
                .toList();

        // Create final bid response DTO
        return buildBidResponse(existingBid, messageResponseDTOList);
    }

    // Helper method to check if the user has "USER" role
    private boolean isUserRole(UserInfoEntity userInfoEntity) {
        return userInfoEntity.getRoles() != null && userInfoEntity.getRoles().contains("USER");
    }

    // Helper method to validate wallet balance
    private void validateWalletBalance(UUID userId, String messageContent) {
        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByCreatedByUUID(userId);
        WalletEntity walletEntity = optionalWalletEntity
                .orElseThrow(() -> new RuntimeException("No wallet found for userId: " + userId));

        BigDecimal amount = extractAmountFromMessage(messageContent);
        if (amount != null && amount.compareTo(walletEntity.getBalance()) > 0) {
            throw new RuntimeException("Insufficient balance. Available: " + walletEntity.getBalance() + ", Requested: " + amount);
        }
    }

    // Helper method to extract amount from message content
    private BigDecimal extractAmountFromMessage(String messageContent) {
        if (messageContent != null && messageContent.contains("amount:")) {
            try {
                String amountStr = messageContent.substring(messageContent.indexOf("amount:") + 7).trim();
                return new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid amount format in message content");
            }
        }
        return null;
    }

    // Helper method to map MessageDetails to MessageResponseDTO
    private BidResponseDTO.MessageResponseDTO mapToMessageResponseDTO(MessageDetails messageDetails) {
        BidResponseDTO.MessageResponseDTO messageResponseDTO = new BidResponseDTO.MessageResponseDTO();
        messageResponseDTO.setId(messageDetails.getId());
        messageResponseDTO.setMessage(messageDetails.getMessage());
        messageResponseDTO.setUsername(messageDetails.getUsername());
        messageResponseDTO.setName(messageDetails.getName());
        messageResponseDTO.setTimestamp(messageDetails.getTimestamp());
        return messageResponseDTO;
    }

    // Helper method to build BidResponseDTO
    private BidResponseDTO buildBidResponse(BidEntity existingBid, List<BidResponseDTO.MessageResponseDTO> messageResponseDTOList) {
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
