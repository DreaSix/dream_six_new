package com.dream.six.service.impl;

import com.dream.six.entity.Payment;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.repository.PaymentRepository;
import com.dream.six.service.PaymentService;
import com.dream.six.vo.request.PaymentRequestDTO;
import com.dream.six.vo.response.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dream.six.mapper.CommonMapper.mapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) throws Exception {
        log.info("Creating a new payment: {}", requestDTO);

        Optional<Payment> optionalPayment = paymentRepository.findByPaymentMethod(requestDTO.getPaymentMethod());

        if (optionalPayment.isPresent()){
            throw new Exception("Payement already exist with method");
        }

        Payment payment = mapper.convertPaymentRequestToEntity(requestDTO);

        Payment savedPayment = paymentRepository.save(payment);

        return mapper.convertEntityToPaymentResponseDTO(savedPayment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(UUID id) {
        log.info("Fetching payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found for ID: {}", id);
                    return new RuntimeException("Payment not found for ID: " + id);
                });

        return mapper.convertEntityToPaymentResponseDTO(payment);
    }

    @Override
    public PaymentResponseDTO updatePayment(UUID id, PaymentRequestDTO requestDTO) {
        log.info("Updating payment with ID: {}", id);

        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment not found for ID: {}", id);
                    return new RuntimeException("Payment not found for ID: " + id);
                });

        existingPayment.setPaymentMethod(requestDTO.getPaymentMethod());
        existingPayment.setAccountName(requestDTO.getAccountName());
        existingPayment.setAccountNumber(requestDTO.getAccountNumber());
        existingPayment.setIfscCode(requestDTO.getIfscCode());
        existingPayment.setBankName(requestDTO.getBankName());
        existingPayment.setUpiId(requestDTO.getUpiId());
        existingPayment.setUpiPhone(requestDTO.getUpiPhone());
        existingPayment.setQrCodeUrl(requestDTO.getQrCodeUrl());

        Payment updatedPayment = paymentRepository.save(existingPayment);

        return mapper.convertEntityToPaymentResponseDTO(updatedPayment);
    }

    @Override
    public void deletePayment(UUID id) {
        log.info("Deleting payment with ID: {}", id);

        if (!paymentRepository.existsById(id)) {
            log.error("Payment not found for ID: {}", id);
            throw new ResourceNotFoundException("Payment not found for ID: " + id);
        }

        paymentRepository.deleteById(id);
        log.info("Payment with ID: {} has been successfully deleted", id);
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        log.info("Fetching all payments");

        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .map(mapper::convertEntityToPaymentResponseDTO)
                .toList();
    }
}
