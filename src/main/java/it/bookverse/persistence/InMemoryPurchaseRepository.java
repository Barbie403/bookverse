//Concrete Product C1

package it.bookverse.persistence;

import it.bookverse.entity.Purchase;

import java.util.ArrayList;
import java.util.List;

public class InMemoryPurchaseRepository
        implements PurchaseRepository {

    private final List<Purchase> purchases =
            new ArrayList<>();

    @Override
    public void save(Purchase purchase) {
        purchases.add(purchase);
    }

    @Override
    public boolean existsByReaderIdAndBookId(
            String readerId,
            String bookId
    ) {
        return purchases.stream()
                .anyMatch(purchase ->
                        purchase.getReaderId()
                                .equals(readerId)
                                && purchase.getBookId()
                                .equals(bookId)
                );
    }

    @Override
    public List<Purchase> findByReaderId(
            String readerId
    ) {
        return purchases.stream()
                .filter(purchase ->
                        purchase.getReaderId()
                                .equals(readerId)
                )
                .toList();
    }
}