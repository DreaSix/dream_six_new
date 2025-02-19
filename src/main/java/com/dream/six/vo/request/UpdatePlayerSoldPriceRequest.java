package com.dream.six.vo.request;

import lombok.Data;
import java.util.UUID;

@Data
public class UpdatePlayerSoldPriceRequest {
    private UUID playerId;     // ID of the player being updated
    private double soldPrice;  // New sold price for the player
    private String status;     // New status of the player (e.g., "SOLD", "UNSOLD")
    private UUID userId;

}
