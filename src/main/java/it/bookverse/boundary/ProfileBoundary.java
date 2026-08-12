package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.ViewProfileController;
import it.bookverse.controller.ViewProfileController.ProfileDetails;
import it.bookverse.controller.ViewProfileController.ReaderProfileDetails;
import it.bookverse.controller.ViewProfileController.WriterProfileDetails;
import it.bookverse.controller.ViewWalletController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;

public class ProfileBoundary {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String ERROR_CLASS =
            "profile-message-error";

    private final ViewProfileController
            viewProfileController;

    private final ViewWalletController
            viewWalletController;

    private final LoginController
            loginController;

    private final SceneManager
            sceneManager;

    @FXML
    private Label profileInitialLabel;

    @FXML
    private Label profileNameHeaderLabel;

    @FXML
    private Label profileRoleHeaderLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private GridPane readerInformationPane;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label accountTypeLabel;

    @FXML
    private GridPane writerInformationPane;

    @FXML
    private Label penNameLabel;

    @FXML
    private Label biographyLabel;

    @FXML
    private Label websiteLabel;

    @FXML
    private Label walletBalanceLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button myLibraryButton;

    @FXML
    private Button myBooksButton;

    @FXML
    private Button uploadBookButton;

    public ProfileBoundary(
            ViewProfileController viewProfileController,
            ViewWalletController viewWalletController,
            LoginController loginController,
            SceneManager sceneManager
    ) {
        this.viewProfileController =
                viewProfileController;

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
        loadProfile();
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

    private void loadProfile() {
        try {
            ProfileDetails profileDetails =
                    viewProfileController
                            .viewProfile();

            if (profileDetails
                    instanceof ReaderProfileDetails readerDetails) {

                displayReaderProfile(
                        readerDetails
                );

                return;
            }

            if (profileDetails
                    instanceof WriterProfileDetails writerDetails) {

                displayWriterProfile(
                        writerDetails
                );

                return;
            }

            showError(
                    "Unsupported profile type."
            );

        } catch (IllegalStateException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void displayReaderProfile(
            ReaderProfileDetails details
    ) {
        configureProfileHeader(
                details.fullName(),
                "Reader"
        );

        fullNameLabel.setText(
                details.fullName()
        );

        emailLabel.setText(
                details.email()
        );

        roleLabel.setText(
                "Reader"
        );

        birthDateLabel.setText(
                details.birthDate()
                        .format(
                                DATE_FORMATTER
                        )
        );

        accountTypeLabel.setText(
                details.accountType()
        );

        readerInformationPane.setVisible(true);
        readerInformationPane.setManaged(true);

        writerInformationPane.setVisible(false);
        writerInformationPane.setManaged(false);

        myLibraryButton.setVisible(true);
        myLibraryButton.setManaged(true);

        myBooksButton.setVisible(false);
        myBooksButton.setManaged(false);

        uploadBookButton.setVisible(false);
        uploadBookButton.setManaged(false);

        clearMessage();
    }

    private void displayWriterProfile(
            WriterProfileDetails details
    ) {
        configureProfileHeader(
                details.fullName(),
                "Writer"
        );

        fullNameLabel.setText(
                details.fullName()
        );

        emailLabel.setText(
                details.email()
        );

        roleLabel.setText(
                "Writer"
        );

        penNameLabel.setText(
                details.penName()
        );

        biographyLabel.setText(
                safeText(
                        details.biography()
                )
        );

        websiteLabel.setText(
                safeText(
                        details.websiteOrSocial()
                )
        );

        writerInformationPane.setVisible(true);
        writerInformationPane.setManaged(true);

        readerInformationPane.setVisible(false);
        readerInformationPane.setManaged(false);

        myBooksButton.setVisible(true);
        myBooksButton.setManaged(true);

        uploadBookButton.setVisible(true);
        uploadBookButton.setManaged(true);

        myLibraryButton.setVisible(false);
        myLibraryButton.setManaged(false);

        clearMessage();
    }

    private void configureProfileHeader(
            String fullName,
            String role
    ) {
        profileNameHeaderLabel.setText(
                fullName
        );

        profileRoleHeaderLabel.setText(
                role
        );

        profileInitialLabel.setText(
                createInitial(fullName)
        );
    }

    private String createInitial(
            String fullName
    ) {
        if (fullName == null
                || fullName.isBlank()) {

            return "B";
        }

        return fullName
                .trim()
                .substring(0, 1)
                .toUpperCase();
    }

    private String safeText(
            String value
    ) {
        if (value == null
                || value.isBlank()) {

            return "Not provided";
        }

        return value;
    }

    @FXML
    private void handleBackToLibrary() {
        sceneManager.showLibrary();
    }

    @FXML
    private void handleOpenPurchasedBooks() {
        sceneManager.showPurchasedBooks();
    }

    @FXML
    private void handleOpenMyBooks() {
        sceneManager.showMyBooks();
    }

    @FXML
    private void handleOpenUploadBook() {
        sceneManager.showUploadBook();
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

    private void clearMessage() {
        messageLabel
                .getStyleClass()
                .remove(ERROR_CLASS);

        messageLabel.setText("");
    }

    private void showError(
            String message
    ) {
        messageLabel
                .getStyleClass()
                .remove(ERROR_CLASS);

        messageLabel
                .getStyleClass()
                .add(ERROR_CLASS);

        messageLabel.setText(
                message == null
                        ? "The profile could not be loaded."
                        : message
        );
    }
}