package com.payment.integration.stripe.mapper;

import com.payment.integration.stripe.dto.param.CreatePaymentParam;
import com.payment.integration.stripe.dto.PaymentResponseDto;
import com.payment.integration.stripe.model.Payment;
import com.payment.integration.stripe.model.enums.PaymentProvider;
import com.payment.integration.stripe.model.enums.PaymentStatus;
import java.util.Locale;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "rawStatus", source = "status")
    PaymentResponseDto toPaymentResponseDto(Payment payment);

    @Mapping(target = "currency", source = "currency", qualifiedByName = "toLower")
    Payment toModel(CreatePaymentParam param);

    @AfterMapping
    default void setDefaults(CreatePaymentParam param,
            @MappingTarget Payment payment,
            @Context String idempotencyKey) {
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setIdempotencyKey(idempotencyKey);
        payment.setStatus(PaymentStatus.PENDING);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "providerPaymentId", source = "providerPaymentId")
    @Mapping(target = "clientSecret", source = "clientSecret")
    void applyProviderResult(@MappingTarget Payment payment,
            PaymentResponseDto result,
            @Context PaymentProvider provider);

    @AfterMapping
    default void applyStatus(PaymentResponseDto result,
            @MappingTarget Payment payment,
            @Context PaymentProvider provider) {
        payment.setStatus(mapProviderStatus(provider, result.rawStatus()));
    }

    @Named("toLower")
    static String toLower(String currency) {
        return currency == null ? null : currency.toLowerCase(Locale.ROOT);
    }

    static PaymentStatus mapProviderStatus(PaymentProvider provider, String rawStatus) {
        if (provider == PaymentProvider.STRIPE) {
            return switch (rawStatus) {
                case "succeeded" -> PaymentStatus.SUCCEEDED;
                case "processing" -> PaymentStatus.PROCESSING;
                case "canceled" -> PaymentStatus.DECLINED;
                default -> PaymentStatus.PENDING;
            };
        }
        return PaymentStatus.PENDING;
    }
}