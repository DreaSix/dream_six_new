package com.dream.six.api;

import com.dream.six.service.PlayerDetailsService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.PlayerDetailsRequest;
import com.dream.six.vo.request.TeamPlayerDetailsRequest;
import com.dream.six.vo.request.UpdatePlayerSoldPriceRequest;
import com.dream.six.vo.response.PlayerDetailsResponse;
import com.dream.six.vo.response.TeamPlayerDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/player-details")
public class PlayerDetailsController {

    private final PlayerDetailsService playerDetailsService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> savePlayerDetails(@ModelAttribute PlayerDetailsRequest playerDetailsRequest) throws IOException {
        log.info("Received request to save player details: {}", playerDetailsRequest);

        playerDetailsService.savePlayerDetails(playerDetailsRequest);
        log.info("Player details saved successfully");

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()

                .message("Player details saved successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<List<PlayerDetailsResponse>>> getPlayerDetails(@ModelAttribute PlayerDetailsRequest playerDetailsRequest) throws IOException {
        log.info("Received request to fetch player details: {}", playerDetailsRequest);

        List<PlayerDetailsResponse> playerDetailsResponseList = playerDetailsService.getPlayerDetails(playerDetailsRequest);
        log.info("Fetched player details successfully: {}", playerDetailsResponseList.size());

        ApiResponse<List<PlayerDetailsResponse>> apiResponse = ApiResponse.<List<PlayerDetailsResponse>>builder()

                .data(playerDetailsResponseList)
                .message("Player details fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/save-team")
    public ResponseEntity<ApiResponse<String>> saveTeamPlayerDetails(@RequestBody TeamPlayerDetailsRequest teamPlayerDetailsRequest) throws Exception {
        log.info("Received request to save team player details: {}", teamPlayerDetailsRequest);

        playerDetailsService.saveTeamPlayerDetails(teamPlayerDetailsRequest);
        log.info("Team player details saved successfully");

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Team player details saved successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{teamPlayerId}/updateSoldPrice")
    public ResponseEntity<ApiResponse<String>> updateSoldPrice(
            @PathVariable UUID teamPlayerId,
            @RequestBody UpdatePlayerSoldPriceRequest request) throws Exception {

        log.info("Received request to update sold price for teamPlayerId: {}, playerId: {}",
                teamPlayerId, request.getPlayerId());

        playerDetailsService.updateSoldPrice(teamPlayerId, request);

        log.info("Sold price updated successfully for playerId: {}", request.getPlayerId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message("Player sold price updated successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/{matchId}/matchDetails")
    public ResponseEntity<ApiResponse<List<TeamPlayerDetailsResponse>>> getTeamPlayerDetailsByMatch(@PathVariable UUID matchId) {
        log.info("Received request to fetch match players for match ID: {}", matchId);

        List<TeamPlayerDetailsResponse> playerDetailsResponseList = playerDetailsService.getMatchTeamPlayers(matchId);

        ApiResponse<List<TeamPlayerDetailsResponse>> apiResponse = ApiResponse.<List<TeamPlayerDetailsResponse>>builder()
                .data(playerDetailsResponseList)
                .message("Match players fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{teamPlayerId}")
    public ResponseEntity<ApiResponse<TeamPlayerDetailsResponse>> getTeamPlayerDetailsById(@PathVariable UUID teamPlayerId) {
        log.info("Received request to fetch match players for match ID: {}", teamPlayerId);

        TeamPlayerDetailsResponse playerDetailsResponse = playerDetailsService.getTeamPlayerDetailsById(teamPlayerId);

        ApiResponse<TeamPlayerDetailsResponse> apiResponse = ApiResponse.<TeamPlayerDetailsResponse>builder()
                .data(playerDetailsResponse)
                .message("Match players fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


}
