package com.payment.integration.stripe.service.factory;

import com.payment.integration.stripe.model.enums.PaymentProvider;
import com.payment.integration.stripe.service.strategy.PaymentManagementStrategy;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderFactory {

    private final Map<PaymentProvider, PaymentManagementStrategy> paymentManagementStrategyMap;

    public PaymentProviderFactory(List<PaymentManagementStrategy> services) {
        this.paymentManagementStrategyMap = services.stream()
                .collect(Collectors.toMap(
                        PaymentManagementStrategy::getProvider,
                        Function.identity()
                ));
    }

    public PaymentManagementStrategy getPaymentManagementStrategy(PaymentProvider paymentProvider) {
        return paymentManagementStrategyMap.getOrDefault(
                paymentProvider,
                paymentManagementStrategyMap.get(PaymentProvider.STRIPE));
    }
}
