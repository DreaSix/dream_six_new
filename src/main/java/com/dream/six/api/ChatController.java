package com.dream.six.api;

import com.dream.six.service.impl.MessageService;
import com.dream.six.vo.response.BidResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")  // Ensures messages are sent to the correct topic
    public BidResponseDTO sendMessage(@Payload BidMessageRequest request) {
        String username = MDC.get("username"); // Retrieve username from context
        if (username == null) {
            log.warn("Username is missing from MDC context for bidId: {}", request.getBidId());
            return new BidResponseDTO(); // Return an empty response
        }

        try {
            log.info("Received message from user {} for bidId: {}", username, request.getBidId());
            return messageService.saveMessage(request.getBidId(), username, request.getMessageContent());
        } catch (RuntimeException e) {
            log.error("Error saving message: {}", e.getMessage(), e);
            return new BidResponseDTO(); // Return an error response
        }
    }

    @MessageMapping("/getMatchMessages")
    @SendTo("/topic/public")  // Ensures messages are sent to the correct topic
    public BidResponseDTO getMatchMessages(@Payload BidMessageRequest request) {
        log.info("Fetching messages for bidId: {}", request.getBidId());
        return messageService.getMessages(request.getBidId());
    }

    @Setter
    @Getter
    public static class BidMessageRequest {
        private UUID bidId;
        private String messageContent;
    }
}
