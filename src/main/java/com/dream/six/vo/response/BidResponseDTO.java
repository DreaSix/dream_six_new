package com.dream.six.vo.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class BidResponseDTO {
    private UUID id;

    private UUID matchId;
    private UUID playerId;
    private List<MessageResponseDTO> responseDTOList;

    @Data
    public static class MessageResponseDTO{
        private UUID id;
        private String message;
        private String username;
        private LocalDateTime timestamp;

    }
}
