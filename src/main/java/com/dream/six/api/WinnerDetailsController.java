package com.dream.six.api;

import com.dream.six.service.WinnerDetailsService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.WinnerDetailsRequest;
import com.dream.six.vo.response.WinnerDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/winner-details")
@RequiredArgsConstructor
public class WinnerDetailsController {

    private final WinnerDetailsService winnerDetailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createWinner(@ModelAttribute WinnerDetailsRequest request) throws Exception {
        log.info("Received request to create winner: {}", request);

        winnerDetailsService.createWinner(request);
        log.info("Winner created successfully with name: {}", request.getWinnerName());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data("Winner created successfully with name: " + request.getWinnerName())
                .message("Success")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
