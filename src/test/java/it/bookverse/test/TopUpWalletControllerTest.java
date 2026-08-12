package it.bookverse.test;

import it.bookverse.controller.TopUpWalletController;
import it.bookverse.entity.Reader;
import it.bookverse.entity.Writer;
import it.bookverse.payment.MockPaymentGateway;
import it.bookverse.payment.PaymentGateway;
import it.bookverse.persistence.InMemoryUserRepository;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Person in charge: Baran
 */
class TopUpWalletControllerTest {

    private UserRepository userRepository;
    private UserSession userSession;
    private PaymentGateway paymentGateway;

    private TopUpWalletController topUpWalletController;

    private Reader reader;

    @BeforeEach
    void setUp() {
        userRepository =
                new InMemoryUserRepository();

        userSession =
                new UserSession();

        paymentGateway =
                new MockPaymentGateway();

        reader = new Reader(
                "reader-1",
                "Test Reader",
                "reader@test.com",
                "password123",
                LocalDate.of(2000, 1, 1),
                new BigDecimal("100.00")
        );

        userRepository.save(reader);

        userSession.start(reader);

        topUpWalletController =
                new TopUpWalletController(
                        userRepository,
                        userSession,
                        paymentGateway
                );
    }

    @Test
    void topUpWalletWithValidAmountIncreasesBalance() {
        TopUpWalletController.TopUpResult result =
                topUpWalletController.topUpWallet(
                        new BigDecimal("25.00")
                );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("25.00"),
                result.amountAdded()
        );

        assertEquals(
                new BigDecimal("125.00"),
                result.newBalance()
        );

        assertEquals(
                new BigDecimal("125.00"),
                reader.getWallet().getBalance()
        );

        assertNotNull(
                result.transactionId()
        );

        assertTrue(
                result.transactionId()
                        .startsWith("MOCK-")
        );
    }

    @Test
    void topUpWalletWithInvalidAmountThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> topUpWalletController
                        .topUpWallet(
                                BigDecimal.ZERO
                        )
        );

        assertEquals(
                new BigDecimal("100.00"),
                reader.getWallet().getBalance()
        );
    }

    @Test
    void topUpWalletWhenUserIsWriterThrowsException() {
        Writer writer = new Writer(
                "writer-1",
                "Test Writer",
                "writer@test.com",
                "password123",
                "Test Author",
                "Test biography",
                null
        );

        userRepository.save(writer);
        userSession.start(writer);

        assertFalse(
                topUpWalletController
                        .canCurrentUserTopUp()
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> topUpWalletController
                                .topUpWallet(
                                        new BigDecimal("20.00")
                                )
                );

        assertEquals(
                "Only readers can add funds to their wallet.",
                exception.getMessage()
        );

        assertEquals(
                BigDecimal.ZERO,
                writer.getWallet().getBalance()
        );
    }
}