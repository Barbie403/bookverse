//Abstract Product C

package it.bookverse.persistence;

import it.bookverse.entity.Purchase;

import java.util.List;

public interface PurchaseRepository {

    void save(Purchase purchase);

    boolean existsByReaderIdAndBookId(
            String readerId,
            String bookId
    );

    List<Purchase> findByReaderId(String readerId);
}