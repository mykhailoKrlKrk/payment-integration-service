package com.payment.integration.stripe.service;

import com.payment.integration.stripe.dto.PaymentResponseDto;
import com.payment.integration.stripe.dto.param.CreatePaymentParam;
import com.payment.integration.stripe.exception.ConflictException;
import com.payment.integration.stripe.exception.NotFoundException;
import com.payment.integration.stripe.mapper.PaymentMapper;
import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentStatus;
import com.payment.integration.stripe.repository.PaymentRepository;
import com.payment.integration.stripe.service.factory.PaymentProviderFactory;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentProviderFactory paymentProviderFactory;

    @Transactional
    public PaymentResponseDto createPayment(CreatePaymentParam param) {
        String idempotencyKey = resolveIdempotencyKey(param.idempotencyKey());

        return paymentRepository.findByIdempotencyKey(idempotencyKey)
                .map(paymentMapper::toPaymentResponseDto)
                .orElseGet(() -> create(param, idempotencyKey));
    }

    private PaymentResponseDto create(CreatePaymentParam param, String idempotencyKey) {
        Payment payment = paymentMapper.toModel(param);
        paymentMapper.setDefaults(param, payment, idempotencyKey);

        try {
            payment = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException ex) {
            return paymentRepository.findByIdempotencyKey(idempotencyKey)
                    .map(paymentMapper::toPaymentResponseDto)
                    .orElseThrow(() ->
                            new ConflictException(
                                    "Payment already exists for provided idempotencyKey"));
        }
        var strategy = paymentProviderFactory.getPaymentManagementStrategy(param.provider());
        PaymentResponseDto result = strategy.createPayment(payment, idempotencyKey);

        paymentMapper.applyProviderResult(payment, result, param.provider());
        return paymentMapper.toPaymentResponseDto(paymentRepository.save(payment));
    }

    private String resolveIdempotencyKey(String idempotencyKey) {
        return Optional.ofNullable(idempotencyKey)
                .filter(key -> !key.isBlank())
                .orElseGet(() -> String.valueOf(UUID.randomUUID()));
    }

    public PaymentStatus getPaymentStatus(UUID id) {
        if (paymentRepository.existsById(id)) {
            return paymentRepository.getPaymentStatus(id);
        }
        throw new NotFoundException("Failed: payment with provided id is not exist!");
    }

    public PaymentResponseDto getPayment(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toPaymentResponseDto)
                .orElseThrow(() -> new NotFoundException("Payment with provided id is not found"));
    }
}
