//Concrete Factory 2
//فقط family مربوط به فایل رو می‌سازه

package it.bookverse.persistence;

import java.nio.file.Path;
import java.util.Objects;

public class TextFileRepositoryFactory
        extends RepositoryFactory {

    private static final Path DEFAULT_DATA_DIRECTORY =
            Path.of(
                    "data",
                    "persistence"
            );

    private final Path dataDirectory;

    public TextFileRepositoryFactory() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public TextFileRepositoryFactory(
            Path dataDirectory
    ) {
        this.dataDirectory =
                Objects.requireNonNull(
                        dataDirectory,
                        "Data directory cannot be null."
                );
    }

    @Override
    public UserRepository createUserRepository() {
        return new TextFileUserRepository(
                dataDirectory.resolve(
                        "users.txt"
                )
        );
    }

    @Override
    public BookRepository createBookRepository() {
        return new TextFileBookRepository(
                dataDirectory.resolve(
                        "books.txt"
                )
        );
    }

    @Override
    public PurchaseRepository
    createPurchaseRepository() {
        return new TextFilePurchaseRepository(
                dataDirectory.resolve(
                        "purchases.txt"
                )
        );
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}