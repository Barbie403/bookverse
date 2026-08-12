package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.persistence.BookRepository;
import it.bookverse.session.UserSession;

import java.util.List;

public class ViewMyBooksController {

    private final BookRepository bookRepository;
    private final UserSession userSession;

    public ViewMyBooksController(
            BookRepository bookRepository,
            UserSession userSession
    ) {
        this.bookRepository = bookRepository;
        this.userSession = userSession;
    }

    public List<Book> viewMyBooks() {
        Writer writer = getAuthenticatedWriter();

        return bookRepository.findByWriterId(
                writer.getId()
        );
    }

    private Writer getAuthenticatedWriter() {
        User currentUser = userSession
                .getCurrentUser()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The user must be authenticated."
                        )
                );

        if (!(currentUser instanceof Writer writer)) {
            throw new IllegalStateException(
                    "Only writers can view their uploaded books."
            );
        }

        return writer;
    }
}