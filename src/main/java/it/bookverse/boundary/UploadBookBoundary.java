package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.entity.Book;
import it.bookverse.controller.UploadBookController;
import it.bookverse.entity.Audience;
import it.bookverse.entity.Category;
import it.bookverse.exception.InvalidBookDataException;
import it.bookverse.exception.UnauthorizedUploadException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;

public class UploadBookBoundary {

    private static final String SUCCESS_CLASS =
            "upload-message-success";

    private static final String ERROR_CLASS =
            "upload-message-error";

    private final UploadBookController
            uploadBookController;

    private final SceneManager
            sceneManager;

    private Path selectedPdfFile;
    private Path selectedCoverFile;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<Audience> audienceComboBox;

    @FXML
    private Label pdfFileLabel;

    @FXML
    private Label coverFileLabel;

    @FXML
    private Label messageLabel;

    public UploadBookBoundary(
            UploadBookController uploadBookController,
            SceneManager sceneManager
    ) {
        this.uploadBookController =
                uploadBookController;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        categoryComboBox
                .getItems()
                .addAll(
                        Category.values()
                );

        audienceComboBox
                .getItems()
                .addAll(
                        Audience.values()
                );

        audienceComboBox.setValue(
                Audience.EVERYONE
        );
    }

    @FXML
    private void handleChoosePdf() {
        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Select book PDF"
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
                fileChooser.showOpenDialog(
                        null
                );

        if (selectedFile == null) {
            return;
        }

        selectedPdfFile =
                selectedFile.toPath();

        pdfFileLabel.setText(
                selectedFile.getName()
        );

        clearMessage();
    }

    @FXML
    private void handleChooseCover() {
        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Select book cover"
        );

        fileChooser
                .getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Image files",
                                "*.png",
                                "*.jpg",
                                "*.jpeg"
                        )
                );

        File selectedFile =
                fileChooser.showOpenDialog(
                        null
                );

        if (selectedFile == null) {
            return;
        }

        selectedCoverFile =
                selectedFile.toPath();

        coverFileLabel.setText(
                selectedFile.getName()
        );

        clearMessage();
    }

    @FXML
    private void handleUpload() {
        clearMessage();

        try {
            Book uploadedBook =
                    uploadBookController
                            .uploadBook(
                                    titleField.getText(),
                                    descriptionArea.getText(),
                                    priceField.getText(),
                                    audienceComboBox.getValue(),
                                    categoryComboBox.getValue(),
                                    selectedPdfFile,
                                    selectedCoverFile
                            );

            showSuccess(
                    "\""
                            + uploadedBook.getTitle()
                            + "\" was published successfully."
            );

            clearForm();

        } catch (InvalidBookDataException
                 | UnauthorizedUploadException
                 | IllegalStateException exception) {

            showError(
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void handleCancel() {
        sceneManager.showMyBooks();
    }

    @FXML
    private void handleBackToLibrary() {
        sceneManager.showMyBooks();
    }

    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        priceField.clear();

        categoryComboBox.setValue(
                null
        );

        audienceComboBox.setValue(
                Audience.EVERYONE
        );

        selectedPdfFile = null;
        selectedCoverFile = null;

        pdfFileLabel.setText(
                "No PDF selected"
        );

        coverFileLabel.setText(
                "No cover selected"
        );
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
                        ? "The book could not be published."
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