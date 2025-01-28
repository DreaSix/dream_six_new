package com.dream.six.api;

import com.dream.six.entity.MatchDetails;
import com.dream.six.service.MatchDetailsService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.MatchDetailsRequest;
import com.dream.six.vo.response.MatchDetailsResponse;
import com.dream.six.constants.ApiResponseMessages;
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
@RequestMapping("/api/match-details")
public class MatchDetailsController {

    private final MatchDetailsService matchDetailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<MatchDetails>> createMatchDetails(@RequestBody MatchDetailsRequest matchDetailsRequest) throws IOException {
        log.info("Received request to create match details: {}", matchDetailsRequest);
        MatchDetails matchDetails = matchDetailsService.saveMatchDetails(matchDetailsRequest);
        log.info("Match details created successfully with ID: {}", matchDetails.getId());
        ApiResponse<MatchDetails> apiResponse = ApiResponse.<MatchDetails>builder()
                .data(matchDetails)
                .message(ApiResponseMessages.MATCH_DETAILS_CREATED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MatchDetailsResponse>> getMatchDetailsById(@PathVariable UUID id) throws Exception {
        log.info("Received request to fetch match details with ID: {}", id);
        MatchDetailsResponse response = matchDetailsService.getMatchDetailsById(id);
        log.info("Fetched match details successfully with ID: {}", id);
        ApiResponse<MatchDetailsResponse> apiResponse = ApiResponse.<MatchDetailsResponse>builder()
                .data(response)
                .message(ApiResponseMessages.MATCH_DETAILS_FETCHED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MatchDetails>> updateMatchDetails(@PathVariable UUID id, @RequestBody MatchDetailsRequest matchDetailsRequest) {
        log.info("Received request to update match details with ID: {}", id);
        MatchDetails updatedMatchDetails = matchDetailsService.updateMatchDetails(id, matchDetailsRequest);
        log.info("Match details updated successfully with ID: {}", id);
        ApiResponse<MatchDetails> apiResponse = ApiResponse.<MatchDetails>builder()
                .data(updatedMatchDetails)
                .message(ApiResponseMessages.MATCH_DETAILS_UPDATED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMatchDetails(@PathVariable UUID id) {
        log.info("Received request to delete match details with ID: {}", id);
        matchDetailsService.deleteMatchDetails(id);
        log.info("Match details deleted successfully with ID: {}", id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(ApiResponseMessages.MATCH_DETAILS_DELETED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatchDetailsResponse>>> getAllMatchDetails() {
        log.info("Received request to fetch all match details");
        List<MatchDetailsResponse> matchDetailsList = matchDetailsService.getMatchDetails();
        log.info("Fetched all match details successfully");
        ApiResponse<List<MatchDetailsResponse>> apiResponse = ApiResponse.<List<MatchDetailsResponse>>builder()
                .data(matchDetailsList)
                .message(ApiResponseMessages.MATCH_DETAILS_FETCHED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
