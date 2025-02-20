package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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


    @Column(name = "WINNER_NAME")
    private String winnerName;

    @JoinColumn(name = "TEAM_PLAYER_ID")
    @ManyToOne
    private TeamPlayerDetails matchDetails;

    @Column(name = "WINNER_IMAGE")
    private byte[] winnerImage;

    @Column(name = "WINNER_AMOUNT")
    private Double winnerAmount;



}
