package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.exception.BookDownloadException;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.exception.BookNotPurchasedException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.PurchaseRepository;
import it.bookverse.session.UserSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DownloadBookController {

    private final BookRepository bookRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserSession userSession;

    public DownloadBookController(
            BookRepository bookRepository,
            PurchaseRepository purchaseRepository,
            UserSession userSession
    ) {
        this.bookRepository = bookRepository;
        this.purchaseRepository = purchaseRepository;
        this.userSession = userSession;
    }

    public Path downloadBook(
            String bookId,
            Path destinationFile
    ) {
        Reader reader = getAuthenticatedReader();

        checkBookWasPurchased(
                reader.getId(),
                bookId
        );

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException(bookId)
                );

        Path sourceFile = Path.of(
                book.getPdfPath()
        );

        validateDestination(destinationFile);

        try {
            return Files.copy(
                    sourceFile,
                    destinationFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException exception) {
            throw new BookDownloadException(
                    "The book could not be downloaded.",
                    exception
            );
        }
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
                    "Only readers can download purchased books."
            );
        }

        return reader;
    }

    private void checkBookWasPurchased(
            String readerId,
            String bookId
    ) {
        boolean purchased =
                purchaseRepository
                        .existsByReaderIdAndBookId(
                                readerId,
                                bookId
                        );

        if (!purchased) {
            throw new BookNotPurchasedException();
        }
    }

    private void validateDestination(
            Path destinationFile
    ) {
        if (destinationFile == null) {
            throw new IllegalArgumentException(
                    "The download destination cannot be null."
            );
        }
    }
}