package it.bookverse.payment;

import java.util.Objects;

public record PaymentResult(
        boolean successful,
        String transactionId,
        String message
) {

    public PaymentResult {
        Objects.requireNonNull(
                message,
                "Payment message cannot be null."
        );
    }

    public static PaymentResult approved(
            String transactionId
    ) {
        return new PaymentResult(
                true,
                Objects.requireNonNull(
                        transactionId,
                        "Transaction id cannot be null."
                ),
                "Mock payment approved successfully."
        );
    }

    public static PaymentResult rejected(
            String message
    ) {
        return new PaymentResult(
                false,
                null,
                Objects.requireNonNull(
                        message,
                        "Rejection message cannot be null."
                )
        );
    }
}