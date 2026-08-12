package it.bookverse;
import it.bookverse.navigation.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
public class BookVerseApplication
        extends Application {
    @Override
    public void start(
            Stage stage
    ) {
        ApplicationContext applicationContext =
                new ApplicationContext();
        SceneManager sceneManager =
                new SceneManager(
                        stage,
                        applicationContext
                );
        sceneManager.showLogin();
    }
}