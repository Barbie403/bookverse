package it.bookverse.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Purchase {

    private final String id;
    private final String readerId;
    private final String bookId;
    private final BigDecimal pricePaid;
    private final LocalDateTime purchaseDate;

    public Purchase(
            String id,
            String readerId,
            String bookId,
            BigDecimal pricePaid,
            LocalDateTime purchaseDate
    ) {
        this.id = Objects.requireNonNull(
                id,
                "Purchase id cannot be null."
        );

        this.readerId = Objects.requireNonNull(
                readerId,
                "Reader id cannot be null."
        );

        this.bookId = Objects.requireNonNull(
                bookId,
                "Book id cannot be null."
        );

        this.pricePaid = Objects.requireNonNull(
                pricePaid,
                "Paid price cannot be null."
        );

        this.purchaseDate = Objects.requireNonNull(
                purchaseDate,
                "Purchase date cannot be null."
        );
    }

    public String getId() {
        return id;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getBookId() {
        return bookId;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
}