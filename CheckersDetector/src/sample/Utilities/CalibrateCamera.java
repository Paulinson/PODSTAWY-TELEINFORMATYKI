package sample.Utilities;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalibrateCamera {

    //<editor-fold desc="Static">
    private static final int BOARDS_NUMBER = 20;
    private static final int HORIZONTAL_CORNERS = 9;
    private static final int VERTICAL_CORNERS = 6;
    //</editor-fold>
    //<editor-fold desc="Variables">
    private Timer timer;
    private boolean cameraActive;
    private Mat savedImage;
    private Image undistoredImage, camStream;
    private List<Mat> imagePoints;
    private List<Mat> objectPoints;
    private MatOfPoint3f obj;
    private MatOfPoint2f imageCorners;
    private int successes;
    private Mat intrinsic;
    private Mat distCoeffs;
    private VideoCapture capture;
    private CircleServices circleServices;
    //</editor-fold>

    public Mat grabFrame() {
        Mat frame = new Mat();

        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
                    this.findAndDrawPoints(frame);
                }

            } catch (Exception e) {
                System.err.print("ERROR");
                e.printStackTrace();
            }
        }

        return frame;
    }

    private void findAndDrawPoints(Mat frame) {
        Mat grayImage = new Mat();

        if (this.successes < this.BOARDS_NUMBER) {
            Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
            Size boardSize = new Size(this.HORIZONTAL_CORNERS, this.VERTICAL_CORNERS);
            boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
            if (found) {
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
                grayImage.copyTo(this.savedImage);
                Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);
            }
        }
    }
}