package it.bookverse.controller;

import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.payment.PaymentGateway;
import it.bookverse.payment.PaymentResult;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;

import java.math.BigDecimal;
import java.util.Objects;

public class TopUpWalletController {

    private final UserRepository userRepository;
    private final UserSession userSession;
    private final PaymentGateway paymentGateway;

    public TopUpWalletController(
            UserRepository userRepository,
            UserSession userSession,
            PaymentGateway paymentGateway
    ) {
        this.userRepository =
                Objects.requireNonNull(
                        userRepository,
                        "User repository cannot be null."
                );

        this.userSession =
                Objects.requireNonNull(
                        userSession,
                        "User session cannot be null."
                );

        this.paymentGateway =
                Objects.requireNonNull(
                        paymentGateway,
                        "Payment gateway cannot be null."
                );
    }

    public TopUpResult topUpWallet(
            BigDecimal amount
    ) {
        validateAmount(amount);

        Reader reader =
                getAuthenticatedReader();

        PaymentResult paymentResult =
                paymentGateway.processPayment(
                        reader.getId(),
                        amount
                );

        if (!paymentResult.successful()) {
            throw new IllegalStateException(
                    paymentResult.message()
            );
        }

        reader.getWallet().credit(amount);

        /*
         * In file mode, this writes the new balance
         * to users.txt. In memory mode, it updates
         * the current in-memory repository.
         */
        userRepository.update(reader);

        return new TopUpResult(
                amount,
                reader.getWallet().getBalance(),
                paymentResult.transactionId(),
                paymentResult.message()
        );
    }

    public boolean canCurrentUserTopUp() {
        return userSession
                .getCurrentUser()
                .filter(Reader.class::isInstance)
                .isPresent();
    }

    private Reader getAuthenticatedReader() {
        User currentUser =
                userSession.getCurrentUser()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "The user must be authenticated."
                                )
                        );

        if (!(currentUser instanceof Reader reader)) {
            throw new IllegalStateException(
                    "Only readers can add funds to their wallet."
            );
        }

        return reader;
    }

    private void validateAmount(
            BigDecimal amount
    ) {
        Objects.requireNonNull(
                amount,
                "Top-up amount cannot be null."
        );

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Top-up amount must be greater than zero."
            );
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Top-up amount can have at most two decimal places."
            );
        }
    }

    public record TopUpResult(
            BigDecimal amountAdded,
            BigDecimal newBalance,
            String transactionId,
            String paymentMessage
    ) {
    }
}