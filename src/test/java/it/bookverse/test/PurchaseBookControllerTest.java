package it.bookverse.test;

import it.bookverse.entity.Book;
import it.bookverse.controller.PurchaseBookController;
import it.bookverse.entity.Audience;
import it.bookverse.entity.Category;
import it.bookverse.entity.Purchase;
import it.bookverse.entity.Reader;
import it.bookverse.entity.Writer;
import it.bookverse.exception.BookAlreadyPurchasedException;
import it.bookverse.exception.InsufficientBalanceException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.InMemoryBookRepository;
import it.bookverse.persistence.InMemoryPurchaseRepository;
import it.bookverse.persistence.InMemoryUserRepository;
import it.bookverse.persistence.PurchaseRepository;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Person in charge: Baran
 */
class PurchaseBookControllerTest {

    private UserRepository userRepository;
    private BookRepository bookRepository;
    private PurchaseRepository purchaseRepository;
    private UserSession userSession;

    private PurchaseBookController purchaseBookController;

    private Reader reader;
    private Writer writer;
    private Book book;

    @BeforeEach
    void setUp() {
        userRepository =
                new InMemoryUserRepository();

        bookRepository =
                new InMemoryBookRepository();

        purchaseRepository =
                new InMemoryPurchaseRepository();

        userSession =
                new UserSession();

        reader = new Reader(
                "reader-1",
                "Test Reader",
                "reader@test.com",
                "password123",
                LocalDate.of(2000, 1, 1),
                new BigDecimal("100.00")
        );

        writer = new Writer(
                "writer-1",
                "Test Writer",
                "writer@test.com",
                "password123",
                "Test Author",
                "Test biography",
                null
        );

        book = new Book(
                "book-1",
                "Test Book",
                writer.getId(),
                "A book used for testing.",
                new BigDecimal("20.00"),
                Audience.EVERYONE,
                Category.FICTION,
                "test-book.pdf",
                null
        );

        userRepository.save(reader);
        userRepository.save(writer);
        bookRepository.save(book);

        userSession.start(reader);

        purchaseBookController =
                new PurchaseBookController(
                        bookRepository,
                        userRepository,
                        purchaseRepository,
                        userSession
                );
    }

    @Test
    void purchaseBookWithSufficientBalanceCompletesPurchase() {
        Purchase purchase =
                purchaseBookController
                        .purchaseBook(book.getId());

        assertNotNull(purchase);

        assertEquals(
                reader.getId(),
                purchase.getReaderId()
        );

        assertEquals(
                book.getId(),
                purchase.getBookId()
        );

        assertEquals(
                new BigDecimal("20.00"),
                purchase.getPricePaid()
        );

        assertEquals(
                new BigDecimal("80.00"),
                reader.getWallet().getBalance()
        );

        assertEquals(
                new BigDecimal("20.00"),
                writer.getWallet().getBalance()
        );

        assertTrue(
                purchaseRepository
                        .existsByReaderIdAndBookId(
                                reader.getId(),
                                book.getId()
                        )
        );
    }

    @Test
    void purchaseBookWithInsufficientBalanceThrowsException() {
        Reader poorReader = new Reader(
                "reader-2",
                "Poor Reader",
                "poor@test.com",
                "password123",
                LocalDate.of(2000, 1, 1),
                new BigDecimal("5.00")
        );

        userRepository.save(poorReader);
        userSession.start(poorReader);

        assertThrows(
                InsufficientBalanceException.class,
                () -> purchaseBookController
                        .purchaseBook(book.getId())
        );

        assertEquals(
                new BigDecimal("5.00"),
                poorReader.getWallet().getBalance()
        );

        assertFalse(
                purchaseRepository
                        .existsByReaderIdAndBookId(
                                poorReader.getId(),
                                book.getId()
                        )
        );
    }

    @Test
    void purchaseBookWhenAlreadyPurchasedThrowsException() {
        Purchase existingPurchase =
                new Purchase(
                        "purchase-1",
                        reader.getId(),
                        book.getId(),
                        book.getPrice(),
                        LocalDateTime.now()
                );

        purchaseRepository.save(
                existingPurchase
        );

        assertThrows(
                BookAlreadyPurchasedException.class,
                () -> purchaseBookController
                        .purchaseBook(book.getId())
        );

        assertEquals(
                new BigDecimal("100.00"),
                reader.getWallet().getBalance()
        );
    }
}