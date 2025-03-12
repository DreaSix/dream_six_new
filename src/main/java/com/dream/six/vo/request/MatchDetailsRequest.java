package com.dream.six.vo.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
public class MatchDetailsRequest {

    private String matchName;
    private String matchTime;
    private String countDownEndTime;
    private String teamOneName;
    private String teamTwoName;
    private List<String> matchAction;
    private MultipartFile matchImage;
}
