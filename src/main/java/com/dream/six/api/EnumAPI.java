package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.service.EnumService;
import com.dream.six.vo.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/enum")
@Slf4j
public class EnumAPI {

    private final EnumService enumService;

    @Autowired
    public EnumAPI(EnumService enumService) {
        this.enumService = enumService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String,List< Map<String, Object>>>>> getAllEnumValues() {
        log.info("Received request to retrieve all Enum Values.");
        Map<String, List<Map<String, Object>>> enums = enumService.getAllEnumValues();
        log.info("Retrieved all Enum Values successfully.");
        ApiResponse<Map<String, List<Map<String, Object>>>> response = ApiResponse.<Map<String, List<Map<String, Object>>>>builder()
                .data(enums)
                .message(ApiResponseMessages.ENUMS_VALUE_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

}
