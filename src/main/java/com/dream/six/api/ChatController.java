package com.dream.six.api;

import com.dream.six.service.impl.MessageService;
import com.dream.six.vo.response.BidResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping("/createBid")
    public BidResponseDTO createBid(@RequestParam UUID matchId, @RequestParam UUID playerId) {
        log.info("Creating bid for matchId: {} and playerId: {}", matchId, playerId);
        return messageService.createBid(matchId, playerId);
    }

    @MessageMapping("/chat/sendMessage") // Matches React publish destination
    public void sendMessage(@Payload BidMessageRequest request) {
        log.info("Received message: {} for bidId: {}", request.getMessageContent(), request.getBidId());

        // Save message
        BidResponseDTO response = messageService.saveMessage(request.getBidId(), request.userId, request.getUsername(), request.getMessageContent());

        // Broadcast to all users
        simpMessagingTemplate.convertAndSend("/topic/public", response);
    }

    @MessageMapping("/chat/getMatchMessages") // Matches React publish destination
    public void getMatchMessages(@Payload BidMessageRequest request) {
        log.info("Fetching messages for bidId: {}", request.getBidId());

        // Retrieve messages
        BidResponseDTO response = messageService.getMessages(request.getBidId());

        // Send response back to client
        simpMessagingTemplate.convertAndSend("/topic/public", response);
    }

    @Setter
    @Getter
    public static class BidMessageRequest {
        private UUID bidId;
        private String messageContent;
        private String username;
        private String userId;
    }
}
