//Abstract Factory

package it.bookverse.persistence;

public abstract class RepositoryFactory {

    public static RepositoryFactory getFactory(
            PersistenceMode mode
    ) {
        if (mode == null) {
            throw new IllegalArgumentException(
                    "Persistence mode cannot be null."
            );
        }

        return switch (mode) {
            case IN_MEMORY ->
                    new InMemoryRepositoryFactory();

            case FILE_SYSTEM ->
                    new TextFileRepositoryFactory();
        };
    }

    public abstract UserRepository
    createUserRepository();

    public abstract BookRepository
    createBookRepository();

    public abstract PurchaseRepository
    createPurchaseRepository();

    public RepositoryBundle createRepositories() {
        return new RepositoryBundle(
                createUserRepository(),
                createBookRepository(),
                createPurchaseRepository()
        );
    }
}