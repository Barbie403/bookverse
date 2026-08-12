package it.bookverse.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Objects;

public class Reader extends User {

    private static final BigDecimal INITIAL_BALANCE =
            new BigDecimal("100.00");

    private final LocalDate birthDate;
    private final Wallet wallet;

    /*
     * Used when a new reader registers.
     * Every new reader starts with the initial balance.
     */
    public Reader(
            String id,
            String fullName,
            String email,
            String password,
            LocalDate birthDate
    ) {
        this(
                id,
                fullName,
                email,
                password,
                birthDate,
                INITIAL_BALANCE
        );
    }

    /*
     * Used when an existing reader is restored
     * from persistent storage.
     */
    public Reader(
            String id,
            String fullName,
            String email,
            String password,
            LocalDate birthDate,
            BigDecimal walletBalance
    ) {
        super(
                id,
                fullName,
                email,
                password
        );

        this.birthDate = Objects.requireNonNull(
                birthDate,
                "Birth date cannot be null."
        );

        this.wallet = new Wallet(
                Objects.requireNonNull(
                        walletBalance,
                        "Wallet balance cannot be null."
                )
        );
    }

    @Override
    public Role getRole() {
        return Role.READER;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public int getAge() {
        return Period.between(
                birthDate,
                LocalDate.now(
                        ZoneId.systemDefault()
                )
        ).getYears();
    }

    public boolean isMinor() {
        return getAge() < 18;
    }
}