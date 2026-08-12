package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.ApplicationContext;
import it.bookverse.exception.InvalidCredentialsException;
import it.bookverse.persistence.PersistenceMode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class LoginBoundary {

    private static final String SELECTED_CARD_CLASS =
            "persistence-card-selected";

    private final ApplicationContext applicationContext;
    private final SceneManager sceneManager;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private RadioButton demoModeRadioButton;

    @FXML
    private RadioButton fileModeRadioButton;

    @FXML
    private ToggleGroup persistenceToggleGroup;

    @FXML
    private VBox demoModeCard;

    @FXML
    private VBox fileModeCard;

    @FXML
    private Label messageLabel;

    public LoginBoundary(
            ApplicationContext applicationContext,
            SceneManager sceneManager
    ) {
        this.applicationContext =
                applicationContext;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        PersistenceMode currentMode =
                applicationContext.getCurrentMode();

        if (currentMode
                == PersistenceMode.FILE_SYSTEM) {

            fileModeRadioButton.setSelected(true);

        } else {
            demoModeRadioButton.setSelected(true);
        }

        updatePersistenceCardStyle();

        persistenceToggleGroup
                .selectedToggleProperty()
                .addListener(
                        (observable,
                         oldToggle,
                         newToggle) ->
                                updatePersistenceCardStyle()
                );
    }

    @FXML
    private void handleSelectDemoMode() {
        demoModeRadioButton.setSelected(true);
    }

    @FXML
    private void handleSelectFileMode() {
        fileModeRadioButton.setSelected(true);
    }

    @FXML
    private void handleLogin() {
        messageLabel.setText("");

        try {
            applicationContext.selectPersistenceMode(
                    getSelectedPersistenceMode()
            );

            applicationContext
                    .getLoginController()
                    .login(
                            emailField.getText(),
                            passwordField.getText()
                    );

            sceneManager.showLibrary();

        } catch (InvalidCredentialsException exception) {
            messageLabel.setText(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            messageLabel.setText(
                    exception.getMessage() == null
                            ? "The operation could not be completed."
                            : exception.getMessage()
            );
        }
    }

    @FXML
    private void handleOpenRegistration() {
        messageLabel.setText("");

        try {
            applicationContext.selectPersistenceMode(
                    getSelectedPersistenceMode()
            );

            sceneManager.showRegistration();

        } catch (RuntimeException exception) {
            messageLabel.setText(
                    exception.getMessage() == null
                            ? "The operation could not be completed."
                            : exception.getMessage()
            );
        }
    }

    private PersistenceMode getSelectedPersistenceMode() {
        if (fileModeRadioButton.isSelected()) {
            return PersistenceMode.FILE_SYSTEM;
        }

        return PersistenceMode.IN_MEMORY;
    }

    private void updatePersistenceCardStyle() {
        demoModeCard
                .getStyleClass()
                .remove(SELECTED_CARD_CLASS);

        fileModeCard
                .getStyleClass()
                .remove(SELECTED_CARD_CLASS);

        if (fileModeRadioButton.isSelected()) {
            fileModeCard
                    .getStyleClass()
                    .add(SELECTED_CARD_CLASS);

        } else {
            demoModeCard
                    .getStyleClass()
                    .add(SELECTED_CARD_CLASS);
        }
    }
}