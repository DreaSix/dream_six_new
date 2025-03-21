package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "DS_WINNER_DETAILS")
public class WinnerDetails  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @JoinColumn(name = "USER_ID")
    @ManyToOne
    private UserInfoEntity winner;

    @JoinColumn(name = "MATCH_ID")
    @ManyToOne
    private MatchDetails matchDetails;

    @Column(name = "WINNER_IMAGE")
    private byte[] winnerImage;

    @Column(name = "WINNER_AMOUNT")
    private BigDecimal winnerAmount;

    @JoinColumn(name = "PLAYER_ID")
    @ManyToOne
    private PlayerDetails playerDetails;
}
