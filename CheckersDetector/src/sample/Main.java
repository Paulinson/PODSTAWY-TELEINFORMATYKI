package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.Controllers.MainController;
import sample.Utilities.CalibrateCamera;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        VBox rootElement =  loader.load();
        rootElement.setStyle("-fx-background-color: whitesmoke;");
        Scene scene = new Scene(rootElement, 800, 600);
        primaryStage.setTitle("Checkers Detector");
        primaryStage.setScene(scene);
        MainController controller = loader.getController();
        controller.setCalibrateCamera(new CalibrateCamera());
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.load(new File("/usr/local/Cellar/opencv3/HEAD-10787c6/share/OpenCV/java/libopencv_java320.dylib").getAbsolutePath());
        launch(args);
    }
}
