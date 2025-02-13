package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DS_WALLET")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "NET_EXPOSURE", nullable = false)
    private BigDecimal netExposure = BigDecimal.ZERO;


}
