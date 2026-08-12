package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.Purchase;
import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.PurchaseRepository;
import it.bookverse.session.UserSession;

import java.util.Comparator;
import java.util.List;

public class ViewPurchasedBooksController {

    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;
    private final UserSession userSession;

    public ViewPurchasedBooksController(
            PurchaseRepository purchaseRepository,
            BookRepository bookRepository,
            UserSession userSession
    ) {
        this.purchaseRepository = purchaseRepository;
        this.bookRepository = bookRepository;
        this.userSession = userSession;
    }

    public List<PurchasedBookItem> viewPurchasedBooks() {

        Reader reader =
                getAuthenticatedReader();

        return purchaseRepository
                .findByReaderId(reader.getId())
                .stream()
                .sorted(
                        Comparator.comparing(
                                Purchase::getPurchaseDate
                        ).reversed()
                )
                .map(this::createPurchasedBookItem)
                .toList();
    }

    private PurchasedBookItem createPurchasedBookItem(
            Purchase purchase
    ) {
        Book book = bookRepository
                .findById(purchase.getBookId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Purchased book was not found: "
                                        + purchase.getBookId()
                        )
                );

        return new PurchasedBookItem(
                book,
                purchase
        );
    }

    private Reader getAuthenticatedReader() {

        User currentUser = userSession
                .getCurrentUser()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The user must be authenticated."
                        )
                );

        if (!(currentUser instanceof Reader reader)) {
            throw new IllegalStateException(
                    "Only readers have a purchased-books library."
            );
        }

        return reader;
    }

    public record PurchasedBookItem(
            Book book,
            Purchase purchase
    ) {
    }
}