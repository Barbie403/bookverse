package it.bookverse.console;

import it.bookverse.entity.Book;
import it.bookverse.controller.BrowseBooksController;
import it.bookverse.controller.DownloadBookController;
import it.bookverse.controller.LoginController;
import it.bookverse.controller.PurchaseBookController;
import it.bookverse.controller.RegisterController;
import it.bookverse.controller.RemoveBookController;
import it.bookverse.controller.SearchBooksController;
import it.bookverse.controller.UploadBookController;
import it.bookverse.controller.ValidateDataController;
import it.bookverse.controller.ViewBookDetailsController;
import it.bookverse.controller.ViewMyBooksController;
import it.bookverse.controller.ViewProfileController;
import it.bookverse.controller.ViewPurchasedBooksController;
import it.bookverse.controller.ViewWalletController;
import it.bookverse.entity.Audience;
import it.bookverse.entity.Category;
import it.bookverse.entity.Purchase;
import it.bookverse.entity.Reader;
import it.bookverse.entity.User;
import it.bookverse.entity.Writer;
import it.bookverse.exception.InvalidCredentialsException;
import it.bookverse.exception.InvalidRegistrationDataException;
import it.bookverse.exception.UserAlreadyExistsException;
import it.bookverse.persistence.BookRepository;
import it.bookverse.persistence.PersistenceMode;
import it.bookverse.persistence.PurchaseRepository;
import it.bookverse.persistence.RepositoryBundle;
import it.bookverse.persistence.RepositoryFactory;
import it.bookverse.persistence.UserRepository;
import it.bookverse.session.UserSession;

