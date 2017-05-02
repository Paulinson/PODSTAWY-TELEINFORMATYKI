package sample.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button startCameraButton;
    @FXML
    private Button nextMoveButton;
    @FXML
    public Button calibrateButton;

    @FXML
    private ImageView cameraView;
    @FXML
    private ImageView captureView;
    @FXML
    private ImageView checkersBoardView;

    ImageProcessing imageProcessing;

    Color firstPlayer;
    Color secondPlayer;
    Color borderEdges;

    @FXML
    protected void startCamera() {
        borderEdges = new Color(50, 32, 16, 80, 245, 245); //GREEEN
        firstPlayer = new Color(110, 50, 50, 130, 255, 255); //BLUE
        secondPlayer = new Color(20, 100, 100, 30, 255, 255); //YELLOW


        Image checkersBoardImage = new Image(new File("/Users/sot/Documents/workspace/PODSTAWY-TELEINFORMATYKI/CheckersDetector/checkersboard.png").toURI().toString());
        checkersBoardView.setImage(checkersBoardImage);
        if (!imageProcessing.cameraActive) {
            imageProcessing = new ImageProcessing();
            imageProcessing.capture.open(0);
            if (imageProcessing.capture.isOpened()) {
                imageProcessing.cameraActive = true;

                TimerTask frameGrabber = new TimerTask() {
                    @Override
                    public void run() {
                        Mat frame = imageProcessing.grabFrame();
                        Mat topView = frame;
                        Image image = mat2Image(frame);
//                        Mat circleFrame = imageProcessing.findCircles(frame, secondPlayer.getMinValues(), secondPlayer.getMaxValues());
//                        Image circleImage = mat2Image(circleFrame);
                        Image topViewImage = null;
                        if (imageProcessing.generatedPerspectiveMatrix == false) {
                            imageProcessing.findPerspectiveMatrix(frame, borderEdges);
                        } else {
                            topView = imageProcessing.topView(frame, imageProcessing.perspectiveMatrix);
                            topViewImage = mat2Image(topView);
                            captureView.setImage(topViewImage);
                        }
                        cameraView.setImage(image);
                        cameraView.setFitWidth(380);
                        cameraView.setFitHeight(400);
                        cameraView.setPreserveRatio(true);
                    }
                };
                imageProcessing.timer.schedule(frameGrabber, 0, 500);
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
    public void calibrate(){
        imageProcessing.generatedPerspectiveMatrix = false;
    }
    //<editor-fold desc="Getters and Setters">
    public Button getStartCameraButton() {
        return startCameraButton;
    }

    public void setStartCameraButton(Button startCameraButton) {
        this.startCameraButton = startCameraButton;
    }

    public Button getNextMoveButton() {
        return nextMoveButton;
    }

    public void setNextMoveButton(Button nextMoveButton) {
        this.nextMoveButton = nextMoveButton;
    }

    public ImageView getCameraView() {
        return cameraView;
    }

    public void setCameraView(ImageView cameraView) {
        this.cameraView = cameraView;
    }

    public ImageView getCaptureView() {
        return captureView;
    }

    public void setCaptureView(ImageView captureView) {
        this.captureView = captureView;
    }

    public ImageView getCheckersBoardView() {
        return checkersBoardView;
    }

    public void setCheckersBoardView(ImageView checkersBoardView) {
        this.checkersBoardView = checkersBoardView;
    }

    public ImageProcessing getImageProcessing() {
        return imageProcessing;
    }

    public void setImageProcessing(ImageProcessing imageProcessing) {
        this.imageProcessing = imageProcessing;
    }

    public Color getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Color firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Color getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(Color secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public Color getBorderEdges() {
        return borderEdges;
    }

    public void setBorderEdges(Color borderEdges) {
        this.borderEdges = borderEdges;
    }

    //</editor-fold>
}
