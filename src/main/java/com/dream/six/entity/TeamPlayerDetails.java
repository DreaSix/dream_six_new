package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "DS_TEAM_PLAYER_DETAILS")
public class TeamPlayerDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "TEAM_NAME")
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "MATCH_DETAILS", nullable = false)
    private MatchDetails matchDetails;

    @ManyToMany
    @JoinTable(
            name = "TEAM_PLAYER_MAPPING",
            joinColumns = @JoinColumn(name = "TEAM_PLAYER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PLAYER_ID")
    )
    private List<PlayerDetails> players;
}
