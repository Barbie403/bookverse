package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.entity.Book;
import it.bookverse.controller.PurchaseBookController;
import it.bookverse.controller.ViewBookDetailsController;
import it.bookverse.controller.ViewBookDetailsController.BookDetails;
import it.bookverse.exception.BookAlreadyPurchasedException;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.exception.InsufficientBalanceException;
import it.bookverse.exception.UnauthorizedPurchaseException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.net.URL;

public class BookDetailsBoundary {

    private static final String SUCCESS_CLASS =
            "details-message-success";

    private static final String ERROR_CLASS =
            "details-message-error";

    private final ViewBookDetailsController
            viewBookDetailsController;

    private final PurchaseBookController
            purchaseBookController;

    private final SceneManager sceneManager;
    private final String bookId;
    private final boolean openedFromPurchasedBooks;

    @FXML
    private StackPane coverContainer;

    @FXML
    private ImageView coverImageView;

    @FXML
    private Label coverPlaceholderLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label writerLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label audienceLabel;

    @FXML
    private Label availabilityLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button purchaseButton;

    public BookDetailsBoundary(
            ViewBookDetailsController viewBookDetailsController,
            PurchaseBookController purchaseBookController,
            SceneManager sceneManager,
            String bookId,
            boolean openedFromPurchasedBooks
    ) {
        this.viewBookDetailsController =
                viewBookDetailsController;

        this.purchaseBookController =
                purchaseBookController;

        this.sceneManager =
                sceneManager;

        this.bookId =
                bookId;

        this.openedFromPurchasedBooks =
                openedFromPurchasedBooks;
    }

    @FXML
    private void initialize() {
        loadBookDetails();
        configurePurchaseState();
    }

    private void loadBookDetails() {
        try {
            BookDetails details =
                    viewBookDetailsController
                            .viewBookDetails(bookId);

            displayBook(details);

        } catch (BookNotFoundException exception) {
            showError(
                    exception.getMessage()
            );

            purchaseButton.setDisable(true);
        }
    }

    private void displayBook(
            BookDetails details
    ) {
        Book book =
                details.book();

        titleLabel.setText(
                book.getTitle()
        );

        writerLabel.setText(
                "By " + details.writerPenName()
        );

        categoryLabel.setText(
                formatCategory(book)
        );

        audienceLabel.setText(
                book.isAdultsOnly()
                        ? "Adults only · 18+"
                        : "Everyone"
        );

        availabilityLabel.setText(
                book.isAvailable()
                        ? "Available"
                        : "Removed"
        );

        updateAvailabilityStyle(
                book.isAvailable()
        );

        priceLabel.setText(
                "€ "
                        + book.getPrice()
                        .toPlainString()
        );

        descriptionLabel.setText(
                book.getDescription()
        );

        displayCover(book);
        clearMessage();

        if (!book.isAvailable()
                && !openedFromPurchasedBooks) {

            purchaseButton.setDisable(true);
            purchaseButton.setText(
                    "Not Available"
            );
        }
    }

    private void configurePurchaseState() {
        boolean alreadyPurchased =
                openedFromPurchasedBooks
                        || purchaseBookController
                        .hasCurrentReaderPurchased(
                                bookId
                        );

        if (alreadyPurchased) {
            purchaseButton.setDisable(true);
            purchaseButton.setText(
                    "Already Purchased"
            );

            clearMessage();
        }
    }

    private void displayCover(
            Book book
    ) {
        Image image =
                loadCoverImage(
                        book.getCoverPath()
                );

        if (image != null
                && !image.isError()) {

            coverImageView.setImage(image);

            coverImageView.setVisible(true);
            coverImageView.setManaged(true);

            coverPlaceholderLabel.setVisible(false);
            coverPlaceholderLabel.setManaged(false);

        } else {
            coverImageView.setImage(null);

            coverImageView.setVisible(false);
            coverImageView.setManaged(false);

            coverPlaceholderLabel.setText(
                    createCoverText(
                            book.getTitle()
                    )
            );

            coverPlaceholderLabel.setVisible(true);
            coverPlaceholderLabel.setManaged(true);
        }
    }

    private Image loadCoverImage(
            String coverPath
    ) {
        if (coverPath == null
                || coverPath.isBlank()) {

            return null;
        }

        try {
            URL resource =
                    getClass().getResource(
                            coverPath.startsWith("/")
                                    ? coverPath
                                    : "/" + coverPath
                    );

            if (resource != null) {
                return new Image(
                        resource.toExternalForm(),
                        true
                );
            }

            File file =
                    new File(coverPath);

            if (file.exists()) {
                return new Image(
                        file.toURI().toString(),
                        true
                );
            }

        } catch (RuntimeException exception) {
            return null;
        }

        return null;
    }

    private void updateAvailabilityStyle(
            boolean available
    ) {
        availabilityLabel
                .getStyleClass()
                .removeAll(
                        "details-availability-available",
                        "details-availability-removed"
                );

        if (available) {
            availabilityLabel
                    .getStyleClass()
                    .add(
                            "details-availability-available"
                    );

        } else {
            availabilityLabel
                    .getStyleClass()
                    .add(
                            "details-availability-removed"
                    );
        }
    }

    @FXML
    private void handlePurchase() {
        clearMessage();

        try {
            purchaseBookController
                    .purchaseBook(bookId);

            showSuccess(
                    "Book purchased successfully."
            );

            purchaseButton.setDisable(true);
            purchaseButton.setText(
                    "Purchased"
            );

        } catch (InsufficientBalanceException
                 | BookAlreadyPurchasedException
                 | UnauthorizedPurchaseException
                 | BookNotFoundException
                 | IllegalStateException exception) {

            showError(
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void handleBackToLibrary() {
        if (openedFromPurchasedBooks) {
            sceneManager.showPurchasedBooks();

        } else {
            sceneManager.showLibrary();
        }
    }

    private void showSuccess(
            String message
    ) {
        clearMessageStyles();

        messageLabel
                .getStyleClass()
                .add(SUCCESS_CLASS);

        messageLabel.setText(message);
    }

    private void showError(
            String message
    ) {
        clearMessageStyles();

        messageLabel
                .getStyleClass()
                .add(ERROR_CLASS);

        messageLabel.setText(
                message == null
                        ? "The operation could not be completed."
                        : message
        );
    }

    private void clearMessage() {
        clearMessageStyles();
        messageLabel.setText("");
    }

    private void clearMessageStyles() {
        messageLabel
                .getStyleClass()
                .removeAll(
                        SUCCESS_CLASS,
                        ERROR_CLASS
                );
    }

    private String createCoverText(
            String title
    ) {
        if (title == null
                || title.isBlank()) {

            return "BOOKVERSE";
        }

        return title.toUpperCase();
    }

    private String formatCategory(
            Book book
    ) {
        return book.getCategory()
                .name()
                .replace("_", " ");
    }
}