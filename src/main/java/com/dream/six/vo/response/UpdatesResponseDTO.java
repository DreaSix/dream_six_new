package com.dream.six.vo.response;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdatesResponseDTO {
    private UUID id;
    private String updateText;
    private Timestamp date;
}
