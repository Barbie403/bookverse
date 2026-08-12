//Concrete Factory 1
//فقط family مربوط به RAM رو می‌سازه

package it.bookverse.persistence;

public class InMemoryRepositoryFactory
        extends RepositoryFactory {

    @Override
    public UserRepository createUserRepository() {
        return new InMemoryUserRepository();
    }

    @Override
    public BookRepository createBookRepository() {
        return new InMemoryBookRepository();
    }

    @Override
    public PurchaseRepository
    createPurchaseRepository() {
        return new InMemoryPurchaseRepository();
    }
}