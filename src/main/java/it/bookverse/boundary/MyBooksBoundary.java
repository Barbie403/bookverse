package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.entity.Book;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.RemoveBookController;
import it.bookverse.controller.ViewMyBooksController;
import it.bookverse.controller.ViewWalletController;
import it.bookverse.exception.BookNotFoundException;
import it.bookverse.exception.UnauthorizedBookRemovalException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class MyBooksBoundary {

    private static final String SUCCESS_CLASS =
            "my-books-message-success";

    private static final String ERROR_CLASS =
            "my-books-message-error";

    private final ViewMyBooksController
            viewMyBooksController;

    private final RemoveBookController
            removeBookController;

    private final ViewWalletController
            viewWalletController;

    private final LoginController
            loginController;

    private final SceneManager
            sceneManager;

    @FXML
    private FlowPane booksContainer;

    @FXML
    private Label walletBalanceLabel;

    @FXML
    private Label messageLabel;

    public MyBooksBoundary(
            ViewMyBooksController viewMyBooksController,
            RemoveBookController removeBookController,
            ViewWalletController viewWalletController,
            LoginController loginController,
            SceneManager sceneManager
    ) {
        this.viewMyBooksController =
                viewMyBooksController;

        this.removeBookController =
                removeBookController;

        this.viewWalletController =
                viewWalletController;

        this.loginController =
                loginController;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        displayWalletBalance();
        loadMyBooks();
    }

    private void displayWalletBalance() {
        try {
            ViewWalletController.WalletDetails details =
                    viewWalletController.viewWallet();

            walletBalanceLabel.setText(
                    "€ "
                            + details.balance()
                            .toPlainString()
            );

        } catch (RuntimeException exception) {
            walletBalanceLabel.setText(
                    "€ 0.00"
            );
        }
    }

    private void loadMyBooks() {
        try {
            List<Book> books =
                    viewMyBooksController
                            .viewMyBooks();

            displayBooks(books);

        } catch (IllegalStateException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void displayBooks(
            List<Book> books
    ) {
        booksContainer
                .getChildren()
                .clear();

        if (books.isEmpty()) {
            clearMessageStyles();

            messageLabel.setText(
                    "You have not uploaded any books yet."
            );

            return;
        }

        clearMessage();

        for (Book book : books) {
            booksContainer
                    .getChildren()
                    .add(
                            createBookCard(book)
                    );
        }
    }

    private HBox createBookCard(
            Book book
    ) {
        StackPane coverContainer =
                createCoverContainer(book);

        Label titleLabel =
                new Label(
                        book.getTitle()
                );

        titleLabel.setWrapText(true);

        titleLabel.getStyleClass()
                .add(
                        "my-books-card-title"
                );

        Label categoryLabel =
                new Label(
                        formatCategory(book)
                );

        categoryLabel.getStyleClass()
                .add(
                        "my-books-category-badge"
                );

        Label audienceLabel =
                new Label(
                        book.isAdultsOnly()
                                ? "Adults only · 18+"
                                : "Everyone"
                );

        audienceLabel.getStyleClass()
                .add(
                        "my-books-card-detail"
                );

        Label priceLabel =
                new Label(
                        "Price: € "
                                + book.getPrice()
                                .toPlainString()
                );

        priceLabel.getStyleClass()
                .add(
                        "my-books-card-price"
                );

        Label statusLabel =
                new Label(
                        book.isAvailable()
                                ? "Available"
                                : "Removed"
                );

        statusLabel.getStyleClass()
                .add(
                        "my-books-status-badge"
                );

        if (book.isAvailable()) {
            statusLabel.getStyleClass()
                    .add(
                            "my-books-status-available"
                    );

        } else {
            statusLabel.getStyleClass()
                    .add(
                            "my-books-status-removed"
                    );
        }

        HBox metadataRow =
                new HBox(
                        10,
                        categoryLabel,
                        statusLabel
                );

        metadataRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox informationBox =
                new VBox(
                        10,
                        titleLabel,
                        metadataRow,
                        audienceLabel,
                        priceLabel
                );

        informationBox.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                informationBox,
                Priority.ALWAYS
        );

        Button removeButton =
                createRemoveButton(book);

        VBox actionBox =
                new VBox(
                        12,
                        removeButton
                );

        actionBox.setAlignment(
                Pos.CENTER
        );

        actionBox.setPrefWidth(
                195
        );

        HBox card =
                new HBox(
                        22,
                        coverContainer,
                        informationBox,
                        actionBox
                );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPrefWidth(
                800
        );

        card.setMaxWidth(
                800
        );

        card.getStyleClass()
                .add(
                        "my-books-card"
                );

        return card;
    }

    private Button createRemoveButton(
            Book book
    ) {
        Button removeButton =
                new Button();

        removeButton.setMaxWidth(
                Double.MAX_VALUE
        );

        if (book.isAvailable()) {
            removeButton.setText(
                    "Remove from Catalog"
            );

            removeButton.getStyleClass()
                    .add(
                            "my-books-remove-button"
                    );

            removeButton.setOnAction(event ->
                    confirmAndRemoveBook(book)
            );

        } else {
            removeButton.setText(
                    "Already Removed"
            );

            removeButton.setDisable(true);

            removeButton.getStyleClass()
                    .add(
                            "my-books-removed-button"
                    );
        }

        return removeButton;
    }

    private StackPane createCoverContainer(
            Book book
    ) {
        StackPane container =
                new StackPane();

        container.setPrefSize(
                115,
                160
        );

        container.setMinSize(
                115,
                160
        );

        container.setMaxSize(
                115,
                160
        );

        container.getStyleClass()
                .add(
                        "my-books-cover-container"
                );

        Image image =
                loadCoverImage(
                        book.getCoverPath()
                );

        if (image != null
                && !image.isError()) {

            ImageView imageView =
                    new ImageView(image);

            imageView.setFitWidth(
                    115
            );

            imageView.setFitHeight(
                    160
            );

            imageView.setPreserveRatio(
                    false
            );

            imageView.setSmooth(true);

            container.getChildren()
                    .add(imageView);

        } else {
            Label placeholder =
                    new Label(
                            createCoverText(
                                    book.getTitle()
                            )
                    );

            placeholder.setWrapText(true);
            placeholder.setMaxWidth(90);

            placeholder.getStyleClass()
                    .add(
                            "my-books-cover-placeholder"
                    );

            container.getChildren()
                    .add(placeholder);
        }

        return container;
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
                        file.toURI()
                                .toString(),
                        true
                );
            }

        } catch (RuntimeException exception) {
            return null;
        }

        return null;
    }

    private void confirmAndRemoveBook(
            Book book
    ) {
        Alert confirmationAlert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmationAlert.setTitle(
                "Remove Book"
        );

        confirmationAlert.setHeaderText(
                "Remove \""
                        + book.getTitle()
                        + "\" from the catalog?"
        );

        confirmationAlert.setContentText(
                "New readers will no longer see or purchase "
                        + "this book. Previous buyers will keep it."
        );

        URL stylesheet =
                getClass().getResource(
                        "/it/bookverse/bookverse.css"
                );

        if (stylesheet != null) {
            confirmationAlert
                    .getDialogPane()
                    .getStylesheets()
                    .add(
                            stylesheet.toExternalForm()
                    );

            confirmationAlert
                    .getDialogPane()
                    .getStyleClass()
                    .add(
                            "remove-book-dialog"
                    );
        }

        Optional<ButtonType> result =
                confirmationAlert
                        .showAndWait();

        if (result.isEmpty()
                || result.get() != ButtonType.OK) {

            return;
        }

        removeBook(
                book.getId()
        );
    }

    private void removeBook(
            String bookId
    ) {
        try {
            Book removedBook =
                    removeBookController
                            .removeFromCatalog(
                                    bookId
                            );

            /*
             * Reload first, then show the success message.
             * Otherwise loadMyBooks() would clear it.
             */
            loadMyBooks();

            showSuccess(
                    "\""
                            + removedBook.getTitle()
                            + "\" was removed from the catalog."
            );

        } catch (BookNotFoundException
                 | UnauthorizedBookRemovalException
                 | IllegalStateException exception) {

            showError(
                    exception.getMessage()
            );
        }
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
                .replace(
                        "_",
                        " "
                );
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
    private void handleOpenUploadBook() {
        sceneManager.showUploadBook();
    }

    @FXML
    private void handleLogout() {
        loginController.logout();
        sceneManager.showLogin();
    }

    private void showSuccess(
            String message
    ) {
        clearMessageStyles();

        messageLabel
                .getStyleClass()
                .add(
                        SUCCESS_CLASS
                );

        messageLabel.setText(message);
    }

    private void showError(
            String message
    ) {
        clearMessageStyles();

        messageLabel
                .getStyleClass()
                .add(
                        ERROR_CLASS
                );

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
}