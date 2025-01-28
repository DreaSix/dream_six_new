package com.dream.six.service.impl;

import com.dream.six.repository.MatchDetailsRepository;
import com.dream.six.repository.PlayerDetailsRepository;
import com.dream.six.repository.TeamPlayerDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamPlayerDetailsServiceImpl {

    @Autowired
    private MatchDetailsRepository matchDetailsRepository;

    @Autowired
    private PlayerDetailsRepository playerDetailsRepository;

    @Autowired
    private TeamPlayerDetailsRepository teamPlayerDetailsRepository;

}
