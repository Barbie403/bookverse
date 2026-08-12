package it.bookverse.navigation;


import it.bookverse.ApplicationContext;
import it.bookverse.BookVerseApplication;
import it.bookverse.boundary.BookDetailsBoundary;
import it.bookverse.boundary.LibraryBoundary;
import it.bookverse.boundary.LoginBoundary;
import it.bookverse.boundary.MyBooksBoundary;
import it.bookverse.boundary.ProfileBoundary;
import it.bookverse.boundary.PurchasedBooksBoundary;
import it.bookverse.boundary.RegistrationBoundary;
import it.bookverse.boundary.UploadBookBoundary;
import it.bookverse.boundary.WalletBoundary;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneManager {

    private static final double WINDOW_WIDTH =
            1200;

    private static final double WINDOW_HEIGHT =
            780;

    private static final String STYLESHEET_PATH =
            "/it/bookverse/bookverse.css";

    private final Stage stage;
    private final ApplicationContext applicationContext;

    private boolean windowInitialized;

    public SceneManager(
            Stage stage,
            ApplicationContext applicationContext
    ) {
        this.stage = Objects.requireNonNull(
                stage,
                "Stage cannot be null."
        );

        this.applicationContext =
                Objects.requireNonNull(
                        applicationContext,
                        "Application context cannot be null."
                );
    }

    public void showLogin() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/login-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == LoginBoundary.class) {
                return new LoginBoundary(
                        applicationContext,
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - Login"
        );
    }

    public void showRegistration() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/registration-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass
                    == RegistrationBoundary.class) {

                return new RegistrationBoundary(
                        applicationContext
                                .getRegisterController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - Registration"
        );
    }

    public void showLibrary() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/library-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == LibraryBoundary.class) {
                return new LibraryBoundary(
                        applicationContext
                                .getBrowseBooksController(),
                        applicationContext
                                .getSearchBooksController(),
                        applicationContext
                                .getUploadBookController(),
                        applicationContext
                                .getViewWalletController(),
                        applicationContext
                                .getLoginController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - Library"
        );
    }

    public void showUploadBook() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/upload-book-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass
                    == UploadBookBoundary.class) {

                return new UploadBookBoundary(
                        applicationContext
                                .getUploadBookController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - Upload Book"
        );
    }

    public void showMyBooks() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/my-books-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == MyBooksBoundary.class) {
                return new MyBooksBoundary(
                        applicationContext.getViewMyBooksController(),
                        applicationContext.getRemoveBookController(),
                        applicationContext.getViewWalletController(),
                        applicationContext.getLoginController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - My Books"
        );
    }

    public void showWallet() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/wallet-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == WalletBoundary.class) {
                return new WalletBoundary(
                        applicationContext
                                .getViewWalletController(),
                        applicationContext
                                .getTopUpWalletController(),
                        applicationContext
                                .getLoginController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - Wallet"
        );
    }

    public void showProfile() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/profile-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == ProfileBoundary.class) {
                return new ProfileBoundary(
                        applicationContext
                                .getViewProfileController(),
                        applicationContext
                                .getViewWalletController(),
                        applicationContext
                                .getLoginController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - My Profile"
        );
    }

    public void showBookDetails(
            String bookId
    ) {
        showBookDetails(
                bookId,
                false
        );
    }

    public void showPurchasedBookDetails(
            String bookId
    ) {
        showBookDetails(
                bookId,
                true
        );
    }

    private void showBookDetails(
            String bookId,
            boolean openedFromPurchasedBooks
    ) {
        FXMLLoader loader = createLoader(
                "/it/bookverse/book-details-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass
                    == BookDetailsBoundary.class) {

                return new BookDetailsBoundary(
                        applicationContext
                                .getViewBookDetailsController(),
                        applicationContext
                                .getPurchaseBookController(),
                        this,
                        bookId,
                        openedFromPurchasedBooks
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - Book Details"
        );
    }

    public void showPurchasedBooks() {
        FXMLLoader loader = createLoader(
                "/it/bookverse/purchased-books-view.fxml"
        );

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass
                    == PurchasedBooksBoundary.class) {

                return new PurchasedBooksBoundary(
                        applicationContext
                                .getViewPurchasedBooksController(),
                        applicationContext
                                .getDownloadBookController(),
                        applicationContext
                                .getViewWalletController(),
                        applicationContext
                                .getLoginController(),
                        this
                );
            }

            return createDefaultController(
                    controllerClass
            );
        });

        showScene(
                loader,
                "BookVerse - My Library"
        );
    }

    private FXMLLoader createLoader(
            String resourcePath
    ) {
        URL resource =
                BookVerseApplication.class.getResource(
                        resourcePath
                );

        if (resource == null) {
            throw new IllegalStateException(
                    "FXML resource not found: "
                            + resourcePath
            );
        }

        return new FXMLLoader(resource);
    }

    private void showScene(
            FXMLLoader loader,
            String title
    ) {
        try {
            Parent root =
                    loader.load();

            Scene scene =
                    new Scene(root);

            applyStylesheet(scene);

            stage.setTitle(title);
            stage.setScene(scene);

            if (!windowInitialized) {
                stage.setWidth(WINDOW_WIDTH);
                stage.setHeight(WINDOW_HEIGHT);

                stage.setMinWidth(WINDOW_WIDTH);
                stage.setMinHeight(WINDOW_HEIGHT);

                stage.setResizable(true);
                stage.centerOnScreen();

                windowInitialized = true;
            }

            stage.show();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Cannot load scene: "
                            + title,
                    exception
            );
        }
    }

    private void applyStylesheet(
            Scene scene
    ) {
        URL stylesheet =
                BookVerseApplication.class.getResource(
                        STYLESHEET_PATH
                );

        if (stylesheet == null) {
            throw new IllegalStateException(
                    "Stylesheet not found: "
                            + STYLESHEET_PATH
            );
        }

        scene.getStylesheets().add(
                stylesheet.toExternalForm()
        );
    }

    private Object createDefaultController(
            Class<?> controllerClass
    ) {
        try {
            return controllerClass
                    .getDeclaredConstructor()
                    .newInstance();

        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Cannot create controller: "
                            + controllerClass.getName(),
                    exception
            );
        }
    }
}