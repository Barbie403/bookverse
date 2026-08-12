package it.bookverse.payment;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class MockPaymentGateway
        implements PaymentGateway {

    @Override
    public PaymentResult processPayment(
            String customerId,
            BigDecimal amount
    ) {
        Objects.requireNonNull(
                customerId,
                "Customer id cannot be null."
        );

        Objects.requireNonNull(
                amount,
                "Payment amount cannot be null."
        );

        if (customerId.isBlank()) {
            return PaymentResult.rejected(
                    "The customer id is invalid."
            );
        }

        if (amount.signum() <= 0) {
            return PaymentResult.rejected(
                    "The payment amount must be greater than zero."
            );
        }

        /*
         * The real external payment request is simulated.
         * A valid mock payment is always approved.
         */
        return PaymentResult.approved(
                "MOCK-"
                        + UUID.randomUUID()
                        .toString()
                        .toUpperCase()
        );
    }
}