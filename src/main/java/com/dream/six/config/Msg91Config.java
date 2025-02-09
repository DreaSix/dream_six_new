package com.dream.six.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class Msg91Config {

    @Value("${msg91.authkey}")
    private String authKey;

    @Value("${msg91.widgetId}")
    private String widgetId;
}
