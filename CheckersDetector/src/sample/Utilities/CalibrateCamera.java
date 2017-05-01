package sample.Utilities;

import javafx.scene.image.Image;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

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

    public CalibrateCamera(){
        this.capture = new VideoCapture();
        this.cameraActive = false;
        this.obj = new MatOfPoint3f();
        this.imageCorners = new MatOfPoint2f();
        this.savedImage = new Mat();
        this.undistoredImage = null;
        this.imagePoints = new ArrayList<>();
        this.objectPoints = new ArrayList<>();
        this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        this.distCoeffs = new Mat();
        this.successes = 0;
        this.cameraActive = false;
        this.timer = new Timer();
    }

    public Mat grabFrame() {
        Mat frame = new Mat();

        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
//                    this.findAndDrawPoints(frame);
                }

            } catch (Exception e) {
                System.err.print("ERROR");
                e.printStackTrace();
            }
        }
//        this.findAndDrawPoints(frame);
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

    //<editor-fold desc="Getters and Setters">
    public static int getBoardsNumber() {
        return BOARDS_NUMBER;
    }

    public static int getHorizontalCorners() {
        return HORIZONTAL_CORNERS;
    }

    public static int getVerticalCorners() {
        return VERTICAL_CORNERS;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public boolean isCameraActive() {
        return cameraActive;
    }

    public void setCameraActive(boolean cameraActive) {
        this.cameraActive = cameraActive;
    }

    public Mat getSavedImage() {
        return savedImage;
    }

    public void setSavedImage(Mat savedImage) {
        this.savedImage = savedImage;
    }

    public Image getUndistoredImage() {
        return undistoredImage;
    }

    public void setUndistoredImage(Image undistoredImage) {
        this.undistoredImage = undistoredImage;
    }

    public Image getCamStream() {
        return camStream;
    }

    public void setCamStream(Image camStream) {
        this.camStream = camStream;
    }

    public List<Mat> getImagePoints() {
        return imagePoints;
    }

    public void setImagePoints(List<Mat> imagePoints) {
        this.imagePoints = imagePoints;
    }

    public List<Mat> getObjectPoints() {
        return objectPoints;
    }

    public void setObjectPoints(List<Mat> objectPoints) {
        this.objectPoints = objectPoints;
    }

    public MatOfPoint3f getObj() {
        return obj;
    }

    public void setObj(MatOfPoint3f obj) {
        this.obj = obj;
    }

    public MatOfPoint2f getImageCorners() {
        return imageCorners;
    }

    public void setImageCorners(MatOfPoint2f imageCorners) {
        this.imageCorners = imageCorners;
    }

    public int getSuccesses() {
        return successes;
    }

    public void setSuccesses(int successes) {
        this.successes = successes;
    }

    public Mat getIntrinsic() {
        return intrinsic;
    }

    public void setIntrinsic(Mat intrinsic) {
        this.intrinsic = intrinsic;
    }

    public Mat getDistCoeffs() {
        return distCoeffs;
    }

    public void setDistCoeffs(Mat distCoeffs) {
        this.distCoeffs = distCoeffs;
    }

    public VideoCapture getCapture() {
        return capture;
    }

    public void setCapture(VideoCapture capture) {
        this.capture = capture;
    }

    public CircleServices getCircleServices() {
        return circleServices;
    }

    public void setCircleServices(CircleServices circleServices) {
        this.circleServices = circleServices;
    }
    //</editor-fold>
}