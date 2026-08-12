package it.bookverse.boundary;

import it.bookverse.navigation.SceneManager;
import it.bookverse.controller.RegisterController;
import it.bookverse.entity.Role;
import it.bookverse.exception.InvalidRegistrationDataException;
import it.bookverse.exception.UserAlreadyExistsException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;

public class RegistrationBoundary {

    private static final int MIN_BIRTH_YEAR = 1900;

    private final RegisterController registerController;
    private final SceneManager sceneManager;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ToggleButton readerToggleButton;

    @FXML
    private ToggleButton writerToggleButton;

    @FXML
    private ToggleGroup roleToggleGroup;

    @FXML
    private ComboBox<Integer> birthDayComboBox;

    @FXML
    private ComboBox<Month> birthMonthComboBox;

    @FXML
    private ComboBox<Integer> birthYearComboBox;

    @FXML
    private TextField penNameField;

    @FXML
    private TextArea bioArea;

    @FXML
    private TextField websiteField;

    @FXML
    private VBox readerFields;

    @FXML
    private VBox writerFields;

    @FXML
    private Label messageLabel;

    public RegistrationBoundary(
            RegisterController registerController,
            SceneManager sceneManager
    ) {
        this.registerController =
                registerController;

        this.sceneManager =
                sceneManager;
    }

    @FXML
    private void initialize() {
        readerToggleButton.setSelected(true);

        initializeBirthDateFields();

        updateRoleFields();

        roleToggleGroup.selectedToggleProperty()
                .addListener(
                        (observable,
                         previousToggle,
                         selectedToggle) -> {

                            if (selectedToggle == null) {
                                readerToggleButton
                                        .setSelected(true);

                                return;
                            }

                            updateRoleFields();
                        }
                );
    }

    private void initializeBirthDateFields() {

        birthMonthComboBox
                .getItems()
                .addAll(
                        Month.values()
                );

        int currentYear =
                Year.now(
                        ZoneId.systemDefault()
                ).getValue();

        for (int year = currentYear;
             year >= MIN_BIRTH_YEAR;
             year--) {

            birthYearComboBox
                    .getItems()
                    .add(year);
        }

        for (int day = 1;
             day <= 31;
             day++) {

            birthDayComboBox
                    .getItems()
                    .add(day);
        }

        birthMonthComboBox
                .valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                updateAvailableDays()
                );

        birthYearComboBox
                .valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                updateAvailableDays()
                );
    }

    private void updateAvailableDays() {

        Month selectedMonth =
                birthMonthComboBox.getValue();

        Integer selectedYear =
                birthYearComboBox.getValue();

        Integer previousDay =
                birthDayComboBox.getValue();

        if (selectedMonth == null
                || selectedYear == null) {

            return;
        }

        int maximumDay =
                YearMonth.of(
                        selectedYear,
                        selectedMonth
                ).lengthOfMonth();

        birthDayComboBox
                .getItems()
                .clear();

        for (int day = 1;
             day <= maximumDay;
             day++) {

            birthDayComboBox
                    .getItems()
                    .add(day);
        }

        if (previousDay != null
                && previousDay <= maximumDay) {

            birthDayComboBox.setValue(
                    previousDay
            );
        }
    }

    @FXML
    private void handleRegister() {
        messageLabel.setText("");

        try {
            Role selectedRole =
                    getSelectedRole();

            if (selectedRole == Role.READER) {
                registerReader();

            } else {
                registerWriter();
            }

            sceneManager.showLogin();

        } catch (InvalidRegistrationDataException
                 | UserAlreadyExistsException
                 | IllegalArgumentException exception) {

            messageLabel.setText(
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void handleBackToLogin() {
        sceneManager.showLogin();
    }

    private Role getSelectedRole() {

        if (writerToggleButton.isSelected()) {
            return Role.WRITER;
        }

        return Role.READER;
    }

    private void registerReader() {

        LocalDate birthDate =
                createBirthDate();

        registerController.registerReader(
                fullNameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                birthDate
        );
    }

    private LocalDate createBirthDate() {

        Integer day =
                birthDayComboBox.getValue();

        Month month =
                birthMonthComboBox.getValue();

        Integer year =
                birthYearComboBox.getValue();

        if (day == null
                || month == null
                || year == null) {

            throw new IllegalArgumentException(
                    "Please select your complete date of birth."
            );
        }

        try {
            return LocalDate.of(
                    year,
                    month,
                    day
            );

        } catch (DateTimeException exception) {

            throw new IllegalArgumentException(
                    "The selected date of birth is not valid."
            );
        }
    }

    private void registerWriter() {

        registerController.registerWriter(
                fullNameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                penNameField.getText(),
                bioArea.getText(),
                websiteField.getText()
        );
    }

    private void updateRoleFields() {

        boolean readerSelected =
                readerToggleButton.isSelected();

        readerFields.setVisible(
                readerSelected
        );

        readerFields.setManaged(
                readerSelected
        );

        writerFields.setVisible(
                !readerSelected
        );

        writerFields.setManaged(
                !readerSelected
        );
    }
}