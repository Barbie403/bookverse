package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.UserRepository;

public class ViewBookDetailsController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ViewBookDetailsController(
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public BookDetails viewBookDetails(
            String bookId
    ) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new BookNotFoundException(bookId)
                );

        User writerUser = userRepository
                .findById(book.getWriterId())
                .orElse(null);

        String writerPenName =
                resolveWriterName(writerUser);

        return new BookDetails(
                book,
                writerPenName
        );
    }

    private String resolveWriterName(
            User user
    ) {
        if (user instanceof Writer writer) {
            return writer.getPenName();
        }

        return "Unknown writer";
    }

    public record BookDetails(
            Book book,
            String writerPenName
    ) {
    }
}