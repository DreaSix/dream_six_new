package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "PLAYER_DETAILS")
public class PlayerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;


    @Column(name = "PLAYER_NAME")
    private String playerName;

    @Column(name = "COUNTRY_NAME")
    private String countryName;

    @Lob
    @Column(name = "PLAYER_IMAGE")
    private byte[] playerImage;

    @Column(name = "STATUS")
    private String status;

}
