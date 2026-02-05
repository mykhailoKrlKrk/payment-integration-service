package com.payment.integration.stripe.service.providers.stripe;

import com.payment.integration.stripe.model.enums.PaymentProvider;
import com.payment.integration.stripe.properties.StripeProperties;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private static final Set<String> SUPPORTED_EVENTS = Set.of(
            "payment_intent.succeeded",
            "payment_intent.payment_failed"
    );

    private final StripeProperties properties;
    private final StripePaymentSyncService paymentSyncService;

    public boolean handleWebHookEvent(String payload, String sigHeader) {
        final Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, properties.getWebHookSecretKey());
        } catch (Exception e) {
            log.warn("Invalid Stripe webhook signature/payload: {}", e.getMessage());
            return false;
        }

        String type = event.getType();
        if (!SUPPORTED_EVENTS.contains(type)) {
            return true;
        }

        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (intent == null) {
            log.warn("Stripe webhook {} has no PaymentIntent in data.object", type);
            return true;
        }
        paymentSyncService.syncByProviderPaymentId(PaymentProvider.STRIPE, intent.getId());
        return true;
    }
}
