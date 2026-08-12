package it.bookverse;

import it.bookverse.entity.Book;
import it.bookverse.controller.BrowseBooksController;
import it.bookverse.controller.DownloadBookController;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.PurchaseBookController;
import it.bookverse.controller.RegisterController;
import it.bookverse.controller.RemoveBookController;
import it.bookverse.controller.SearchBooksController;
import it.bookverse.controller.TopUpWalletController;
import it.bookverse.controller.UploadBookController;
import it.bookverse.controller.ValidateDataController;
import it.bookverse.controller.ViewBookDetailsController;
import it.bookverse.controller.ViewMyBooksController;
import it.bookverse.controller.ViewProfileController;
import it.bookverse.controller.ViewPurchasedBooksController;
import it.bookverse.controller.ViewWalletController;
import it.bookverse.entity.Audience;
import it.bookverse.entity.Category;
import it.bookverse.entity.Writer;
import it.bookverse.payment.MockPaymentGateway;
import it.bookverse.payment.PaymentGateway;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.PersistenceMode;
import it.bookverse.persistence.PurchaseRepository;
import it.bookverse.persistence.RepositoryBundle;
import it.bookverse.persistence.RepositoryFactory;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationContext {

    private final Map<PersistenceMode, RepositoryBundle>
            repositoryBundles;

    private PersistenceMode currentMode;
    private UserSession userSession;

    private LoginController loginController;
    private RegisterController registerController;
    private BrowseBooksController browseBooksController;
    private SearchBooksController searchBooksController;
    private UploadBookController uploadBookController;
    private ViewMyBooksController viewMyBooksController;
    private RemoveBookController removeBookController;
    private ViewBookDetailsController viewBookDetailsController;
    private PurchaseBookController purchaseBookController;

    private ViewPurchasedBooksController
            viewPurchasedBooksController;

    private DownloadBookController downloadBookController;
    private ViewWalletController viewWalletController;
    private TopUpWalletController topUpWalletController;
    private ViewProfileController viewProfileController;

    public ApplicationContext() {

        repositoryBundles =
                new EnumMap<>(
                        PersistenceMode.class
                );

        selectPersistenceMode(
                PersistenceMode.IN_MEMORY
        );
    }

    public void selectPersistenceMode(
            PersistenceMode mode
    ) {
        Objects.requireNonNull(
                mode,
                "Persistence mode cannot be null."
        );

        if (mode == currentMode
                && loginController != null) {

            return;
        }

        RepositoryBundle repositories =
                repositoryBundles.computeIfAbsent(
                        mode,
                        this::createAndInitializeRepositories
                );

        currentMode = mode;

        createControllers(
                repositories
        );
    }

    private RepositoryBundle createAndInitializeRepositories(
            PersistenceMode mode
    ) {

        RepositoryFactory factory =
                RepositoryFactory.getFactory(
                        mode
                );

        RepositoryBundle repositories =
                factory.createRepositories();

        initializeDataIfNecessary(
                repositories
        );

        return repositories;
    }

    private void initializeDataIfNecessary(
            RepositoryBundle repositories
    ) {

        if (repositories
                .userRepository()
                .findAll()
                .isEmpty()) {

            loadDemoWriters(
                    repositories.userRepository()
            );
        }

        if (repositories
                .bookRepository()
                .findAll()
                .isEmpty()) {

            loadDemoBooks(
                    repositories.bookRepository()
            );
        }
    }

    private void createControllers(
            RepositoryBundle repositories
    ) {

        UserRepository userRepository =
                repositories.userRepository();

        BookRepository bookRepository =
                repositories.bookRepository();

        PurchaseRepository purchaseRepository =
                repositories.purchaseRepository();

        userSession =
                new UserSession();

        PaymentGateway paymentGateway =
                new MockPaymentGateway();

        ValidateDataController validateDataController =
                new ValidateDataController();

        registerController =
                new RegisterController(
                        userRepository,
                        validateDataController
                );

        loginController =
                new LoginController(
                        userRepository,
                        userSession
                );

        browseBooksController =
                new BrowseBooksController(
                        bookRepository,
                        userSession
                );

        searchBooksController =
                new SearchBooksController(
                        browseBooksController
                );

        uploadBookController =
                new UploadBookController(
                        bookRepository,
                        userSession
                );

        viewMyBooksController =
                new ViewMyBooksController(
                        bookRepository,
                        userSession
                );

        removeBookController =
                new RemoveBookController(
                        bookRepository,
                        userSession
                );

        viewBookDetailsController =
                new ViewBookDetailsController(
                        bookRepository,
                        userRepository
                );

        purchaseBookController =
                new PurchaseBookController(
                        bookRepository,
                        userRepository,
                        purchaseRepository,
                        userSession
                );

        viewPurchasedBooksController =
                new ViewPurchasedBooksController(
                        purchaseRepository,
                        bookRepository,
                        userSession
                );

        downloadBookController =
                new DownloadBookController(
                        bookRepository,
                        purchaseRepository,
                        userSession
                );

        viewWalletController =
                new ViewWalletController(
                        userSession
                );

        topUpWalletController =
                new TopUpWalletController(
                        userRepository,
                        userSession,
                        paymentGateway
                );

        viewProfileController =
                new ViewProfileController(
                        userSession
                );
    }

    public PersistenceMode getCurrentMode() {
        return currentMode;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public RegisterController getRegisterController() {
        return registerController;
    }

    public BrowseBooksController getBrowseBooksController() {
        return browseBooksController;
    }

    public SearchBooksController getSearchBooksController() {
        return searchBooksController;
    }

    public UploadBookController getUploadBookController() {
        return uploadBookController;
    }

    public ViewMyBooksController getViewMyBooksController() {
        return viewMyBooksController;
    }

    public RemoveBookController getRemoveBookController() {
        return removeBookController;
    }

    public ViewBookDetailsController
    getViewBookDetailsController() {
        return viewBookDetailsController;
    }

    public PurchaseBookController
    getPurchaseBookController() {
        return purchaseBookController;
    }

    public ViewPurchasedBooksController
    getViewPurchasedBooksController() {
        return viewPurchasedBooksController;
    }

    public DownloadBookController
    getDownloadBookController() {
        return downloadBookController;
    }

    public ViewWalletController
    getViewWalletController() {
        return viewWalletController;
    }

    public TopUpWalletController
    getTopUpWalletController() {
        return topUpWalletController;
    }

    public ViewProfileController
    getViewProfileController() {
        return viewProfileController;
    }

    private void loadDemoWriters(
            UserRepository userRepository
    ) {

        userRepository.save(
                createDemoWriter(
                        "writer-1",
                        "Jane Austen"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-2",
                        "F. Scott Fitzgerald"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-3",
                        "George Orwell"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-4",
                        "J. K. Rowling"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-5",
                        "Frank Herbert"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-6",
                        "Arthur Conan"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-7",
                        "Charlotte Bronte"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-8",
                        "Leo Tolstoy"
                )
        );

        userRepository.save(
                createDemoWriter(
                        "writer-9",
                        "Fyodor Dostoevsky"
                )
        );
    }

    private Writer createDemoWriter(
            String id,
            String penName
    ) {

        String emailName =
                id.replace(
                        "-",
                        ""
                );

        return new Writer(
                id,
                penName,
                emailName
                        + "@bookverse.demo",
                "password123",
                penName,
                "Demo writer profile.",
                null
        );
    }

    private void loadDemoBooks(
            BookRepository bookRepository
    ) {

        bookRepository.save(
                new Book(
                        "book-1",
                        "Pride and Prejudice",
                        "writer-1",
                        "Published in 1813 by Jane Austen, Pride and Prejudice follows witty protagonist Elizabeth Bennet as she navigates 19th-century English society. She clashes with the wealthy, proud Mr. Darcy. Over time, both overcome their flaws—his pride and her prejudice—realizing their true feelings and marrying for love",
                        new BigDecimal(
                                "15.00"
                        ),
                        Audience.EVERYONE,
                        Category.ROMANCE,
                        "data/books/pride-and-prejudice.pdf",
                        "/it/bookverse/images/covers/pride-and-prejudice.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-2",
                        "The Great Gatsby",
                        "writer-2",
                        "F. Scott Fitzgerald's 1925 classic novel The Great Gatsby follows narrator Nick Carraway as he moves to Long Island. He befriends his wealthy, mysterious neighbor Jay Gatsby, who throws huge parties in a desperate bid to win back his former love, Nick’s cousin Daisy Buchanan, now married to the arrogant Tom Buchanan",
                        new BigDecimal(
                                "15.00"
                        ),
                        Audience.EVERYONE,
                        Category.FICTION,
                        "data/books/the-great-gatsby.pdf",
                        "/it/bookverse/images/covers/the-great-gatsby.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-3",
                        "1984",
                        "writer-3",
                        "George Orwell’s 1984 is a classic dystopian novel about Winston Smith, a man rebelling against an all-controlling totalitarian government. Set in London, Oceania, society is monitored constantly by Big Brother and the Thought Police, where independent thought is illegal and history is rewritten daily",
                        new BigDecimal(
                                "18.00"
                        ),
                        Audience.ADULTS_ONLY,
                        Category.FICTION,
                        "data/books/1984.pdf",
                        "/it/bookverse/images/covers/1984.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-4",
                        "Harry Potter and the Sorcerer's Stone",
                        "writer-4",
                        "Harry Potter and the Sorcerer's Stone by J.K. Rowling follows an 11-year-old orphan who discovers he is a wizard. He attends Hogwarts School of Witchcraft and Wizardry, makes close friends, learns about his magical past, and stops the dark wizard Lord Voldemort from stealing a powerful magical item",
                        new BigDecimal(
                                "14.00"
                        ),
                        Audience.EVERYONE,
                        Category.FANTASY,
                        "data/books/harry-potter.pdf",
                        "/it/bookverse/images/covers/harry-potter.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-5",
                        "Dune",
                        "writer-5",
                        "Frank Herbert’s 1965 classic sci-fi novel Dune follows young Paul Atreides. His family moves to Arrakis, a harsh desert planet that is the universe's only source of \"spice\" melange, a drug vital for space travel. Betrayed by rivals, Paul flees into the desert, unites with the native Fremen, and leads a revolution to reclaim the planet",
                        new BigDecimal(
                                "20.00"
                        ),
                        Audience.EVERYONE,
                        Category.SCIENCE_FICTION,
                        "data/books/dune.pdf",
                        "/it/bookverse/images/covers/dune.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-6",
                        "Sherlock Holmes",
                        "writer-6",
                        "The Adventures of Sherlock Holmes, features 12 short stories about the brilliant London detective Sherlock Holmes and his loyal friend Dr. John Watson. Using sharp logic and close observation, Holmes solves baffling crimes, ranging from blackmail and theft to mysterious murders.",
                        new BigDecimal(
                                "16.00"
                        ),
                        Audience.EVERYONE,
                        Category.ADVENTURE,
                        "data/books/sherlock-holmes.pdf",
                        "/it/bookverse/images/covers/sherlock-holmes.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-7",
                        "Jane Eyre",
                        "writer-7",
                        " is a classic 1847 novel following an orphan girl's journey to independence. She survives a harsh childhood, finds love and a dark secret as a governess at Thornfield Hall, flees a disastrous wedding, and ultimately returns on her own terms to find maturity, family, and a reunited life with Mr. Rochester.",
                        new BigDecimal(
                                "21.00"
                        ),
                        Audience.EVERYONE,
                        Category.ROMANCE,
                        "data/books/jane-eyre.pdf",
                        "/it/bookverse/images/covers/jane-eyre.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-8",
                        "War And Peace",
                        "writer-8",
                        "follows five aristocratic Russian families—chiefly the Bezukhovs, Bolkonskys, and Rostovs—from 1805 to 1820. It contrasts the personal, social lives of society (peace) with the brutal French invasion led by Napoleon (war), charting characters as they search for life's true meaning",
                        new BigDecimal(
                                "24.00"
                        ),
                        Audience.EVERYONE,
                        Category.ADVENTURE,
                        "data/books/war-and-peace.pdf",
                        "/it/bookverse/images/covers/war-and-peace.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-9",
                        "White Nights",
                        "writer-9",
                        "A lonely, nameless young man known as \"the Dreamer\" meets a weeping young woman named Nastenka. Over four magical nights, they share their life stories and grow close, but her heart belongs to another, leading to heartbreak when her true love returns",
                        new BigDecimal(
                                "12.00"
                        ),
                        Audience.EVERYONE,
                        Category.FICTION,
                        "data/books/white-nights.pdf",
                        "/it/bookverse/images/covers/white-nights.jpg"
                )
        );
    }
}
