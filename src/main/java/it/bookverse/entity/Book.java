package it.bookverse.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Book {

    private final String id;
    private String title;
    private final String writerId;
    private String description;
    private BigDecimal price;
    private Audience audience;
    private Category category;
    private String pdfPath;
    private String coverPath;

    /*
     * true  = visible in the public catalog
     * false = removed from the public catalog
     */
    private boolean available;

    /*
     * Groups all data needed to create or restore a book.
     */
    public record BookData(
            String id,
            String title,
            String writerId,
            String description,
            BigDecimal price,
            Audience audience,
            Category category,
            String pdfPath,
            String coverPath,
            boolean available
    ) {
    }

    /*
     * Used when a writer uploads a new book.
     * Every new book is initially available.
     */
    public Book(
            String id,
            String title,
            String writerId,
            String description,
            BigDecimal price,
            Audience audience,
            Category category,
            String pdfPath,
            String coverPath
    ) {
        this(
                new BookData(
                        id,
                        title,
                        writerId,
                        description,
                        price,
                        audience,
                        category,
                        pdfPath,
                        coverPath,
                        true
                )
        );
    }

    /*
     * Used when an existing book is restored
     * from persistent storage.
     */
    public Book(
            BookData data
    ) {
        Objects.requireNonNull(
                data,
                "Book data cannot be null."
        );

        this.id = Objects.requireNonNull(
                data.id(),
                "Book id cannot be null."
        );

        this.title = Objects.requireNonNull(
                data.title(),
                "Book title cannot be null."
        );

        this.writerId = Objects.requireNonNull(
                data.writerId(),
                "Writer id cannot be null."
        );

        this.description = Objects.requireNonNull(
                data.description(),
                "Book description cannot be null."
        );

        this.price = Objects.requireNonNull(
                data.price(),
                "Book price cannot be null."
        );

        if (price.signum() < 0) {
            throw new IllegalArgumentException(
                    "Book price cannot be negative."
            );
        }

        this.audience = Objects.requireNonNull(
                data.audience(),
                "Book audience cannot be null."
        );

        this.category = Objects.requireNonNull(
                data.category(),
                "Book category cannot be null."
        );

        this.pdfPath = Objects.requireNonNull(
                data.pdfPath(),
                "PDF path cannot be null."
        );

        this.coverPath = data.coverPath();
        this.available = data.available();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Audience getAudience() {
        return audience;
    }

    public Category getCategory() {
        return category;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isAdultsOnly() {
        return audience == Audience.ADULTS_ONLY;
    }

    public void removeFromCatalog() {
        available = false;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(
                title,
                "Book title cannot be null."
        );
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNull(
                description,
                "Book description cannot be null."
        );
    }

    public void setPrice(BigDecimal price) {
        Objects.requireNonNull(
                price,
                "Book price cannot be null."
        );

        if (price.signum() < 0) {
            throw new IllegalArgumentException(
                    "Book price cannot be negative."
            );
        }

        this.price = price;
    }

    public void setAudience(Audience audience) {
        this.audience = Objects.requireNonNull(
                audience,
                "Book audience cannot be null."
        );
    }

    public void setCategory(Category category) {
        this.category = Objects.requireNonNull(
                category,
                "Book category cannot be null."
        );
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = Objects.requireNonNull(
                pdfPath,
                "PDF path cannot be null."
        );
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}