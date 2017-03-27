package sample.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.Utilities.CalibrateCamera;

import java.util.Timer;
import java.util.TimerTask;

public class MainController {
    @FXML
    private Button startCameraButton;

    @FXML
    private ImageView originalFrameView;

    private CalibrateCamera calibrateCamera;

    @FXML
    protected void startCamera() {
        if (!calibrateCamera.isCameraActive()) {
            calibrateCamera.getCapture().open(0);

            if (calibrateCamera.getCapture().isOpened()) {
                calibrateCamera.setCameraActive(true);

                TimerTask frameGrabber = new TimerTask() {
                    @Override
                    public void run() {
                        calibrateCamera.setCamStream(calibrateCamera.grabFrame());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                originalFrameView.setImage(calibrateCamera.getCamStream());
                                originalFrameView.setFitWidth(380);
                                originalFrameView.setFitHeight(400);
                                originalFrameView.setPreserveRatio(true);
                            }
                        });

                    }
                };
                calibrateCamera.getTimer().schedule(frameGrabber, 0, 33);
                this.startCameraButton.setText("Stop Camera");

            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            calibrateCamera.setCameraActive(false);
            if (calibrateCamera.getTimer() != null) {
                calibrateCamera.getTimer().cancel();
                calibrateCamera.setTimer(null);
            }
            calibrateCamera.getCapture().release();
            originalFrameView.setImage(null);
        }
    }

    public CalibrateCamera getCalibrateCamera() {
        return calibrateCamera;
    }

    public void setCalibrateCamera(CalibrateCamera calibrateCamera) {
        this.calibrateCamera = calibrateCamera;
    }
}
