package com.dream.six.vo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PlayerDetailsRequest {

    private String playerName;

    private String countryName;

    private MultipartFile playerImage;

    private double basePrice;



}
