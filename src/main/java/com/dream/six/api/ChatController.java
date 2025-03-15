package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.enums.EntityFlag;
import com.dream.six.service.PlayerDetailsService;
import com.dream.six.service.impl.MessageService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.response.BidResponseDTO;
import com.dream.six.vo.response.TeamPlayerDetailsResponse;
import com.dream.six.vo.response.TransactionResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PlayerDetailsService playerDetailsService;
    private final SimpMessagingTemplate messagingTemplate;




    @PostMapping("/createBid")
    public BidResponseDTO createBid(@RequestParam UUID matchId, @RequestParam UUID playerId ,@RequestParam EntityFlag flag) throws Exception {
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

    @GetMapping("/chat/getMatchMessages/{bidId}") // Matches React publish destination
    public ResponseEntity<ApiResponse<BidResponseDTO>> getMatchMessages(@PathVariable UUID bidId) {
        log.info("Fetching messages for bidId: {}", bidId);

        // Retrieve messages
        BidResponseDTO response = messageService.getMessages(bidId);

        ApiResponse<BidResponseDTO> apiResponse = ApiResponse.<BidResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.TRANSACTION_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);

        // Send response back to client
//        simpMessagingTemplate.convertAndSend("/topic/public", response);
    }

    @MessageMapping("/teamPlayers/{matchId}")
    public void sendMatchTeamPlayers(@DestinationVariable UUID matchId) {
        try {
            List<TeamPlayerDetailsResponse> teamPlayers = playerDetailsService.getMatchTeamPlayers(matchId);
            messagingTemplate.convertAndSend("/topic/teamPlayers/" + matchId, teamPlayers);
        } catch (Exception e) {
            log.error("Error fetching match team players: {}", e.getMessage());
            messagingTemplate.convertAndSend("/topic/teamPlayers/" + matchId, "Error fetching match team players.");
        }
    }
    @Setter
    @Getter
    public static class BidMessageRequest {
        private UUID bidId;
        private String messageContent;
        private String username;
        private UUID userId;
    }
}
