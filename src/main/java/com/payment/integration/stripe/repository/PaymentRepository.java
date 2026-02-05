package com.payment.integration.stripe.repository;

import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentProvider;
import com.payment.integration.stripe.model.enums.PaymentStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    boolean existsById(UUID id);

    @Query("SELECT payment.status FROM Payment payment WHERE payment.id = :id")
    PaymentStatus getPaymentStatus(UUID id);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByProviderAndProviderPaymentId(
            PaymentProvider provider, String providerId);

    @Query("""
            SELECT payment
            FROM Payment payment
            WHERE payment.status IN :statuses
              AND payment.providerPaymentId IS NOT NULL
              AND payment.isDeleted = FALSE
            ORDER BY payment.updatedAt ASC
            """)
    List<Payment> findForSync(
            @Param("statuses") Collection<PaymentStatus> statuses, Pageable pageable);
}
