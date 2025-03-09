package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "DS_TEAM_PLAYER_DETAILS")
public class TeamPlayerDetails extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "TEAM_NAME", nullable = false)
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "MATCH_ID", nullable = false)
    private MatchDetails matchDetails;

    @ElementCollection
    @CollectionTable(name = "DS_PLAYER_DETAILS", joinColumns = @JoinColumn(name = "TEAM_PLAYER_ID"))
    @MapKeyColumn(name = "PLAYER_ID")
    private Map<UUID, PlayersDto> playersDtoMap;

    @Data
    @Embeddable
    public static class PlayersDto implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Column(name = "PLAYER_NAME", nullable = false)
        private String playerName;

        @Column(name = "SOLD_DATE")
        private Date soldDate;

        @Column(name = "STATUS", nullable = false)
        private String status;

        @Column(name = "BASE_PRICE", nullable = false)
        private Double basePrice;

        @Column(name = "SOLD_PRICE")
        private Double soldPrice = 0.0;

        @Column(name = "USER_ID")
        private UUID userId;
    }
}
