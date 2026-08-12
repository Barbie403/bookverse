package it.bookverse.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Wallet {

    private BigDecimal balance;

    public Wallet(BigDecimal initialBalance) {
        Objects.requireNonNull(
                initialBalance,
                "Initial balance cannot be null."
        );

        if (initialBalance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Initial balance cannot be negative."
            );
        }

        this.balance = initialBalance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void credit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        validatePositiveAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Wallet balance cannot become negative."
            );
        }

        balance = balance.subtract(amount);
    }

    private void validatePositiveAmount(
            BigDecimal amount
    ) {
        Objects.requireNonNull(
                amount,
                "Amount cannot be null."
        );

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }
    }
}