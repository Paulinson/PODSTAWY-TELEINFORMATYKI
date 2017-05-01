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
    private Button nextMoveButton;


    @FXML
    private ImageView cameraView;
    @FXML
    private ImageView captureView;
    @FXML
    private ImageView checkersBoardView;

    private CalibrateCamera calibrateCamera;
    private CircleServices circleServices = new CircleServices();

    @FXML
    protected void startCamera() {
        Image checkersBoardImage = new Image(new File("/Users/sot/Documents/workspace/PODSTAWY-TELEINFORMATYKI/CheckersDetector/checkersboard.png").toURI().toString());
        checkersBoardView.setImage(checkersBoardImage);
        if (!calibrateCamera.isCameraActive()) {
            calibrateCamera.getCapture().open(0);

            if (calibrateCamera.getCapture().isOpened()) {
            calibrateCamera.setCameraActive(true);

            TimerTask frameGrabber = new TimerTask() {
                @Override
                public void run() {
                    Mat frame = calibrateCamera.grabFrame();
//                    Image image = mat2Image(frame);
                    Mat circleFrame = circleServices.findCircles(frame);
                    Image circleImage = mat2Image(circleFrame);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            cameraView.setImage(circleImage);
                            cameraView.setFitWidth(380);
                            cameraView.setFitHeight(400);
                            cameraView.setPreserveRatio(true);
                        }
                    });

                }
            };
            calibrateCamera.getTimer().schedule(frameGrabber, 0, 1000);
            this.startCameraButton.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            calibrateCamera.setCameraActive(false);
            startCameraButton.setText("Start Camera");
            if (calibrateCamera.getTimer() != null) {
                calibrateCamera.getTimer().cancel();
                calibrateCamera.setTimer(null);
            }
            calibrateCamera.getCapture().release();
            cameraView.setImage(null);
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
