package it.bookverse.controller;


import it.bookverse.entity.Book;
import java.util.List;
import java.util.Locale;

public class SearchBooksController {

    private final BrowseBooksController browseBooksController;

    public SearchBooksController(
            BrowseBooksController browseBooksController
    ) {
        this.browseBooksController = browseBooksController;
    }

    public List<Book> searchBooks(String query) {

        List<Book> visibleBooks =
                browseBooksController.browseBooks();  //extand

        if (query == null || query.isBlank()) {
            return visibleBooks;
        }

        String normalizedQuery =
                query.trim().toLowerCase(Locale.ROOT);

        return visibleBooks.stream()
                .filter(book ->
                        matchesQuery(book, normalizedQuery)
                )
                .toList();
    }

    private boolean matchesQuery(
            Book book,
            String normalizedQuery
    ) {
        return book.getTitle()
                .toLowerCase(Locale.ROOT)
                .contains(normalizedQuery)

                || book.getDescription()
                .toLowerCase(Locale.ROOT)
                .contains(normalizedQuery)

                || book.getCategory()
                .name()
                .toLowerCase(Locale.ROOT)
                .replace("_", " ")
                .contains(normalizedQuery);
    }
}