package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.entity.Book;
import it.bookverse.controller.BrowseBooksController;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.SearchBooksController;
import it.bookverse.controller.UploadBookController;
import it.bookverse.controller.ViewWalletController;
import it.bookverse.entity.Role;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.List;

public class LibraryBoundary {

    private static final double CARD_WIDTH =
            190;

    private static final double COVER_WIDTH =
            158;

    private static final double COVER_HEIGHT =
            215;

    private final BrowseBooksController
            browseBooksController;

    private final SearchBooksController
            searchBooksController;

    private final UploadBookController
            uploadBookController;

    private final ViewWalletController
            viewWalletController;

    private final LoginController
            loginController;

    private final SceneManager
            sceneManager;

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane booksContainer;

    @FXML
    private Label messageLabel;

    @FXML
    private Label walletBalanceLabel;

    @FXML
    private Label accountNameLabel;

    @FXML
    private Label accountRoleLabel;

    @FXML
    private Button uploadBookButton;

    @FXML
    private Button myBooksButton;

    @FXML
    private Button myLibraryButton;

    public LibraryBoundary(
            BrowseBooksController browseBooksController,
            SearchBooksController searchBooksController,
            UploadBookController uploadBookController,
            ViewWalletController viewWalletController,
            LoginController loginController,
            SceneManager sceneManager
    ) {
        this.browseBooksController =
                browseBooksController;

        this.searchBooksController =
                searchBooksController;

        this.uploadBookController =
                uploadBookController;

        this.viewWalletController =
                viewWalletController;

        this.loginController =
                loginController;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        displayBooks(
                browseBooksController.browseBooks()
        );

        displayUserInformation();

        searchField.textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                handleSearch()
                );

