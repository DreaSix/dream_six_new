package com.dream.six.mapper;


import com.dream.six.entity.MatchDetails;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.vo.response.MatchDetailsResponse;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ModelMapper {

    private final UserInfoRepository userInfoRepository;

    public ModelMapper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public MatchDetailsResponse convertEntityToMatchDetailsResponse(MatchDetails matchDetails){
        MatchDetailsResponse matchDetailsResponse = new MatchDetailsResponse();
        matchDetailsResponse.setMatchId(matchDetails.getId());
        matchDetailsResponse.setMatchName(matchDetails.getMatchName());
        matchDetailsResponse.setMatchTime(matchDetails.getMatchTime());
        matchDetailsResponse.setCountdownEndTime(matchDetails.getCountdownEndTime());
        matchDetailsResponse.setCountdownStartTime(matchDetails.getCountdownStartTime());
        matchDetailsResponse.setMatchAction(matchDetails.getMatchAction());
        matchDetailsResponse.setTeamOneName(matchDetails.getTeamOneName());
        matchDetailsResponse.setTeamTwoName(matchDetails.getTeamTwoName());
        matchDetailsResponse.setOptionCompleted(matchDetails.getOptionCompleted());
        byte[] imageBytes = matchDetails.getMatchImage();

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        matchDetailsResponse.setMatchImage(base64Image);
        return matchDetailsResponse;
    }


}