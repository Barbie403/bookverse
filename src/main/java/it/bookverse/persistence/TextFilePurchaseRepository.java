//Concrete Product C2

package it.bookverse.persistence;

import it.bookverse.entity.Purchase;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TextFilePurchaseRepository
        implements PurchaseRepository {

    private static final String SEPARATOR = "\\|";

    private final Path filePath;
    private final List<Purchase> purchases;

    public TextFilePurchaseRepository(
            Path filePath
    ) {
        this.filePath = filePath;
        this.purchases = new ArrayList<>();

        loadFromFile();
    }

    @Override
    public void save(
            Purchase purchase
    ) {
        purchases.add(purchase);
        saveToFile();
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

    private void loadFromFile() {
        purchases.clear();

        List<String> lines =
                TextFileSupport.readAllLines(
                        filePath
                );

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            purchases.add(
                    parsePurchase(line)
            );
        }
    }

    private Purchase parsePurchase(
            String line
    ) {
        String[] values =
                line.split(
                        SEPARATOR,
                        -1
                );

        if (values.length != 5) {
            throw new IllegalStateException(
                    "Invalid purchase record in file: "
                            + line
            );
        }

        try {
            return new Purchase(
                    TextFileSupport.decode(values[0]),
                    TextFileSupport.decode(values[1]),
                    TextFileSupport.decode(values[2]),
                    new BigDecimal(values[3]),
                    LocalDateTime.parse(values[4])
            );

        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "Invalid purchase record in file: "
                            + line,
                    exception
            );
        }
    }

    private void saveToFile() {
        List<String> lines = purchases.stream()
                .map(this::formatPurchase)
                .toList();

        TextFileSupport.writeAllLines(
                filePath,
                lines
        );
    }

    private String formatPurchase(
            Purchase purchase
    ) {
        return String.join(
                "|",
                TextFileSupport.encode(
                        purchase.getId()
                ),
                TextFileSupport.encode(
                        purchase.getReaderId()
                ),
                TextFileSupport.encode(
                        purchase.getBookId()
                ),
                purchase.getPricePaid()
                        .toPlainString(),
                purchase.getPurchaseDate()
                        .toString()
        );
    }
}