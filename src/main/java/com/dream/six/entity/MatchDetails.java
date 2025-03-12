package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "DS_MATCH_DETAILS")
public class MatchDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @Lob
    @Column(name = "MATCH_IMAGE")
    private byte[] matchImage;

    @Column(name = "MATCH_NAME")
    private String matchName;

    @Column(name = "MATCH_TIME")
    private String matchTime;

    @Column(name = "COUNTDOWN_START_TIME")
    private LocalDateTime countdownStartTime;

    @Column(name = "COUNTDOWN_END_TIME")
    private LocalDateTime countdownEndTime;

    @Column(name = "TEAM_1_NAME")
    private String teamOneName;

    @Column(name = "TEAM_2_NAME")
    private String teamTwoName;

    @Column(name = "MATCH_ACTION")
    private List<String> matchAction;

    @Column(name = "OPTION_COMPLETED")
    private String optionCompleted;

}
