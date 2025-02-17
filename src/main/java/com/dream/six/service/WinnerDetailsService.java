package com.dream.six.service;


import com.dream.six.vo.request.WinnerDetailsRequest;
import com.dream.six.vo.response.WinnerDetailsResponse;

import java.util.List;

public interface WinnerDetailsService {
    void createWinner(WinnerDetailsRequest request) throws Exception;

    List<WinnerDetailsResponse> getWinnerDetails();
}
