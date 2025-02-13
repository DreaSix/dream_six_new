package com.dream.six.api;

import com.dream.six.service.PlayerDetailsService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.PlayerDetailsRequest;
import com.dream.six.vo.request.TeamPlayerDetailsRequest;
import com.dream.six.vo.response.MatchPlayerDetailsResponse;
import com.dream.six.vo.response.PlayerDetailsResponse;
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<MatchPlayerDetailsResponse>>> getMatchPlayers(@PathVariable UUID id) {
        log.info("Received request to fetch match players for match ID: {}", id);

        List<MatchPlayerDetailsResponse> playerDetailsResponseList = playerDetailsService.getMatchTeamPlayers(id);

        ApiResponse<List<MatchPlayerDetailsResponse>> apiResponse = ApiResponse.<List<MatchPlayerDetailsResponse>>builder()
                .data(playerDetailsResponseList)
                .message("Match players fetched successfully")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
