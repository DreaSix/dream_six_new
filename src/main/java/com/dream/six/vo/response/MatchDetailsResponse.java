package com.dream.six.vo.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MatchDetailsResponse {

    private UUID matchId;
    private String matchImage;
    private String matchName;
    private String matchTime;
    private LocalDateTime countdownStartTime;
    private LocalDateTime countdownEndTime;
    private String teamOneName;
    private String teamTwoName;
    private List<String> matchAction;
    private String optionCompleted;

}
