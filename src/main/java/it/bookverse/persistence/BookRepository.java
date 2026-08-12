// Abstract Product B

package it.bookverse.persistence;

import it.bookverse.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    void save(Book book);

    void update(Book book);

    List<Book> findAll();

    Optional<Book> findById(String id);

    List<Book> findByWriterId(String writerId);
}