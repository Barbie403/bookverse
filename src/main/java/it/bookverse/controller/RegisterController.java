package it.bookverse.controller;

import it.bookverse.entity.Reader;
import it.bookverse.entity.Writer;
import it.bookverse.exception.UserAlreadyExistsException;
import it.bookverse.persistence.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

public class RegisterController {

    private final UserRepository userRepository;
    private final ValidateDataController validateDataController;

    public RegisterController(
            UserRepository userRepository,
            ValidateDataController validateDataController
    ) {
        this.userRepository = userRepository;
        this.validateDataController = validateDataController;
    }

    public Reader registerReader(
            String fullName,
            String email,
            String password,
            String confirmPassword,
            LocalDate birthDate
    ) {
        validateDataController.validateCommonData(
                fullName,
                email,
                password,
                confirmPassword
        );

        validateDataController.validateReaderData(birthDate);

        checkEmailAvailability(email);

        Reader reader = new Reader(
                UUID.randomUUID().toString(),
                fullName.trim(),
                email.trim().toLowerCase(),
                password,
                birthDate
        );

        userRepository.save(reader);

        return reader;
    }

    public Writer registerWriter(
            String fullName,
            String email,
            String password,
            String confirmPassword,
            String penName,
            String bio,
            String websiteOrSocial
    ) {
        validateDataController.validateCommonData(
                fullName,
                email,
                password,
                confirmPassword
        );

        validateDataController.validateWriterData(
                penName,
                bio
        );

        checkEmailAvailability(email);

        Writer writer = new Writer(
                UUID.randomUUID().toString(),
                fullName.trim(),
                email.trim().toLowerCase(),
                password,
                penName.trim(),
                bio.trim(),
                normalizeOptionalText(websiteOrSocial)
        );

        userRepository.save(writer);

        return writer;
    }

    private void checkEmailAvailability(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}