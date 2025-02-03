package com.dream.six.api;

import com.dream.six.entity.BidEntity;
import com.dream.six.service.impl.MessageService;
import com.dream.six.vo.response.BidResponseDTO;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping("/createBid")
    public BidResponseDTO createBid(@RequestParam UUID matchId, @RequestParam UUID playerId) {
        return messageService.createBid(matchId, playerId);
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(BidMessageRequest request) {
        String username = MDC.get("username"); // Retrieve username from context

        try {
            // Call service to save the message and get the updated BidResponseDTO
            BidResponseDTO updatedBidResponse = messageService.saveMessage(request.getBidId(), username, request.getMessageContent());

            // Send the updated bid response to the public chatroom
            simpMessagingTemplate.convertAndSend("/chatroom/public", updatedBidResponse);
        } catch (RuntimeException e) {
            // Handle errors properly
            BidResponseDTO errorResponse = new BidResponseDTO();
            simpMessagingTemplate.convertAndSend("/chatroom/public", errorResponse);
        }
    }

    @MessageMapping("/getMatchMessages")
    public void getMatchMessages(BidMessageRequest request) {
        BidResponseDTO response = messageService.getMessages(request.getBidId());
        simpMessagingTemplate.convertAndSend("/chatroom/public", response);
    }

    // DTO for WebSocket message requests
    @Setter
    @Getter
    public static class BidMessageRequest {
        private UUID bidId;
        private String messageContent;

    }
}
