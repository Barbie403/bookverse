package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.exception.UnauthorizedBookRemovalException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.session.UserSession;

public class RemoveBookController {

    private final BookRepository bookRepository;
    private final UserSession userSession;

    public RemoveBookController(
            BookRepository bookRepository,
            UserSession userSession
    ) {
        this.bookRepository = bookRepository;
        this.userSession = userSession;
    }

    public Book removeFromCatalog(String bookId) {
        Writer writer = getAuthenticatedWriter();

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException(bookId)
                );

        checkBookOwnership(book, writer);

        if (!book.isAvailable()) {
            return book;
        }

        book.removeFromCatalog();
        bookRepository.update(book);

        return book;
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
            throw new UnauthorizedBookRemovalException();
        }

        return writer;
    }

    private void checkBookOwnership(
            Book book,
            Writer writer
    ) {
        if (!book.getWriterId()
                .equals(writer.getId())) {

            throw new UnauthorizedBookRemovalException();
        }
    }
}