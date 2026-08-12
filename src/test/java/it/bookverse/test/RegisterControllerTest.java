package it.bookverse.test;

import it.bookverse.controller.RegisterController;
import it.bookverse.controller.ValidateDataController;
import it.bookverse.entity.Reader;
import it.bookverse.exception.InvalidRegistrationDataException;
import it.bookverse.exception.UserAlreadyExistsException;
import it.bookverse.persistence.InMemoryUserRepository;
import it.bookverse.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Person in charge: Baran
 */
class RegisterControllerTest {

    private UserRepository userRepository;
    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        userRepository =
                new InMemoryUserRepository();

        ValidateDataController validateDataController =
                new ValidateDataController();

        registerController =
                new RegisterController(
                        userRepository,
                        validateDataController
                );
    }

    @Test
    void registerReaderWithValidDataSavesReader() {
        Reader reader =
                registerController.registerReader(
                        "Test Reader",
                        "reader@test.com",
                        "password123",
                        "password123",
                        LocalDate.of(2000, 1, 1)
                );

        assertNotNull(reader);

        assertEquals(
                "Test Reader",
                reader.getFullName()
        );

        assertEquals(
                "reader@test.com",
                reader.getEmail()
        );

        assertTrue(
                userRepository
                        .findByEmail("reader@test.com")
                        .isPresent()
        );
    }

    @Test
    void registerReaderWithDuplicateEmailThrowsException() {
        registerController.registerReader(
                "First Reader",
                "duplicate@test.com",
                "password123",
                "password123",
                LocalDate.of(2000, 1, 1)
        );

        assertThrows(
                UserAlreadyExistsException.class,
                () -> registerController.registerReader(
                        "Second Reader",
                        "duplicate@test.com",
                        "password456",
                        "password456",
                        LocalDate.of(2001, 1, 1)
                )
        );
    }

    @Test
    void registerReaderWithInvalidEmailThrowsException() {
        assertThrows(
                InvalidRegistrationDataException.class,
                () -> registerController.registerReader(
                        "Test Reader",
                        "invalid-email",
                        "password123",
                        "password123",
                        LocalDate.of(2000, 1, 1)
                )
        );

        assertTrue(
                userRepository.findAll().isEmpty()
        );
    }
}