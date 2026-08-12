//این هم جزء رسمی Abstract Factory نیستTولی useful
//الان خودش سه repository را با هم نگه می‌داره
//این‌ها repositoryهای متعلق به یک persistence family انتخاب ‌شده هستند

package it.bookverse.persistence;

import java.util.Objects;

public record RepositoryBundle(
        UserRepository userRepository,
        BookRepository bookRepository,
        PurchaseRepository purchaseRepository
) {

    public RepositoryBundle {
        Objects.requireNonNull(
                userRepository,
                "User repository cannot be null."
        );

        Objects.requireNonNull(
                bookRepository,
                "Book repository cannot be null."
        );

        Objects.requireNonNull(
                purchaseRepository,
                "Purchase repository cannot be null."
        );
    }
}