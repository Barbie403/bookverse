package it.bookverse.controller;


import it.bookverse.entity.Book;
import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.persistence.BookRepository;
import it.bookverse.session.UserSession;

import java.util.List;

public class BrowseBooksController {

    private final BookRepository bookRepository;
    private final UserSession userSession;

    public BrowseBooksController(
            BookRepository bookRepository,
            UserSession userSession
    ) {
        this.bookRepository = bookRepository;
        this.userSession = userSession;
    }

    public List<Book> browseBooks() {
        User currentUser = userSession
                .getCurrentUser()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The user must be authenticated."
                        )
                );

        /*
         * Removed books are not shown
         * in the public catalog.
         */
        List<Book> availableBooks =
                bookRepository.findAll()
                        .stream()
                        .filter(Book::isAvailable)
                        .toList();

        /*
         * A minor Reader cannot see adult books.
         */
        if (currentUser instanceof Reader reader
                && reader.isMinor()) {

            return availableBooks.stream()
                    .filter(book ->
                            !book.isAdultsOnly()
                    )
                    .toList();
        }

        return availableBooks;
    }
}