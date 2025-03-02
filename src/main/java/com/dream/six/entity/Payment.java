package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DS_PAYMENTS")
@Data
public class Payment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;


    @Column(name = "PAYMENT_METHOD", nullable = false)
    private String paymentMethod; // BANK or UPI

    // Bank fields
    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;

    @Column(name = "IFSC_CODE")
    private String ifscCode;

    @Column(name = "BANK_NAME")
    private String bankName;

    // UPI fields
    @Column(name = "UPI_ID")
    private String upiId;

    @Column(name = "UPI_PHONE")
    private String upiPhone;

    @Lob
    @Column(name = "QR_CODE")
    private byte[] qrCode;


}
