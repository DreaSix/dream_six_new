package com.dream.six.service;

import com.dream.six.vo.request.PaymentRequestDTO;
import com.dream.six.vo.response.PaymentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO);

    PaymentResponseDTO getPaymentById(UUID id);

    PaymentResponseDTO updatePayment(UUID id, PaymentRequestDTO requestDTO);

    void deletePayment(UUID id);

    List<PaymentResponseDTO> getAllPayments();
}
