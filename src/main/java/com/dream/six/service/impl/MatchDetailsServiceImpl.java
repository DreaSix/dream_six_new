package com.dream.six.service.impl;

import com.dream.six.entity.MatchDetails;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.mapper.ModelMapper;
import com.dream.six.repository.MatchDetailsRepository;
import com.dream.six.service.MatchDetailsService;
import com.dream.six.vo.request.MatchDetailsRequest;
import com.dream.six.vo.response.MatchDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchDetailsServiceImpl implements MatchDetailsService {

    private final MatchDetailsRepository matchDetailsRepository;

    private final ModelMapper modelMapper;

    @Override
    public MatchDetails saveMatchDetails(MatchDetailsRequest matchDetailsRequest) throws IOException {
        MatchDetails matchDetails = new MatchDetails();
        matchDetails.setMatchName(matchDetailsRequest.getMatchName());
        matchDetails.setMatchAction(matchDetailsRequest.getMatchAction());
        matchDetails.setTeamOneName(matchDetailsRequest.getTeamOneName());
        matchDetails.setTeamTwoName(matchDetailsRequest.getTeamTwoName());

        LocalDateTime countDownStartTime = LocalDateTime.now();
        matchDetails.setCountdownStartTime(countDownStartTime);

        String countDownEndTimeStr = matchDetailsRequest.getCountDownEndTime();  // Assuming the format is string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Adjust this format to match the request format
        LocalDateTime countDownEndTime = LocalDateTime.parse(countDownEndTimeStr, formatter);

        matchDetails.setCountdownEndTime(countDownEndTime);
        matchDetails.setMatchTime(matchDetailsRequest.getMatchTime());
        MultipartFile matchImage = matchDetailsRequest.getMatchImage();
        if (matchImage != null && !matchImage.isEmpty()) {
            // Convert the image file to a byte array
            byte[] imageBytes = matchImage.getBytes();
            // Set the byte array to matchDetails
            matchDetails.setMatchImage(imageBytes);
        }

        matchDetailsRepository.save(matchDetails);

        return matchDetails;
    }

    @Override
    public List<MatchDetailsResponse> getMatchDetails() {
        List<MatchDetails> matchDetails = matchDetailsRepository.findAll();
        return matchDetails.stream().map(
                modelMapper :: convertEntityToMatchDetailsResponse
        ).toList();
    }

    @Override
    public MatchDetailsResponse getMatchDetailsById(UUID matchId) throws Exception {
        Optional<MatchDetails> optionalMatchDetails = matchDetailsRepository.findById(matchId);

        if (optionalMatchDetails.isEmpty()){
            throw new ResourceNotFoundException("no match details found with this id");
        }
        MatchDetails matchDetails = optionalMatchDetails.get();

        return modelMapper.convertEntityToMatchDetailsResponse(matchDetails);
    }

    @Override
    public void deleteMatchDetails(UUID id) {
        Optional<MatchDetails> optionalMatchDetails = matchDetailsRepository.findById(id);

        if (optionalMatchDetails.isEmpty()){
            throw new ResourceNotFoundException("no match details found with this id");
        }
        MatchDetails matchDetails = optionalMatchDetails.get();
        matchDetailsRepository.delete(matchDetails);
    }

    @Override
    public MatchDetails updateMatchDetails(UUID id, MatchDetailsRequest matchDetailsRequest) {
        return null;
    }
}
