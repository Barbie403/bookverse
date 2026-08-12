package it.bookverse.controller;

import it.bookverse.exception.InvalidRegistrationDataException;

import java.time.LocalDate;
import java.time.ZoneId;

public class ValidateDataController {

    public void validateCommonData(
            String fullName,
            String email,
            String password,
            String confirmPassword
    ) {
        if (isBlank(fullName)) {
            throw new InvalidRegistrationDataException(
                    "Full name is required."
            );
        }

        if (isBlank(email) || !email.contains("@")) {
            throw new InvalidRegistrationDataException(
                    "A valid email address is required."
            );
        }

        if (isBlank(password) || password.length() < 8) {
            throw new InvalidRegistrationDataException(
                    "Password must contain at least 8 characters."
            );
        }

        if (!password.equals(confirmPassword)) {
            throw new InvalidRegistrationDataException(
                    "Password and confirmation password do not match."
            );
        }
    }

    public void validateReaderData(LocalDate birthDate) {
        if (birthDate == null) {
            throw new InvalidRegistrationDataException(
                    "Birth date is required."
            );
        }

        if (birthDate.isAfter(
                LocalDate.now(ZoneId.systemDefault())
        )) {
            throw new InvalidRegistrationDataException(
                    "Birth date cannot be in the future."
            );
        }
    }

    public void validateWriterData(
            String penName,
            String bio
    ) {
        if (isBlank(penName)) {
            throw new InvalidRegistrationDataException(
                    "Pen name is required."
            );
        }

        if (isBlank(bio)) {
            throw new InvalidRegistrationDataException(
                    "Biography is required."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}