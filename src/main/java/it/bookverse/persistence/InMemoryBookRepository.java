//Concrete Product B1

package it.bookverse.persistence;

import it.bookverse.entity.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryBookRepository
        implements BookRepository {

    private final List<Book> books =
            new ArrayList<>();

    @Override
    public void save(Book book) {
        books.add(book);
    }

    @Override
    public void update(Book updatedBook) {
        for (int index = 0;
             index < books.size();
             index++) {

            Book existingBook =
                    books.get(index);

            if (existingBook.getId()
                    .equals(updatedBook.getId())) {

                books.set(index, updatedBook);
                return;
            }
        }

        throw new IllegalArgumentException(
                "Book not found: "
                        + updatedBook.getId()
        );
    }

    @Override
    public List<Book> findAll() {
        return List.copyOf(books);
    }

    @Override
    public Optional<Book> findById(String id) {
        return books.stream()
                .filter(book ->
                        book.getId().equals(id)
                )
                .findFirst();
    }

    @Override
    public List<Book> findByWriterId(
            String writerId
    ) {
        return books.stream()
                .filter(book ->
                        book.getWriterId()
                                .equals(writerId)
                )
                .toList();
    }
}