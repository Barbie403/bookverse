// Concrete Product B2

package it.bookverse.persistence;

import it.bookverse.entity.Audience;
import it.bookverse.entity.Book;
import it.bookverse.entity.Category;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextFileBookRepository
        implements BookRepository {

    private static final String SEPARATOR = "\\|";

    private final Path filePath;
    private final List<Book> books;

    public TextFileBookRepository(
            Path filePath
    ) {
        this.filePath = filePath;
        this.books = new ArrayList<>();

        loadFromFile();
    }

    @Override
    public void save(
            Book book
    ) {
        books.add(book);
        saveToFile();
    }

    @Override
    public void update(
            Book updatedBook
    ) {
        for (int index = 0;
             index < books.size();
             index++) {

            Book existingBook =
                    books.get(index);

            if (existingBook.getId()
                    .equals(updatedBook.getId())) {

                books.set(
                        index,
                        updatedBook
                );

                saveToFile();
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
    public Optional<Book> findById(
            String id
    ) {
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

    private void loadFromFile() {
        books.clear();

        List<String> lines =
                TextFileSupport.readAllLines(
                        filePath
                );

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            books.add(
                    parseBook(line)
            );
        }
    }

    private Book parseBook(
            String line
    ) {
        String[] values =
                line.split(
                        SEPARATOR,
                        -1
                );

        if (values.length != 10) {
            throw new IllegalStateException(
                    "Invalid book record in file: "
                            + line
            );
        }

        try {
            Book.BookData bookData =
                    new Book.BookData(
                            TextFileSupport.decode(values[0]),
                            TextFileSupport.decode(values[1]),
                            TextFileSupport.decode(values[2]),
                            TextFileSupport.decode(values[3]),
                            new BigDecimal(values[4]),
                            Audience.valueOf(values[5]),
                            Category.valueOf(values[6]),
                            TextFileSupport.decode(values[7]),
                            TextFileSupport.decode(values[8]),
                            Boolean.parseBoolean(values[9])
                    );

            return new Book(bookData);

        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "Invalid book record in file: "
                            + line,
                    exception
            );
        }
    }

    private void saveToFile() {
        List<String> lines = books.stream()
                .map(this::formatBook)
                .toList();

        TextFileSupport.writeAllLines(
                filePath,
                lines
        );
    }

    private String formatBook(
            Book book
    ) {
        return String.join(
                "|",
                TextFileSupport.encode(
                        book.getId()
                ),
                TextFileSupport.encode(
                        book.getTitle()
                ),
                TextFileSupport.encode(
                        book.getWriterId()
                ),
                TextFileSupport.encode(
                        book.getDescription()
                ),
                book.getPrice()
                        .toPlainString(),
                book.getAudience().name(),
                book.getCategory().name(),
                TextFileSupport.encode(
                        book.getPdfPath()
                ),
                TextFileSupport.encode(
                        book.getCoverPath()
                ),
                Boolean.toString(
                        book.isAvailable()
                )
        );
    }
}