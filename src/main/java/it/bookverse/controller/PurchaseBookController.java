package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.Purchase;
import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.exception.BookAlreadyPurchasedException;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.exception.InsufficientBalanceException;
import it.bookverse.exception.UnauthorizedPurchaseException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.PurchaseRepository;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class PurchaseBookController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserSession userSession;

    public PurchaseBookController(
            BookRepository bookRepository,
            UserRepository userRepository,
            PurchaseRepository purchaseRepository,
            UserSession userSession
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.userSession = userSession;
    }

    public Purchase purchaseBook(
            String bookId
    ) {
        Reader reader =
                getAuthenticatedReader();

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException(bookId)
                );

        checkBookAvailability(book);

        checkNotAlreadyPurchased(
                reader.getId(),
                bookId
        );

        checkSufficientBalance(
                reader,
                book
        );

        Writer writer = findWriter(
                book.getWriterId()
        );

        reader.getWallet().debit(
                book.getPrice()
        );

        writer.getWallet().credit(
                book.getPrice()
        );

        Purchase purchase = new Purchase(
                UUID.randomUUID().toString(),
                reader.getId(),
                book.getId(),
                book.getPrice(),
                LocalDateTime.now(
                        ZoneId.systemDefault()
                )
        );

        purchaseRepository.save(purchase);

        userRepository.update(reader);
        userRepository.update(writer);

        return purchase;
    }

    public boolean hasCurrentReaderPurchased(
            String bookId
    ) {
        User currentUser = userSession
                .getCurrentUser()
                .orElse(null);

        if (!(currentUser instanceof Reader reader)) {
            return false;
        }

        return purchaseRepository
                .existsByReaderIdAndBookId(
                        reader.getId(),
                        bookId
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
            throw new UnauthorizedPurchaseException();
        }

        return reader;
    }

    private void checkBookAvailability(
            Book book
    ) {
        if (!book.isAvailable()) {
            throw new IllegalStateException(
                    "This book is no longer available for purchase."
            );
        }
    }

    private void checkNotAlreadyPurchased(
            String readerId,
            String bookId
    ) {
        boolean alreadyPurchased =
                purchaseRepository
                        .existsByReaderIdAndBookId(
                                readerId,
                                bookId
                        );

        if (alreadyPurchased) {
            throw new BookAlreadyPurchasedException();
        }
    }

    private void checkSufficientBalance(
            Reader reader,
            Book book
    ) {
        if (reader.getWallet()
                .getBalance()
                .compareTo(book.getPrice()) < 0) {

            throw new InsufficientBalanceException();
        }
    }

    private Writer findWriter(
            String writerId
    ) {
        User user = userRepository
                .findById(writerId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The writer of this book was not found."
                        )
                );

        if (!(user instanceof Writer writer)) {
            throw new IllegalStateException(
                    "The book owner is not a writer."
            );
        }

        return writer;
    }
}