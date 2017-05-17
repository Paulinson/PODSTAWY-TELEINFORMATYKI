package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.Core;
import sample.Controllers.MainController;
import sample.Utilities.ImageProcessing;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        VBox rootElement = loader.load();
        rootElement.setStyle("-fx-background-color: whitesmoke;");
        Scene scene = new Scene(rootElement);
        primaryStage.setTitle("Checkers Detector");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        MainController controller = loader.getController();
        controller.imageProcessing = new ImageProcessing(controller);
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