        configureRoleSpecificButtons();
    }

    private void displayUserInformation() {
        try {
            ViewWalletController.WalletDetails details =
                    viewWalletController.viewWallet();

            walletBalanceLabel.setText(
                    "€ "
                            + details.balance()
                            .toPlainString()
            );

            accountNameLabel.setText(
                    "Welcome, "
                            + details.fullName()
            );

            accountRoleLabel.setText(
                    formatRole(
                            details.role()
                    )
            );

        } catch (RuntimeException exception) {
            walletBalanceLabel.setText(
                    "€ 0.00"
            );

            accountNameLabel.setText(
                    "Welcome"
            );

            accountRoleLabel.setText(
                    "BookVerse user"
            );
        }
    }

    private String formatRole(
            Role role
    ) {
        if (role == Role.READER) {
            return "Reader";
        }

        return "Writer";
    }

    private void configureRoleSpecificButtons() {
        boolean writer =
                uploadBookController
                        .isCurrentUserWriter();

        uploadBookButton.setVisible(writer);
        uploadBookButton.setManaged(writer);

        myBooksButton.setVisible(writer);
        myBooksButton.setManaged(writer);

        myLibraryButton.setVisible(!writer);
        myLibraryButton.setManaged(!writer);
    }

    @FXML
    private void handleSearch() {
        List<Book> searchResults =
                searchBooksController.searchBooks(
                        searchField.getText()
                );

        displayBooks(searchResults);
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();

        displayBooks(
                browseBooksController.browseBooks()
        );
    }

    @FXML
    private void handleFantasyFilter() {
        applyCategoryFilter(
                "fantasy"
        );
    }

    @FXML
    private void handleFictionFilter() {
        applyCategoryFilter(
                "fiction"
        );
    }

    @FXML
    private void handleRomanceFilter() {
        applyCategoryFilter(
                "romance"
        );
    }

    @FXML
    private void handleScienceFictionFilter() {
        applyCategoryFilter(
                "science fiction"
        );
    }

    private void applyCategoryFilter(
            String category
    ) {
        searchField.setText(
                category
        );

        displayBooks(
                searchBooksController
                        .searchBooks(
                                category
                        )
        );
    }

    @FXML
    private void handleOpenUploadBook() {
        sceneManager.showUploadBook();
    }

    @FXML
    private void handleOpenMyBooks() {
        sceneManager.showMyBooks();
    }

    @FXML
    private void handleOpenPurchasedBooks() {
        sceneManager.showPurchasedBooks();
    }

    @FXML
    private void handleOpenWallet() {
        sceneManager.showWallet();
    }

    @FXML
    private void handleOpenProfile() {
        sceneManager.showProfile();
    }

    @FXML
    private void handleLogout() {
        loginController.logout();
        sceneManager.showLogin();
    }

    private void displayBooks(
            List<Book> books
    ) {
        booksContainer
                .getChildren()
                .clear();

        if (books.isEmpty()) {
            messageLabel.setText(
                    "No books match your search."
            );

            return;
        }

        messageLabel.setText("");

        for (Book book : books) {
            booksContainer
                    .getChildren()
                    .add(
                            createBookCard(book)
                    );
        }
    }

    private VBox createBookCard(
            Book book
    ) {
        StackPane coverContainer =
                createCoverContainer(book);

        Label titleLabel =
                new Label(
                        book.getTitle()
                );

        titleLabel.setWrapText(true);

        titleLabel.setMaxWidth(
                CARD_WIDTH - 28
        );

        titleLabel
                .getStyleClass()
                .add(
                        "book-card-title"
                );

        Label categoryLabel =
                new Label(
                        formatCategory(book)
                );

        categoryLabel
                .getStyleClass()
                .add(
                        "book-category-chip"
                );

        Label audienceLabel =
                new Label(
                        book.isAdultsOnly()
                                ? "18+"
                                : "Everyone"
                );

        audienceLabel
                .getStyleClass()
                .add(
                        "book-card-audience"
                );

        Label priceLabel =
                new Label(
                        "€ "
                                + book.getPrice()
                                .toPlainString()
                );

        priceLabel
                .getStyleClass()
                .add(
                        "book-card-price"
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox bottomRow =
                new HBox(
                        6,
                        categoryLabel,
                        spacer,
                        priceLabel
                );

        bottomRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox card =
                new VBox(
                        10,
                        coverContainer,
                        titleLabel,
                        audienceLabel,
                        bottomRow
                );

        card.setPrefWidth(
                CARD_WIDTH
        );

        card.setMinWidth(
                CARD_WIDTH
        );

        card.setMaxWidth(
                CARD_WIDTH
        );

        card.setCursor(
                Cursor.HAND
        );

        card.getStyleClass()
                .add(
                        "book-card"
                );

        card.setOnMouseClicked(event ->
                sceneManager.showBookDetails(
                        book.getId()
                )
        );

        return card;
    }

    private StackPane createCoverContainer(
            Book book
    ) {
        StackPane container =
                new StackPane();

        container.setPrefSize(
                COVER_WIDTH,
                COVER_HEIGHT
        );

        container.setMinSize(
                COVER_WIDTH,
                COVER_HEIGHT
        );

        container.setMaxSize(
                COVER_WIDTH,
                COVER_HEIGHT
        );

        container
                .getStyleClass()
                .add(
                        "book-cover-container"
                );

        Image image =
                loadCoverImage(
                        book.getCoverPath()
                );

        if (image != null
                && !image.isError()) {

            ImageView imageView =
                    new ImageView(
                            image
                    );

            imageView.setFitWidth(
                    COVER_WIDTH
            );

            imageView.setFitHeight(
                    COVER_HEIGHT
            );

            imageView.setPreserveRatio(
                    false
            );

            imageView.setSmooth(
                    true
            );

            container
                    .getChildren()
                    .add(
                            imageView
                    );

        } else {
            Label placeholder =
                    new Label(
                            createCoverText(
                                    book.getTitle()
                            )
                    );

            placeholder.setWrapText(
                    true
            );

            placeholder.setMaxWidth(
                    125
            );

            placeholder
                    .getStyleClass()
                    .add(
                            "book-cover-placeholder"
                    );

            container
                    .getChildren()
                    .add(
                            placeholder
                    );
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
                    new File(
                            coverPath
                    );

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
}