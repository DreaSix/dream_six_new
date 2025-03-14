package com.dream.six.service;


import com.dream.six.entity.MatchDetails;
import com.dream.six.vo.request.MatchDetailsRequest;
import com.dream.six.vo.response.MatchDetailsResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MatchDetailsService {
    MatchDetails saveMatchDetails(MatchDetailsRequest matchDetailsRequest) throws IOException;

    List<MatchDetailsResponse> getMatchDetails();

    MatchDetailsResponse getMatchDetailsById(UUID matchId) throws Exception;

    void deleteMatchDetails(UUID id);

    MatchDetails updateMatchDetails(UUID id, MatchDetailsRequest matchDetailsRequest);

    List<MatchDetailsResponse> getAuctionInCompleteData();

    void updateMatchDone(UUID id);
}
