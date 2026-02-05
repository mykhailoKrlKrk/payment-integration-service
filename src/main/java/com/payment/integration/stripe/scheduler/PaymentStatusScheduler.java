package com.payment.integration.stripe.scheduler;

import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentStatus;
import com.payment.integration.stripe.repository.PaymentRepository;
import com.payment.integration.stripe.service.providers.stripe.StripePaymentSyncService;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusScheduler {

    private final PaymentRepository paymentRepository;
    private final StripePaymentSyncService paymentSyncService;

    @Value("${payments.sync.batch-size}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${payments.sync.fixed-delay-ms}")
    public void syncPendingPayments() {
        var statuses = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.PROCESSING);
        List<Payment> batch =
                paymentRepository.findForSync(statuses, PageRequest.of(0, batchSize));
        batch.stream()
                .filter(this::isNotFinal)
                .forEach(this::safeSync);
    }

    private boolean isNotFinal(Payment payment) {
        return payment.getStatus() != PaymentStatus.SUCCEEDED
                && payment.getStatus() != PaymentStatus.DECLINED;
    }

    private void safeSync(Payment payment) {
        try {
            paymentSyncService.syncByProviderPaymentId(
                    payment.getProvider(),
                    payment.getProviderPaymentId()
            );
        } catch (Exception e) {
            log.warn(
                    "Scheduler: failed to sync paymentId={}, providerPaymentId={}, reason={}",
                    payment.getId(), payment.getProviderPaymentId(), e.getMessage()
            );
        }
    }
}
