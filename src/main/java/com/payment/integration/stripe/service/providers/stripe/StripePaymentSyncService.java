package com.payment.integration.stripe.service.providers.stripe;

import com.payment.integration.stripe.exception.InternalErrorException;
import com.payment.integration.stripe.mapper.StripePaymentStatusMapper;
import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentProvider;
import com.payment.integration.stripe.model.enums.PaymentStatus;
import com.payment.integration.stripe.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentSyncService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void syncByProviderPaymentId(PaymentProvider provider, String providerPaymentId) {
        Payment payment =
                paymentRepository.findByProviderAndProviderPaymentId(provider, providerPaymentId)
                        .orElseThrow(() -> new IllegalStateException(
                                "Payment not found for providerPaymentId=" + providerPaymentId));

        if (isFinal(payment.getStatus())) {
            log.info("Payment {} is final", payment.getId());
            return;
        }

        try {
            PaymentIntent fresh = PaymentIntent.retrieve(providerPaymentId);
            payment.setStatus(StripePaymentStatusMapper.map(fresh));
            paymentRepository.save(payment);
        } catch (StripeException e) {
            throw new InternalErrorException("Failed to retrieve PaymentIntent: " + e.getMessage());
        }
    }

    private boolean isFinal(PaymentStatus status) {
        return status == PaymentStatus.SUCCEEDED || status == PaymentStatus.DECLINED;
    }
}
