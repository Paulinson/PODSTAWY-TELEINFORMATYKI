package sample.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import sample.Utilities.CalibrateCamera;
import sample.Utilities.CircleServices;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

public class MainController {
    @FXML
    private Button startCameraButton;

    @FXML
    private Button takeScreenshot;


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
    public void takeScreenshot()
    {
        File out=new File("screenShot0.png");
        int i=0;
        while (out.exists()){
            out=new File("screenShot"+i+".png");
        }
     //    try {
     //        ImageIO.write(Image,"png",out);
     //   } catch (IOException e1) {
     //       e1.printStackTrace();
     //   }
    }


    //<editor-fold desc="Getters and Setters">
    public Button getStartCameraButton() {
        return startCameraButton;
    }

    public Button getTakeScreenshot() { return takeScreenshot; }

    public void setStartCameraButton(Button startCameraButton) {
        this.startCameraButton = startCameraButton;
    }

    public void setTakeScreenshot(Button takeScreenshot) {this.takeScreenshot = takeScreenshot;}

    public ImageView getOriginalFrameView() {
        return originalFrameView;
    }

    public void setOriginalFrameView(ImageView originalFrameView) {
        this.originalFrameView = originalFrameView;
    }

    public ImageView getCapturedCirclesFrameView() {
        return capturedCirclesFrameView;
    }

    public void setCapturedCirclesFrameView(ImageView capturedCirclesFrameView) {
        this.capturedCirclesFrameView = capturedCirclesFrameView;
    }

    public ImageView getCheckersboardFrameView() {
        return checkersboardFrameView;
    }

    public void setCheckersboardFrameView(ImageView checkersboardFrameView) {
        this.checkersboardFrameView = checkersboardFrameView;
    }

    public CalibrateCamera getCalibrateCamera() {
        return calibrateCamera;
    }

    public void setCalibrateCamera(CalibrateCamera calibrateCamera) {
        this.calibrateCamera = calibrateCamera;
    }

    public CircleServices getCircleServices() {
        return circleServices;
    }

    public void setCircleServices(CircleServices circleServices) {
        this.circleServices = circleServices;
    }
    //</editor-fold>
}
