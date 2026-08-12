package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.TopUpWalletController;
import it.bookverse.controller.TopUpWalletController.TopUpResult;
import it.bookverse.controller.ViewWalletController;
import it.bookverse.controller.ViewWalletController.WalletDetails;
import it.bookverse.entity.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class WalletBoundary {

    private static final String ERROR_CLASS =
            "wallet-message-error";

    private static final String SUCCESS_CLASS =
            "wallet-message-success";

    private static final List<BigDecimal>
            TOP_UP_AMOUNTS = List.of(
            new BigDecimal("10.00"),
            new BigDecimal("20.00"),
            new BigDecimal("50.00"),
            new BigDecimal("100.00")
    );

    private final ViewWalletController
            viewWalletController;

    private final TopUpWalletController
            topUpWalletController;

    private final LoginController
            loginController;

    private final SceneManager
            sceneManager;

    @FXML
    private Label ownerInitialLabel;

    @FXML
    private Label ownerNameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label howItWorksLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button addFundsButton;

    @FXML
    private Button myLibraryButton;

    @FXML
    private Button myBooksButton;

    @FXML
    private Button uploadBookButton;

    public WalletBoundary(
            ViewWalletController viewWalletController,
            TopUpWalletController topUpWalletController,
            LoginController loginController,
            SceneManager sceneManager
    ) {
        this.viewWalletController =
                viewWalletController;

        this.topUpWalletController =
                topUpWalletController;

        this.loginController =
                loginController;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        loadWallet();
    }

    private void loadWallet() {
        try {
            WalletDetails details =
                    viewWalletController
                            .viewWallet();

            displayWallet(details);

        } catch (IllegalStateException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void displayWallet(
            WalletDetails details
    ) {
        ownerNameLabel.setText(
                details.fullName()
        );

        ownerInitialLabel.setText(
                createInitial(
                        details.fullName()
                )
        );

        roleLabel.setText(
                formatRole(
                        details.role()
                )
        );

        updateBalanceLabels(
                details.balance()
        );

        boolean reader =
                details.role() == Role.READER;

        addFundsButton.setVisible(reader);
        addFundsButton.setManaged(reader);

        if (reader) {
            descriptionLabel.setText(
                    "This balance is available for purchasing books."
            );

            howItWorksLabel.setText(
                    "Readers can add funds through the simulated "
                            + "payment gateway and use their balance "
                            + "to purchase books."
            );

            myLibraryButton.setVisible(true);
            myLibraryButton.setManaged(true);

            myBooksButton.setVisible(false);
            myBooksButton.setManaged(false);

            uploadBookButton.setVisible(false);
            uploadBookButton.setManaged(false);

        } else {
            descriptionLabel.setText(
                    "This balance contains earnings received from book sales."
            );

            howItWorksLabel.setText(
                    "Writers receive earnings automatically whenever "
                            + "readers purchase one of their books."
            );

            myBooksButton.setVisible(true);
            myBooksButton.setManaged(true);

            uploadBookButton.setVisible(true);
            uploadBookButton.setManaged(true);

            myLibraryButton.setVisible(false);
            myLibraryButton.setManaged(false);
        }

        clearMessage();
    }

    @FXML
    private void handleAddFunds() {
        clearMessage();

        ChoiceDialog<BigDecimal> amountDialog =
                createAmountDialog();

        Optional<BigDecimal> selectedAmount =
                amountDialog.showAndWait();

        if (selectedAmount.isEmpty()) {
            return;
        }

        BigDecimal amount =
                selectedAmount.get();

        if (!showConfirmationDialog(amount)) {
            return;
        }

        try {
            TopUpResult result =
                    topUpWalletController
                            .topUpWallet(amount);

            updateBalanceLabels(
                    result.newBalance()
            );

            showSuccess(
                    "€ "
                            + result.amountAdded()
                            .toPlainString()
                            + " was added successfully. "
                            + "Transaction: "
                            + result.transactionId()
            );

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            showError(
                    exception.getMessage()
            );
        }
    }

    private ChoiceDialog<BigDecimal>
    createAmountDialog() {

        ChoiceDialog<BigDecimal> dialog =
                new ChoiceDialog<>(
                        TOP_UP_AMOUNTS.getFirst(),
                        TOP_UP_AMOUNTS
                );

        dialog.setTitle(
                "BookVerse - Add Funds"
        );

        dialog.setHeaderText(
                "Choose the amount to add"
        );

        dialog.setContentText(
                "Top-up amount:"
        );

        dialog.setResultConverter(buttonType -> {
            if (buttonType != ButtonType.OK) {
                return null;
            }

            return dialog
                    .getSelectedItem();
        });

        dialog.getDialogPane()
                .getStylesheets()
                .add(
                        getClass()
                                .getResource(
                                        "/it/bookverse/bookverse.css"
                                )
                                .toExternalForm()
                );

        dialog.getDialogPane()
                .getStyleClass()
                .add(
                        "top-up-dialog"
                );

        return dialog;
    }

    private boolean showConfirmationDialog(
            BigDecimal amount
    ) {
        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Mock Payment"
        );

        confirmation.setHeaderText(
                "Add € "
                        + amount.toPlainString()
                        + " to your wallet?"
        );

        confirmation.setContentText(
                "This is a simulated payment. "
                        + "No real card or bank transaction will occur."
        );

        confirmation.getDialogPane()
                .getStylesheets()
                .add(
                        getClass()
                                .getResource(
                                        "/it/bookverse/bookverse.css"
                                )
                                .toExternalForm()
                );

        confirmation.getDialogPane()
                .getStyleClass()
                .add(
                        "top-up-dialog"
                );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        return result.isPresent()
                && result.get() == ButtonType.OK;
    }

    private void updateBalanceLabels(
            BigDecimal balance
    ) {
        String formattedBalance =
                "€ "
                        + balance.toPlainString();

        balanceLabel.setText(
                formattedBalance
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

    @FXML
    private void handleBackToLibrary() {
        sceneManager.showLibrary();
    }

    @FXML
    private void handleOpenProfile() {
        sceneManager.showProfile();
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
    private void handleLogout() {
        loginController.logout();
        sceneManager.showLogin();
    }

    private String formatRole(
            Role role
    ) {
        if (role == Role.READER) {
            return "Reader wallet";
        }

        return "Writer wallet";
    }

    private void clearMessage() {
        clearMessageStyles();
        messageLabel.setText("");
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

        messageLabel.setText(
                message
        );
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

    private void clearMessageStyles() {
        messageLabel
                .getStyleClass()
                .removeAll(
                        ERROR_CLASS,
                        SUCCESS_CLASS
                );
    }
}