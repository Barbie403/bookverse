package it.bookverse.payment;

import java.math.BigDecimal;

public interface PaymentGateway {

    PaymentResult processPayment(
            String customerId,
            BigDecimal amount
    );
}