import java.math.BigDecimal;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    private final Scanner scanner;
    private PersistenceMode persistenceMode;
    private UserSession userSession;

    private RegisterController registerController;
    private LoginController loginController;
    private BrowseBooksController browseBooksController;
    private SearchBooksController searchBooksController;
    private ViewBookDetailsController viewBookDetailsController;
    private PurchaseBookController purchaseBookController;

    private ViewPurchasedBooksController
            viewPurchasedBooksController;

    private DownloadBookController downloadBookController;
    private ViewWalletController viewWalletController;
    private ViewProfileController viewProfileController;
    private UploadBookController uploadBookController;
    private ViewMyBooksController viewMyBooksController;
    private RemoveBookController removeBookController;

    public ConsoleApp() {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        ConsoleApp consoleApp =
                new ConsoleApp();

        consoleApp.start();
    }

    public void start() {
        printWelcomeMessage();

        PersistenceMode selectedMode =
                choosePersistenceMode();

        if (selectedMode == null) {
            System.out.println(
                    "\nThank you for using BookVerse."
            );

            scanner.close();
            return;
        }

        initializeApplication(selectedMode);

        boolean running = true;

        while (running) {
            printGuestMenu();

            int choice = readInteger(
                    "Choose an option: "
            );

            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegistration();

                case 0 -> {
                    running = false;

                    System.out.println(
                            "\nThank you for using BookVerse."
                    );
                }

                default -> System.out.println(
                        "\nInvalid option. Please choose 0, 1 or 2."
                );
            }
        }

        scanner.close();
    }

    private PersistenceMode choosePersistenceMode() {
        while (true) {
            System.out.println(
                    "\n------ PERSISTENCE MODE ------"
            );

            System.out.println(
                    "1. Demo - In Memory"
            );

            System.out.println(
                    "2. File - Text Files"
            );

            System.out.println(
                    "0. Exit"
            );

            System.out.println(
                    "------------------------------"
            );

            int choice = readInteger(
                    "Choose an option: "
            );

            switch (choice) {
                case 1:
                    return PersistenceMode.IN_MEMORY;

                case 2:
                    return PersistenceMode.FILE_SYSTEM;

                case 0:
                    return null;

                default:
                    System.out.println(
                            "\nInvalid option. Please choose 0, 1 or 2."
                    );
            }
        }
    }

    private void initializeApplication(
            PersistenceMode selectedMode
    ) {
        persistenceMode = selectedMode;

        RepositoryFactory repositoryFactory =
                RepositoryFactory.getFactory(
                        selectedMode
                );

        RepositoryBundle repositories =
                repositoryFactory.createRepositories();

        UserRepository userRepository =
                repositories.userRepository();

        BookRepository bookRepository =
                repositories.bookRepository();

        PurchaseRepository purchaseRepository =
                repositories.purchaseRepository();

        initializeDemoDataIfNecessary(
                userRepository,
                bookRepository
        );

        userSession = new UserSession();

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

        viewProfileController =
                new ViewProfileController(
                        userSession
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

        System.out.println(
                "\nPersistence mode selected: "
                        + persistenceMode.getDisplayName()
        );

        if (persistenceMode
                == PersistenceMode.IN_MEMORY) {

            System.out.println(
                    "Data will be lost when the application closes."
            );

        } else {
            System.out.println(
                    "Data will be stored in text files."
            );
        }
    }

    private void initializeDemoDataIfNecessary(
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        if (userRepository.findAll().isEmpty()) {
            loadDemoWriters(userRepository);
        }

        if (bookRepository.findAll().isEmpty()) {
            loadDemoBooks(bookRepository);
        }
    }

    private void printWelcomeMessage() {
        System.out.println(
                "=================================="
        );

        System.out.println(
                "       WELCOME TO BOOKVERSE"
        );

        System.out.println(
                "=================================="
        );
    }

    private void printGuestMenu() {
        System.out.println(
                "\n------------- MENU ---------------"
        );

        System.out.println(
                "Persistence: "
                        + persistenceMode.getDisplayName()
        );

        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");

        System.out.println(
                "----------------------------------"
        );
    }

    private void handleLogin() {
        System.out.println(
                "\n---------- LOGIN ----------"
        );

        String email = readRequiredText(
                "Email: "
        );

        String password = readRequiredText(
                "Password: "
        );

        try {
            User authenticatedUser =
                    loginController.login(
                            email,
                            password
                    );

            System.out.println(
                    "\nLogin successful."
            );

            System.out.println(
                    "Welcome, "
                            + authenticatedUser.getFullName()
                            + "!"
            );

            openAuthenticatedMenu(
                    authenticatedUser
            );

        } catch (InvalidCredentialsException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void openAuthenticatedMenu(
            User authenticatedUser
    ) {
        if (authenticatedUser instanceof Reader reader) {
            openReaderMenu(reader);
            return;
        }

        if (authenticatedUser instanceof Writer writer) {
            openWriterMenu(writer);
            return;
        }

        showError(
                "Unsupported user role."
        );

        loginController.logout();
    }

    private void openReaderMenu(
            Reader reader
    ) {
        boolean loggedIn = true;

        while (loggedIn
                && userSession.isAuthenticated()) {

            printReaderMenu(reader);

            int choice = readInteger(
                    "Choose an option: "
            );

            switch (choice) {
                case 1 -> handleBrowseBooks();
                case 2 -> handleSearchBooks();
                case 3 -> handleViewBookDetails();
                case 4 -> handlePurchaseBook();
                case 5 -> handleViewPurchasedBooks();
                case 6 -> handleDownloadBook();
                case 7 -> handleViewWallet();
                case 8 -> handleViewProfile();

                case 9 -> {
                    loginController.logout();
                    loggedIn = false;

                    System.out.println(
                            "\nLogout successful."
                    );
                }

                default -> System.out.println(
                        "\nInvalid option. Please choose from 1 to 9."
                );
            }
        }
    }

    private void printReaderMenu(
            Reader reader
    ) {
        System.out.println(
                "\n---------- READER MENU ----------"
        );

        System.out.println(
                "Logged in as: "
                        + reader.getFullName()
        );

        System.out.println(
                "Persistence: "
                        + persistenceMode.getDisplayName()
        );

        System.out.println("1. Browse books");
        System.out.println("2. Search books");
        System.out.println("3. View book details");
        System.out.println("4. Purchase book");
        System.out.println("5. My library");
        System.out.println("6. Download book");
        System.out.println("7. Wallet");
        System.out.println("8. Profile");
        System.out.println("9. Logout");

        System.out.println(
                "---------------------------------"
        );
    }

    private void openWriterMenu(
            Writer writer
    ) {
        boolean loggedIn = true;

        while (loggedIn
                && userSession.isAuthenticated()) {

            printWriterMenu(writer);

            int choice = readInteger(
                    "Choose an option: "
            );

            switch (choice) {
                case 1 -> handleBrowseBooks();
                case 2 -> handleSearchBooks();
                case 3 -> handleUploadBook();
                case 4 -> handleViewMyBooks();
                case 5 -> handleRemoveBookFromCatalog();
                case 6 -> handleViewWallet();
                case 7 -> handleViewProfile();

                case 8 -> {
                    loginController.logout();
                    loggedIn = false;

                    System.out.println(
                            "\nLogout successful."
                    );
                }

                default -> System.out.println(
                        "\nInvalid option. Please choose from 1 to 8."
                );
            }
        }
    }

    private void printWriterMenu(
            Writer writer
    ) {
        System.out.println(
                "\n---------- WRITER MENU ----------"
        );

        System.out.println(
                "Logged in as: "
                        + writer.getPenName()
        );

        System.out.println(
                "Persistence: "
                        + persistenceMode.getDisplayName()
        );

        System.out.println("1. Browse books");
        System.out.println("2. Search books");
        System.out.println("3. Upload book");
        System.out.println("4. My books");

        System.out.println(
                "5. Remove book from catalog"
        );

        System.out.println("6. Wallet");
        System.out.println("7. Profile");
        System.out.println("8. Logout");

        System.out.println(
                "---------------------------------"
        );
    }

    private void handleBrowseBooks() {
        try {
            List<Book> books =
                    browseBooksController.browseBooks();

            printBookList(
                    books,
                    "AVAILABLE BOOKS"
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleSearchBooks() {
        String query = readRequiredText(
                "\nSearch by title, description or category: "
        );

        try {
            List<Book> books =
                    searchBooksController.searchBooks(
                            query
                    );

            printBookList(
                    books,
                    "SEARCH RESULTS"
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void printBookList(
            List<Book> books,
            String heading
    ) {
        System.out.println(
                "\n---------- "
                        + heading
                        + " ----------"
        );

        if (books.isEmpty()) {
            System.out.println(
                    "No books were found."
            );

            return;
        }

        for (Book book : books) {
            System.out.println(
                    "\nID: "
                            + book.getId()
            );

            System.out.println(
                    "Title: "
                            + book.getTitle()
            );

            System.out.println(
                    "Category: "
                            + formatCategory(book)
            );

            System.out.println(
                    "Audience: "
                            + (
                            book.isAdultsOnly()
                                    ? "18+"
                                    : "Everyone"
                    )
            );

            System.out.println(
                    "Price: $"
                            + book.getPrice()
            );

            System.out.println(
                    "---------------------------------"
            );
        }
    }

    private void handleViewBookDetails() {
        String bookId = readRequiredText(
                "\nEnter book ID: "
        );

        try {
            ViewBookDetailsController.BookDetails details =
                    viewBookDetailsController
                            .viewBookDetails(bookId);

            Book book = details.book();

            System.out.println(
                    "\n---------- BOOK DETAILS ----------"
            );

            System.out.println(
                    "ID: "
                            + book.getId()
            );

            System.out.println(
                    "Title: "
                            + book.getTitle()
            );

            System.out.println(
                    "Writer: "
                            + details.writerPenName()
            );

            System.out.println(
                    "Description: "
                            + book.getDescription()
            );

            System.out.println(
                    "Category: "
                            + formatCategory(book)
            );

            System.out.println(
                    "Audience: "
                            + (
                            book.isAdultsOnly()
                                    ? "18+"
                                    : "Everyone"
                    )
            );

            System.out.println(
                    "Price: $"
                            + book.getPrice()
            );

            System.out.println(
                    "Availability: "
                            + (
                            book.isAvailable()
                                    ? "Available"
                                    : "Removed"
                    )
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handlePurchaseBook() {
        String bookId = readRequiredText(
                "\nEnter the ID of the book to purchase: "
        );

        try {
            Purchase purchase =
                    purchaseBookController
                            .purchaseBook(bookId);

            System.out.println(
                    "\nBook purchased successfully."
            );

            System.out.println(
                    "Purchase date: "
                            + purchase.getPurchaseDate()
                            .format(DATE_TIME_FORMATTER)
            );

            ViewWalletController.WalletDetails wallet =
                    viewWalletController.viewWallet();

            System.out.println(
                    "Remaining balance: $"
                            + wallet.balance()
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleViewPurchasedBooks() {
        try {
            List<ViewPurchasedBooksController.PurchasedBookItem>
                    purchasedBooks =
                    viewPurchasedBooksController
                            .viewPurchasedBooks();

            System.out.println(
                    "\n---------- MY LIBRARY ----------"
            );

            if (purchasedBooks.isEmpty()) {
                System.out.println(
                        "You have not purchased any books yet."
                );

                return;
            }

            for (ViewPurchasedBooksController.PurchasedBookItem
                    item : purchasedBooks) {

                Book book = item.book();
                Purchase purchase = item.purchase();

                System.out.println(
                        "\nID: "
                                + book.getId()
                );

                System.out.println(
                        "Title: "
                                + book.getTitle()
                );

                System.out.println(
                        "Category: "
                                + formatCategory(book)
                );

                System.out.println(
                        "Purchased on: "
                                + purchase.getPurchaseDate()
                                .format(DATE_TIME_FORMATTER)
                );

                System.out.println(
                        "Download available: Yes"
                );

                System.out.println(
                        "---------------------------------"
                );
            }

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleDownloadBook() {
        String bookId = readRequiredText(
                "\nEnter purchased book ID: "
        );

        String destinationText =
                readRequiredText(
                        "Destination file path, including file name: "
                );

        try {
            Path destinationPath =
                    Path.of(destinationText);

            Path downloadedFile =
                    downloadBookController.downloadBook(
                            bookId,
                            destinationPath
                    );

            System.out.println(
                    "\nBook downloaded successfully."
            );

            System.out.println(
                    "Saved to: "
                            + downloadedFile.toAbsolutePath()
            );

        } catch (InvalidPathException exception) {
            showError(
                    "The destination path is invalid."
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleUploadBook() {
        System.out.println(
                "\n---------- UPLOAD BOOK ----------"
        );

        String title = readRequiredText(
                "Title: "
        );

        String description = readRequiredText(
                "Description: "
        );

        String price = readRequiredText(
                "Price: $"
        );

        Audience audience =
                readAudience();

        Category category =
                readCategory();

        String pdfPathText = readRequiredText(
                "PDF file path: "
        );

        String coverPathText = readOptionalText(
                "Cover image path (optional): "
        );

        try {
            Path pdfPath =
                    Path.of(pdfPathText);

            Path coverPath =
                    coverPathText.isBlank()
                            ? null
                            : Path.of(coverPathText);

            Book uploadedBook =
                    uploadBookController.uploadBook(
                            title,
                            description,
                            price,
                            audience,
                            category,
                            pdfPath,
                            coverPath
                    );

            System.out.println(
                    "\nBook uploaded successfully."
            );

            System.out.println(
                    "Book ID: "
                            + uploadedBook.getId()
            );

            System.out.println(
                    "Title: "
                            + uploadedBook.getTitle()
            );

            System.out.println(
                    "Status: Available"
            );

        } catch (InvalidPathException exception) {
            showError(
                    "The selected file path is invalid."
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private Audience readAudience() {
        while (true) {
            System.out.println(
                    "\nChoose audience:"
            );

            System.out.println(
                    "1. Everyone"
            );

            System.out.println(
                    "2. Adults only (18+)"
            );

            int choice = readInteger(
                    "Choose an option: "
            );

            switch (choice) {
                case 1:
                    return Audience.EVERYONE;

                case 2:
                    return Audience.ADULTS_ONLY;

                default:
                    System.out.println(
                            "Invalid audience option."
                    );
            }
        }
    }

    private Category readCategory() {
        Category[] categories =
                Category.values();

        while (true) {
            System.out.println(
                    "\nChoose category:"
            );

            for (int index = 0;
                 index < categories.length;
                 index++) {

                System.out.println(
                        (index + 1)
                                + ". "
                                + formatCategory(
                                categories[index]
                        )
                );
            }

            int choice = readInteger(
                    "Choose an option: "
            );

            if (choice >= 1
                    && choice <= categories.length) {

                return categories[
                        choice - 1
                        ];
            }

            System.out.println(
                    "Invalid category option."
            );
        }
    }

    private void handleViewMyBooks() {
        try {
            List<Book> books =
                    viewMyBooksController
                            .viewMyBooks();

            System.out.println(
                    "\n---------- MY BOOKS ----------"
            );

            if (books.isEmpty()) {
                System.out.println(
                        "You have not uploaded any books yet."
                );

                return;
            }

            for (Book book : books) {
                System.out.println(
                        "\nID: "
                                + book.getId()
                );

                System.out.println(
                        "Title: "
                                + book.getTitle()
                );

                System.out.println(
                        "Category: "
                                + formatCategory(book)
                );

                System.out.println(
                        "Audience: "
                                + (
                                book.isAdultsOnly()
                                        ? "18+"
                                        : "Everyone"
                        )
                );

                System.out.println(
                        "Price: $"
                                + book.getPrice()
                );

                System.out.println(
                        "Status: "
                                + (
                                book.isAvailable()
                                        ? "Available"
                                        : "Removed"
                        )
                );

                System.out.println(
                        "---------------------------------"
                );
            }

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleRemoveBookFromCatalog() {
        try {
            List<Book> books =
                    viewMyBooksController
                            .viewMyBooks();

            if (books.isEmpty()) {
                System.out.println(
                        "\nYou have not uploaded any books yet."
                );

                return;
            }

            System.out.println(
                    "\n---------- REMOVE BOOK ----------"
            );

            for (Book book : books) {
                System.out.println(
                        book.getId()
                                + " | "
                                + book.getTitle()
                                + " | "
                                + (
                                book.isAvailable()
                                        ? "Available"
                                        : "Removed"
                        )
                );
            }

            String bookId = readRequiredText(
                    "\nEnter the ID of the book to remove: "
            );

            Book selectedBook =
                    books.stream()
                            .filter(book ->
                                    book.getId()
                                            .equals(bookId)
                            )
                            .findFirst()
                            .orElse(null);

            if (selectedBook == null) {
                showError(
                        "The selected book does not belong to you."
                );

                return;
            }

            if (!selectedBook.isAvailable()) {
                System.out.println(
                        "\nThis book has already been removed."
                );

                return;
            }

            String confirmation =
                    readRequiredText(
                            "Type YES to confirm removal: "
                    );

            if (!confirmation.equalsIgnoreCase("YES")) {
                System.out.println(
                        "\nBook removal cancelled."
                );

                return;
            }

            Book removedBook =
                    removeBookController
                            .removeFromCatalog(bookId);

            System.out.println(
                    "\nBook removed from the catalog successfully."
            );

            System.out.println(
                    "Title: "
                            + removedBook.getTitle()
            );

            System.out.println(
                    "Status: Removed"
            );

            System.out.println(
                    "Previous buyers will keep access to the book."
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleViewWallet() {
        try {
            ViewWalletController.WalletDetails details =
                    viewWalletController.viewWallet();

            System.out.println(
                    "\n---------- WALLET ----------"
            );

            System.out.println(
                    "Owner: "
                            + details.fullName()
            );

            System.out.println(
                    "Role: "
                            + details.role()
            );

            System.out.println(
                    "Current balance: $"
                            + details.balance()
            );

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleViewProfile() {
        try {
            ViewProfileController.ProfileDetails details =
                    viewProfileController.viewProfile();

            System.out.println(
                    "\n---------- PROFILE ----------"
            );

            if (details instanceof
                    ViewProfileController.ReaderProfileDetails reader) {

                System.out.println(
                        "Full name: "
                                + reader.fullName()
                );

                System.out.println(
                        "Email: "
                                + reader.email()
                );

                System.out.println(
                        "Role: Reader"
                );

                System.out.println(
                        "Date of birth: "
                                + reader.birthDate()
                                .format(DATE_FORMATTER)
                );

                System.out.println(
                        "Account type: "
                                + reader.accountType()
                );

                return;
            }

            if (details instanceof
                    ViewProfileController.WriterProfileDetails writer) {

                System.out.println(
                        "Full name: "
                                + writer.fullName()
                );

                System.out.println(
                        "Email: "
                                + writer.email()
                );

                System.out.println(
                        "Role: Writer"
                );

                System.out.println(
                        "Pen name: "
                                + writer.penName()
                );

                System.out.println(
                        "Biography: "
                                + writer.biography()
                );

                System.out.println(
                        "Website / Social: "
                                + writer.websiteOrSocial()
                );
            }

        } catch (RuntimeException exception) {
            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleRegistration() {
        boolean choosingRole = true;

        while (choosingRole) {
            System.out.println(
                    "\n------- REGISTRATION -------"
            );

            System.out.println(
                    "Persistence: "
                            + persistenceMode.getDisplayName()
            );

            System.out.println(
                    "Choose account type:"
            );

            System.out.println("1. Reader");
            System.out.println("2. Writer");
            System.out.println("0. Back");

            int roleChoice = readInteger(
                    "Choose an option: "
            );

            switch (roleChoice) {
                case 1 -> {
                    handleReaderRegistration();
                    choosingRole = false;
                }

                case 2 -> {
                    handleWriterRegistration();
                    choosingRole = false;
                }

                case 0 -> {
                    choosingRole = false;

                    System.out.println(
                            "\nReturning to the main menu."
                    );
                }

                default -> System.out.println(
                        "\nInvalid account type."
                );
            }
        }
    }

    private void handleReaderRegistration() {
        System.out.println(
                "\n---- READER REGISTRATION ----"
        );

        String fullName = readRequiredText(
                "Full name: "
        );

        String email = readRequiredText(
                "Email: "
        );

        String password = readRequiredText(
                "Password: "
        );

        String confirmPassword = readRequiredText(
                "Confirm password: "
        );

        LocalDate birthDate =
                readBirthDate();

        try {
            Reader registeredReader =
                    registerController.registerReader(
                            fullName,
                            email,
                            password,
                            confirmPassword,
                            birthDate
                    );

            System.out.println(
                    "\nReader account created successfully."
            );

            System.out.println(
                    "Full name: "
                            + registeredReader.getFullName()
            );

            System.out.println(
                    "Email: "
                            + registeredReader.getEmail()
            );

            System.out.println(
                    "Date of birth: "
                            + registeredReader
                            .getBirthDate()
                            .format(DATE_FORMATTER)
            );

            System.out.println(
                    "You can now log in."
            );

        } catch (InvalidRegistrationDataException
                 | UserAlreadyExistsException
                 | IllegalArgumentException exception) {

            showError(
                    exception.getMessage()
            );
        }
    }

    private void handleWriterRegistration() {
        System.out.println(
                "\n---- WRITER REGISTRATION ----"
        );

        String fullName = readRequiredText(
                "Full name: "
        );

        String email = readRequiredText(
                "Email: "
        );

        String password = readRequiredText(
                "Password: "
        );

        String confirmPassword = readRequiredText(
                "Confirm password: "
        );

        String penName = readRequiredText(
                "Pen name: "
        );

        String biography = readRequiredText(
                "Biography: "
        );

        String websiteOrSocial = readOptionalText(
                "Website or social media (optional): "
        );

        try {
            Writer registeredWriter =
                    registerController.registerWriter(
                            fullName,
                            email,
                            password,
                            confirmPassword,
                            penName,
                            biography,
                            websiteOrSocial
                    );

            System.out.println(
                    "\nWriter account created successfully."
            );

            System.out.println(
                    "Full name: "
                            + registeredWriter.getFullName()
            );

            System.out.println(
                    "Email: "
                            + registeredWriter.getEmail()
            );

            System.out.println(
                    "Pen name: "
                            + registeredWriter.getPenName()
            );

            System.out.println(
                    "You can now log in."
            );

        } catch (InvalidRegistrationDataException
                 | UserAlreadyExistsException
                 | IllegalArgumentException exception) {

            showError(
                    exception.getMessage()
            );
        }
    }

    private LocalDate readBirthDate() {
        while (true) {
            String input = readRequiredText(
                    "Birth date (YYYY-MM-DD or DD/MM/YYYY): "
            );

            try {
                if (input.contains("/")) {
                    return LocalDate.parse(
                            input,
                            DATE_FORMATTER
                    );
                }

                return LocalDate.parse(input);

            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Invalid date. Use YYYY-MM-DD "
                                + "or DD/MM/YYYY."
                );
            }
        }
    }

    private String formatCategory(
            Book book
    ) {
        return formatCategory(
                book.getCategory()
        );
    }

    private String formatCategory(
            Category category
    ) {
        return category
                .name()
                .replace("_", " ");
    }

    private int readInteger(
            String message
    ) {
        while (true) {
            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {
                return Integer.parseInt(
                        input
                );

            } catch (NumberFormatException exception) {
                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    private String readRequiredText(
            String message
    ) {
        while (true) {
            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (!input.isBlank()) {
                return input;
            }

            System.out.println(
                    "This field cannot be empty."
            );
        }
    }

    private String readOptionalText(
            String message
    ) {
        System.out.print(message);

        return scanner.nextLine().trim();
    }

    private void showError(
            String message
    ) {
        System.out.println(
                "\nError: "
                        + (
                        message == null
                                ? "The operation could not be completed."
                                : message
                )
        );
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
                        "Harper Lee"
                )
        );
    }

    private Writer createDemoWriter(
            String id,
            String penName
    ) {
        String emailName =
                id.replace("-", "");

        return new Writer(
                id,
                penName,
                emailName + "@bookverse.demo",
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
                        "A classic novel about love, society and personal growth.",
                        new BigDecimal("12.00"),
                        Audience.EVERYONE,
                        Category.ROMANCE,
                        "data/books/pride-and-prejudice.pdf",
                        "covers/pride-and-prejudice.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-2",
                        "The Great Gatsby",
                        "writer-2",
                        "A story of ambition, wealth and lost love.",
                        new BigDecimal("15.00"),
                        Audience.EVERYONE,
                        Category.FICTION,
                        "data/books/the-great-gatsby.pdf",
                        "covers/the-great-gatsby.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-3",
                        "1984",
                        "writer-3",
                        "A dystopian novel about surveillance and totalitarian power.",
                        new BigDecimal("18.00"),
                        Audience.ADULTS_ONLY,
                        Category.FICTION,
                        "data/books/1984.pdf",
                        "covers/1984.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-4",
                        "Harry Potter and the Sorcerer's Stone",
                        "writer-4",
                        "A young wizard discovers a magical world and begins his journey.",
                        new BigDecimal("14.00"),
                        Audience.EVERYONE,
                        Category.FANTASY,
                        "data/books/harry-potter.pdf",
                        "covers/harry-potter.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-5",
                        "Dune",
                        "writer-5",
                        "A science-fiction epic set on the desert planet Arrakis.",
                        new BigDecimal("20.00"),
                        Audience.EVERYONE,
                        Category.SCIENCE_FICTION,
                        "data/books/dune.pdf",
                        "covers/dune.jpg"
                )
        );

        bookRepository.save(
                new Book(
                        "book-6",
                        "To Kill a Mockingbird",
                        "writer-6",
                        "A novel about justice, prejudice and moral courage.",
                        new BigDecimal("16.00"),
                        Audience.EVERYONE,
                        Category.FICTION,
                        "data/books/to-kill-a-mockingbird.pdf",
                        "covers/to-kill-a-mockingbird.jpg"
                )
        );
    }
}