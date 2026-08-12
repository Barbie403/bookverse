package it.bookverse.controller;

import it.bookverse.entity.Book;
import it.bookverse.entity.Audience;
import it.bookverse.entity.Category;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.exception.InvalidBookDataException;
import it.bookverse.exception.UnauthorizedUploadException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.session.UserSession;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

public class UploadBookController {

    private final BookRepository bookRepository;
    private final UserSession userSession;

    public UploadBookController(
            BookRepository bookRepository,
            UserSession userSession
    ) {
        this.bookRepository = bookRepository;
        this.userSession = userSession;
    }

    public Book uploadBook(
            String title,
            String description,
            String priceText,
            Audience audience,
            Category category,
            Path pdfFile,
            Path coverFile
    ) {
        Writer writer = getAuthenticatedWriter();

        String normalizedTitle =
                validateRequiredText(title, "Title is required.");

        String normalizedDescription =
                validateRequiredText(
                        description,
                        "Description is required."
                );

        BigDecimal price = parsePrice(priceText);

        validateAudience(audience);
        validateCategory(category);
        validatePdfFile(pdfFile);
        validateOptionalCoverFile(coverFile);

        Book book = new Book(
                UUID.randomUUID().toString(),
                normalizedTitle,
                writer.getId(),
                normalizedDescription,
                price,
                audience,
                category,
                pdfFile.toAbsolutePath()
                        .normalize()
                        .toString(),
                coverFile == null
                        ? null
                        : coverFile.toAbsolutePath()
                        .normalize()
                        .toString()
        );

        bookRepository.save(book);

        return book;
    }

    public boolean isCurrentUserWriter() {
        return userSession.getCurrentUser()
                .filter(Writer.class::isInstance)
                .isPresent();
    }

    private Writer getAuthenticatedWriter() {
        User currentUser = userSession
                .getCurrentUser()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The user must be authenticated."
                        )
                );

        if (!(currentUser instanceof Writer writer)) {
            throw new UnauthorizedUploadException();
        }

        return writer;
    }

    private String validateRequiredText(
            String value,
            String errorMessage
    ) {
        if (value == null || value.isBlank()) {
            throw new InvalidBookDataException(errorMessage);
        }

        return value.trim();
    }

    private BigDecimal parsePrice(String priceText) {
        if (priceText == null || priceText.isBlank()) {
            throw new InvalidBookDataException(
                    "Price is required."
            );
        }

        try {
            BigDecimal price = new BigDecimal(
                    priceText.trim()
            );

            if (price.signum() <= 0) {
                throw new InvalidBookDataException(
                        "Price must be greater than zero."
                );
            }

            return price;

        } catch (NumberFormatException exception) {
            throw new InvalidBookDataException(
                    "Price must be a valid number."
            );
        }
    }

    private void validateAudience(Audience audience) {
        if (audience == null) {
            throw new InvalidBookDataException(
                    "Audience is required."
            );
        }
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new InvalidBookDataException(
                    "Category is required."
            );
        }
    }

    private void validatePdfFile(Path pdfFile) {
        if (pdfFile == null) {
            throw new InvalidBookDataException(
                    "A PDF file is required."
            );
        }

        if (!Files.isRegularFile(pdfFile)) {
            throw new InvalidBookDataException(
                    "The selected PDF file does not exist."
            );
        }

        if (!hasExtension(pdfFile, ".pdf")) {
            throw new InvalidBookDataException(
                    "The selected book file must be a PDF."
            );
        }
    }

    private void validateOptionalCoverFile(Path coverFile) {
        if (coverFile == null) {
            return;
        }

        if (!Files.isRegularFile(coverFile)) {
            throw new InvalidBookDataException(
                    "The selected cover file does not exist."
            );
        }

        boolean validImage =
                hasExtension(coverFile, ".png")
                        || hasExtension(coverFile, ".jpg")
                        || hasExtension(coverFile, ".jpeg");

        if (!validImage) {
            throw new InvalidBookDataException(
                    "The cover must be a PNG, JPG or JPEG image."
            );
        }
    }

    private boolean hasExtension(
            Path file,
            String extension
    ) {
        return file.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(extension);
    }
}