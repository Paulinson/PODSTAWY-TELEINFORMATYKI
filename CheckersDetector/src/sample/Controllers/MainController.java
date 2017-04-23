package sample.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Data;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import sample.Utilities.CalibrateCamera;
import sample.Utilities.CircleServices;



import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@Data
public class MainController {
    @FXML
    private Button startCameraButton;

    @FXML
    private ImageView originalFrameView;
    @FXML
    private ImageView capturedCirclesFrameView;
    @FXML
    private ImageView checkersboardFrameView;

    private CalibrateCamera calibrateCamera;
    private CircleServices circleServices = new CircleServices();

    @FXML
    protected void startCamera() {
        Image checkersBoardImage = new Image(new File("/Users/sot/Documents/workspace/PODSTAWY-TELEINFORMATYKI/CheckersDetector/checkersboard.png").toURI().toString());
        checkersboardFrameView.setImage(checkersBoardImage);
        if (!calibrateCamera.isCameraActive()) {
//            calibrateCamera.getCapture().open(0);
//
//            if (calibrateCamera.getCapture().isOpened()) {
            calibrateCamera.setCameraActive(true);

            TimerTask frameGrabber = new TimerTask() {
                @Override
                public void run() {
                    Mat frame = calibrateCamera.grabFrame();
                    Image image = mat2Image(frame);
                    Mat circleFrame = circleServices.findCircles(frame);
                    Image circleImage = mat2Image(circleFrame);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            originalFrameView.setImage(image);
                            originalFrameView.setFitWidth(380);
                            originalFrameView.setFitHeight(400);
                            originalFrameView.setPreserveRatio(true);

                            capturedCirclesFrameView.setImage(circleImage);
                            capturedCirclesFrameView.setFitWidth(380);
                            capturedCirclesFrameView.setFitHeight(400);
                            capturedCirclesFrameView.setPreserveRatio(true);
                        }
                    });

                }
            };
            calibrateCamera.getTimer().schedule(frameGrabber, 0, 33);
            this.startCameraButton.setText("Stop Camera");
//            } else {
//                System.err.println("Impossible to open the camera connection...");
//            }
        } else {
            calibrateCamera.setCameraActive(false);
            startCameraButton.setText("Start Camera");
            if (calibrateCamera.getTimer() != null) {
                calibrateCamera.getTimer().cancel();
                calibrateCamera.setTimer(null);
            }
            calibrateCamera.getCapture().release();
            originalFrameView.setImage(null);
            capturedCirclesFrameView.setImage(null);
        }
    }

    public Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    @FXML
    public void takeSnapshot()
    {
        VideoCapture camera = new VideoCapture(0);

        if (!calibrateCamera.isCameraActive()) {
            System.out.println("Error");
        } else {
            Mat frame = new Mat();

            while (true) {
                if (camera.read(frame)) {
                    Imgcodecs.imwrite("test.jpg", frame);
                    break;
                }
            }
        }
        camera.release();
    }

    public void saveImage(BufferedImage img) {
        try {
            File outputfile = new File("Images/new.png");
            ImageIO.write(img, "png", outputfile);
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }

}
