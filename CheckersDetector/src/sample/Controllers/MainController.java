package sample.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import sample.Utilities.Color;
import sample.Utilities.ImageProcessing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.TimerTask;

public class MainController {
    @FXML
    public Button startCameraButton;
    @FXML
    public Button nextMoveButton;
    @FXML
    public Button calibrateButton;

    @FXML
    public ImageView cameraView;
    @FXML
    public ImageView captureView;
    @FXML
    public ImageView calibrateView;
    @FXML
    public ImageView checkersBoardView;
    @FXML
    public TextArea infoTextArea;

    public ImageProcessing imageProcessing;

    Color firstPlayer = new Color(100,150,0, 140,255,255); //BLUE;
    Color secondPlayer = new Color(20, 100, 100, 30, 255, 255); //YELLOW
    Color borderEdges = new Color(50, 32, 16, 80, 245, 245); //GREEEN;
    Color currentColor;
    int gameState = 0;

    public MainController() {

    }

    @FXML
    public void startCamera() {
        Image checkersBoardImage = new Image(new File("/Users/sot/Documents/workspace/PODSTAWY-TELEINFORMATYKI/CheckersDetector/checkersboard.png").toURI().toString());
        checkersBoardView.setImage(checkersBoardImage);
        if (!imageProcessing.cameraActive) {
            imageProcessing = new ImageProcessing(MainController.this);
            imageProcessing.capture.open(0);
            if (imageProcessing.capture.isOpened()) {
                imageProcessing.cameraActive = true;

                TimerTask frameGrabber = new TimerTask() {
                    @Override
                    public void run() {
                        Mat frame = imageProcessing.grabFrame();
                        Mat topView = frame;
                        Image image = mat2Image(frame);
                        Image topViewImage = null;
                        if (imageProcessing.generatedPerspectiveMatrix == false) {
                            imageProcessing.findPerspectiveMatrix(frame, borderEdges);
                        } else {
                            topView = imageProcessing.topView(frame, imageProcessing.perspectiveMatrix);
                            topViewImage = mat2Image(topView);
                            calibrateView.setImage(topViewImage);
                            calibrateView.setFitWidth(380);
                            calibrateView.setFitHeight(400);
                            calibrateView.setPreserveRatio(true);
                        }
                        cameraView.setImage(image);
                        cameraView.setFitWidth(380);
                        cameraView.setFitHeight(400);
                        cameraView.setPreserveRatio(true);

                        if (gameState != 0) {
                            Mat circleFrame = imageProcessing.findCircles(frame, currentColor.getMinValues(), currentColor.getMaxValues(), true);
                            Image circleImage = mat2Image(circleFrame);
                            captureView.setImage(circleImage);
                            captureView.setFitWidth(380);
                            captureView.setFitHeight(400);
                            captureView.setPreserveRatio(true);
                        }
                    }
                };
                imageProcessing.timer.schedule(frameGrabber, 0, 33);
                this.startCameraButton.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            imageProcessing.cameraActive = false;
            startCameraButton.setText("Start Camera");
            if (imageProcessing.timer != null) {
                imageProcessing.timer.cancel();
                imageProcessing.timer = null;
            }
            imageProcessing.capture.release();
            cameraView.setImage(null);
            captureView.setImage(null);
        }
    }

    public Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    @FXML
    public void takeScreenshot() {
        File out = new File("screenShot0.png");
        int i = 0;
        while (out.exists()) {
            out = new File("screenShot" + i + ".png");
        }
        //    try {
        //        ImageIO.write(Image,"png",out);
        //   } catch (IOException e1) {
        //       e1.printStackTrace();
        //   }
    }

    @FXML
    public void calibrate() {
        imageProcessing.generatedPerspectiveMatrix = false;
    }

    @FXML
    public void nextMove() {
        switch (gameState) {
            case 1: {
                currentColor = firstPlayer;
                gameState = 2;
                break;
            }
            case 2: {
                currentColor = secondPlayer;
                gameState = 1;
                break;
            }
            case 0: {
                currentColor = firstPlayer;
                gameState = 1;
                break;
            }
        }
    }
}
