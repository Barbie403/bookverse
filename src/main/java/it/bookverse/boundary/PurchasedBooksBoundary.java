package it.bookverse.boundary;

import it.bookverse.controller.DownloadBookController;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.ViewPurchasedBooksController;
import it.bookverse.controller.ViewPurchasedBooksController.PurchasedBookItem;
import it.bookverse.controller.ViewWalletController;
import it.bookverse.entity.Book;
import it.bookverse.exception.BookDownloadException;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.exception.BookNotPurchasedException;
import it.bookverse.navigation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PurchasedBooksBoundary {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String SUCCESS_CLASS =
            "purchased-message-success";

    private static final String ERROR_CLASS =
            "purchased-message-error";

    private final ViewPurchasedBooksController
            viewPurchasedBooksController;

    private final DownloadBookController
            downloadBookController;

    private final ViewWalletController
            viewWalletController;

    private final LoginController
            loginController;

    private final SceneManager
            sceneManager;

    @FXML
    private FlowPane purchasedBooksContainer;

    @FXML
    private Label messageLabel;

    @FXML
    private Label walletBalanceLabel;

    public PurchasedBooksBoundary(
            ViewPurchasedBooksController viewPurchasedBooksController,
            DownloadBookController downloadBookController,
            ViewWalletController viewWalletController,
            LoginController loginController,
            SceneManager sceneManager
    ) {
        this.viewPurchasedBooksController =
                viewPurchasedBooksController;

        this.downloadBookController =
                downloadBookController;

        this.viewWalletController =
                viewWalletController;

        this.loginController =
                loginController;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        loadPurchasedBooks();
        updateWalletBalance();
    }

    private void loadPurchasedBooks() {
        messageLabel.setText("");

        try {
            List<PurchasedBookItem> purchasedBooks =
                    viewPurchasedBooksController
                            .viewPurchasedBooks();

            displayPurchasedBooks(
                    purchasedBooks
            );

        } catch (IllegalStateException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void displayPurchasedBooks(
            List<PurchasedBookItem> purchasedBooks
    ) {
        purchasedBooksContainer
                .getChildren()
                .clear();

        if (purchasedBooks.isEmpty()) {
            messageLabel.setText(
                    "You have not purchased any books yet."
            );

            return;
        }

        messageLabel.setText("");

        for (PurchasedBookItem item : purchasedBooks) {
            purchasedBooksContainer
                    .getChildren()
                    .add(
                            createBookCard(item)
                    );
        }
    }

    private VBox createBookCard(
            PurchasedBookItem item
    ) {
        Book book =
                item.book();

        ImageView coverView =
                createCoverView(book);

        Label titleLabel =
                new Label(
                        book.getTitle()
                );

        titleLabel.setWrapText(true);

        titleLabel
                .getStyleClass()
                .add(
                        "purchased-card-title"
                );

        Label categoryLabel =
                new Label(
                        book.getCategory()
                                .name()
                                .replace(
                                        "_",
                                        " "
                                )
                );

        categoryLabel
                .getStyleClass()
                .add(
                        "purchased-category-badge"
                );

        Label priceLabel =
                new Label(
                        "Paid: € "
                                + item.purchase()
                                .getPricePaid()
                );

        priceLabel
                .getStyleClass()
                .add(
                        "purchased-card-price"
                );

        Label dateLabel =
                new Label(
                        "Purchased: "
                                + item.purchase()
                                .getPurchaseDate()
                                .format(
                                        DATE_FORMATTER
                                )
                );

        dateLabel
                .getStyleClass()
                .add(
                        "purchased-card-date"
                );

        Button detailsButton =
                new Button(
                        "View Details"
                );

        detailsButton
                .getStyleClass()
                .add(
                        "purchased-secondary-button"
                );

        detailsButton.setMaxWidth(
                Double.MAX_VALUE
        );

        detailsButton.setOnAction(
                event ->
                        sceneManager
                                .showPurchasedBookDetails(
                                        book.getId()
                                )
        );

        Button downloadButton =
                new Button(
                        "Download PDF"
                );

        downloadButton
                .getStyleClass()
                .add(
                        "purchased-download-button"
                );

        downloadButton.setMaxWidth(
                Double.MAX_VALUE
        );

        downloadButton.setOnAction(
                event ->
                        handleDownload(
                                book
                        )
        );

        VBox card =
                new VBox(
                        10,
                        coverView,
                        titleLabel,
                        categoryLabel,
                        priceLabel,
                        dateLabel,
                        detailsButton,
                        downloadButton
                );

        card.setPrefWidth(
                230
        );

        card
                .getStyleClass()
                .add(
                        "purchased-book-card"
                );

        return card;
    }

    private ImageView createCoverView(
            Book book
    ) {
        ImageView imageView =
                new ImageView();

        imageView.setFitWidth(
                150
        );

        imageView.setFitHeight(
                210
        );

        imageView.setPreserveRatio(
                true
        );

        String coverPath =
                book.getCoverPath();

        if (coverPath == null
                || coverPath.isBlank()) {

            return imageView;
        }

        try {
            Path path =
                    Path.of(
                            coverPath
                    );

            if (Files.exists(
                    path
            )) {
                imageView.setImage(
                        new Image(
                                path.toUri()
                                        .toString()
                        )
                );

                return imageView;
            }

            String fileName =
                    path.getFileName()
                            .toString();

            var resource =
                    getClass()
                            .getResource(
                                    "/it/bookverse/images/covers/"
                                            + fileName
                            );

            if (resource != null) {
                imageView.setImage(
                        new Image(
                                resource.toExternalForm()
                        )
                );
            }

        } catch (RuntimeException ignored) {
            // Missing cover does not prevent
            // the purchased book from being displayed.
        }

        return imageView;
    }

    private void handleDownload(
            Book book
    ) {
        messageLabel.setText("");

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Download "
                        + book.getTitle()
        );

        fileChooser.setInitialFileName(
                createDownloadFileName(
                        book
                )
        );

        fileChooser
                .getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "PDF files",
                                "*.pdf"
                        )
                );

        File selectedFile =
                fileChooser.showSaveDialog(
                        null
                );

        if (selectedFile == null) {
            return;
        }

        try {
            Path downloadedFile =
                    downloadBookController
                            .downloadBook(
                                    book.getId(),
                                    selectedFile.toPath()
                            );

            showSuccess(
                    "Book downloaded successfully to: "
                            + downloadedFile
            );

        } catch (BookNotPurchasedException
                 | BookNotFoundException
                 | BookDownloadException
                 | IllegalArgumentException exception) {

            showError(
                    exception.getMessage()
            );
        }
    }

    private String createDownloadFileName(
            Book book
    ) {
        String safeTitle =
                book.getTitle()
                        .replaceAll(
                                "[^a-zA-Z0-9-_ ]",
                                ""
                        )
                        .trim()
                        .replace(
                                " ",
                                "-"
                        );

        if (safeTitle.isBlank()) {
            safeTitle = "book";
        }

        return safeTitle
                + ".pdf";
    }

    private void updateWalletBalance() {
        try {
            ViewWalletController.WalletDetails details =
                    viewWalletController
                            .viewWallet();

            walletBalanceLabel.setText(
                    "€ "
                            + details.balance()
            );

        } catch (IllegalStateException exception) {
            walletBalanceLabel.setText(
                    "€ --"
            );
        }
    }

    @FXML
    private void handleBackToLibrary() {
        sceneManager.showLibrary();
    }

    @FXML
    private void handleOpenProfile() {
        sceneManager.showProfile();
    }

    @FXML
    private void handleOpenWallet() {
        sceneManager.showWallet();
    }

    @FXML
    private void handleLogout() {
        loginController.logout();
        sceneManager.showLogin();
    }

    private void showSuccess(
            String message
    ) {
        messageLabel
                .getStyleClass()
                .remove(
                        ERROR_CLASS
                );

        if (!messageLabel
                .getStyleClass()
                .contains(
                        SUCCESS_CLASS
                )) {

            messageLabel
                    .getStyleClass()
                    .add(
                            SUCCESS_CLASS
                    );
        }

        messageLabel.setText(
                message
        );
    }

    private void showError(
            String message
    ) {
        messageLabel
                .getStyleClass()
                .remove(
                        SUCCESS_CLASS
                );

        if (!messageLabel
                .getStyleClass()
                .contains(
                        ERROR_CLASS
                )) {

            messageLabel
                    .getStyleClass()
                    .add(
                            ERROR_CLASS
                    );
        }

        messageLabel.setText(
                message
        );
    }
}