package com.dream.six.entity;

import com.dream.six.enums.Status;
import com.dream.six.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DS_TRANSACTIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Lob
    @Column(name = "TRANSACTION_IMAGE")
    private byte[] image;

    @Column(name = "UTR", unique = true, nullable = false)
    private String utr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "APPROVAL_STATUS", nullable = false)
    private Status approvalStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WITHDRAW_BANK_ID")
    private WithdrawBankEntity withdrawBank;

}
