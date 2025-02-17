package com.dream.six.api;

import com.dream.six.service.WinnerDetailsService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.WinnerDetailsRequest;
import com.dream.six.vo.response.MatchDetailsResponse;
import com.dream.six.vo.response.WinnerDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/winner-details")
@RequiredArgsConstructor
public class WinnerDetailsController {

    private final WinnerDetailsService winnerDetailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createWinner(@RequestBody WinnerDetailsRequest request) throws Exception {
        log.info("Received request to create winner: {}", request);

        winnerDetailsService.createWinner(request);
        log.info("Winner created successfully with name: {}", request.getWinnerName());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data("Winner created successfully with name: " + request.getWinnerName())
                .message("Success")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WinnerDetailsResponse>>> getWinnerDetails(){
        List<WinnerDetailsResponse> winnerDetailsResponses = winnerDetailsService.getWinnerDetails();

        ApiResponse<List<WinnerDetailsResponse>> apiResponse = ApiResponse.<List<WinnerDetailsResponse>>builder()
                .data(winnerDetailsResponses)
                .message("Success")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
